package br.com.docket.cartorios.service;

import br.com.docket.cartorios.dto.CartorioRequest;
import br.com.docket.cartorios.mapper.CartorioMapper;
import br.com.docket.cartorios.mapper.CertidaoMapper;
import br.com.docket.cartorios.model.Cartorio;
import br.com.docket.cartorios.model.Certidao;
import br.com.docket.cartorios.repository.CartorioRepository;
import br.com.docket.cartorios.repository.CertidaoRepository;
import br.com.docket.cartorios.shared.error.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartorioServiceTest {

    @Mock
    private CartorioRepository cartorioRepository;

    @Mock
    private CertidaoRepository certidaoRepository;

    @Mock
    private CartorioMapper cartorioMapper;

    @Mock
    private CertidaoMapper certidaoMapper;

    @InjectMocks
    private CartorioService cartorioService;

    @Test
    void shouldLinkCertidoesWhenCreating() {
        Certidao nascimento = certidao(1L, "Certidão de Nascimento");
        Certidao casamento = certidao(2L, "Certidão de Casamento");

        when(cartorioMapper.fromRequest(any(CartorioRequest.class))).thenReturn(new Cartorio());
        when(certidaoRepository.findById(1L)).thenReturn(Optional.of(nascimento));
        when(certidaoRepository.findById(2L)).thenReturn(Optional.of(casamento));

        cartorioService.create(request(Set.of(1L, 2L)));

        ArgumentCaptor<Cartorio> captor = ArgumentCaptor.forClass(Cartorio.class);
        verify(cartorioRepository).save(captor.capture());

        assertThat(captor.getValue().getCertidoes())
                .extracting(Certidao::getNome)
                .containsExactlyInAnyOrder("Certidão de Nascimento", "Certidão de Casamento");
    }

    @Test
    void shouldFailWhenCertidaoDoesNotExistOnCreate() {
        when(cartorioMapper.fromRequest(any(CartorioRequest.class))).thenReturn(new Cartorio());
        when(certidaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartorioService.create(request(Set.of(99L))))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Certidão");

        verify(cartorioRepository, never()).save(any(Cartorio.class));
    }

    @Test
    void shouldReplaceCertidoesWhenUpdating() {
        Certidao nascimento = certidao(1L, "Certidão de Nascimento");
        Certidao obito = certidao(3L, "Certidão de Óbito");

        Cartorio existente = new Cartorio();
        existente.addCertidao(nascimento);

        when(cartorioRepository.findById(10L)).thenReturn(Optional.of(existente));
        when(certidaoRepository.findById(3L)).thenReturn(Optional.of(obito));

        cartorioService.update(10L, request(Set.of(3L)));

        assertThat(existente.getCertidoes())
                .extracting(Certidao::getNome)
                .containsExactly("Certidão de Óbito");
    }

    @Test
    void shouldFailWhenCartorioDoesNotExist() {
        when(cartorioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartorioService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void shouldClearCertidoesBeforeDeleting() {
        Certidao nascimento = certidao(1L, "Certidão de Nascimento");

        Cartorio existente = new Cartorio();
        existente.addCertidao(nascimento);

        when(cartorioRepository.findById(10L)).thenReturn(Optional.of(existente));

        cartorioService.delete(10L);

        assertThat(existente.getCertidoes()).isEmpty();
        assertThat(nascimento.getCartorios()).isEmpty();
        verify(cartorioRepository).delete(existente);
    }

    private CartorioRequest request(Set<Long> certidoes) {
        return new CartorioRequest("1º Ofício de Registro Civil", "01302000", "Rua da Consolação", "100",
                null, "Consolação", "São Paulo", "SP", certidoes);
    }

    private Certidao certidao(Long id, String nome) {
        Certidao certidao = new Certidao(nome);
        certidao.setId(id);
        return certidao;
    }
}
