package br.com.docket.cartorios.repository;

import br.com.docket.cartorios.model.Cartorio;
import br.com.docket.cartorios.model.Certidao;
import br.com.docket.cartorios.shared.JpaAuditingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class CartorioRepositoryTest {

    @Autowired
    private CartorioRepository cartorioRepository;

    @Autowired
    private CertidaoRepository certidaoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldLoadCertidoesWhenFindingById() {
        Certidao certidao = certidaoRepository.save(new Certidao("Certidão de Teste"));

        Cartorio cartorio = buildCartorio("Cartório Alfa de Teste");
        cartorio.addCertidao(certidao);
        Long id = cartorioRepository.save(cartorio).getId();

        entityManager.flush();
        entityManager.clear();

        Optional<Cartorio> encontrado = cartorioRepository.findById(id);

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getCertidoes())
                .extracting(Certidao::getNome)
                .containsExactly("Certidão de Teste");
    }

    @Test
    void shouldFillCreatedAtOnPersist() {
        Cartorio cartorio = cartorioRepository.save(buildCartorio("Cartório Beta de Teste"));

        assertThat(cartorio.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFilterByNomeIgnoringCase() {
        cartorioRepository.save(buildCartorio("Cartório Gama de Teste"));

        Page<Cartorio> page = cartorioRepository.findByNomeContainingIgnoreCase("gama de teste", PageRequest.of(0, 10));

        assertThat(page.getContent())
                .extracting(Cartorio::getNome)
                .containsExactly("Cartório Gama de Teste");
    }

    @Test
    void shouldReturnEmptyPageWhenNomeDoesNotMatch() {
        cartorioRepository.save(buildCartorio("Cartório Delta de Teste"));

        Page<Cartorio> page = cartorioRepository.findByNomeContainingIgnoreCase("nome inexistente", PageRequest.of(0, 10));

        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isZero();
    }

    private Cartorio buildCartorio(String nome) {
        Cartorio cartorio = new Cartorio();
        cartorio.setNome(nome);
        cartorio.setCep("01302000");
        cartorio.setRua("Rua da Consolação");
        cartorio.setNumero("100");
        cartorio.setBairro("Consolação");
        cartorio.setCidade("São Paulo");
        cartorio.setUf("SP");
        return cartorio;
    }
}
