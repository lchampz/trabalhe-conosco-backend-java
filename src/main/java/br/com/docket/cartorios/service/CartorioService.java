package br.com.docket.cartorios.service;

import br.com.docket.cartorios.dto.CartorioRequest;
import br.com.docket.cartorios.dto.CartorioResponse;
import br.com.docket.cartorios.dto.CertidaoResponse;
import br.com.docket.cartorios.mapper.CartorioMapper;
import br.com.docket.cartorios.mapper.CertidaoMapper;
import br.com.docket.cartorios.model.Cartorio;
import br.com.docket.cartorios.model.Certidao;
import br.com.docket.cartorios.repository.CartorioRepository;
import br.com.docket.cartorios.repository.CertidaoRepository;
import br.com.docket.cartorios.shared.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartorioService {

    private final CartorioRepository cartorioRepository;
    private final CartorioMapper cartorioMapper;
    private final CertidaoMapper certidaoMapper;
    private final CertidaoRepository certidaoRepository;

    @Transactional(readOnly = true)
    public Page<CartorioResponse> list(String nome, Pageable pageable) {
        Page<Cartorio> page = StringUtils.hasText(nome)
                ? cartorioRepository.findByNomeContainingIgnoreCase(nome, pageable)
                : cartorioRepository.findAll(pageable);
        return page.map(cartorioMapper::fromCartorio);
    }

    @Transactional(readOnly = true)
    public CartorioResponse findById(Long id) {
        return cartorioMapper.fromCartorio(cartorioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cartório", id)));
    }

    @Transactional(readOnly = true)
    public List<CertidaoResponse> listCertidoes(Long id) {
        Cartorio cartorio = cartorioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cartório", id));
        return certidaoMapper.fromCertidao(List.copyOf(cartorio.getCertidoes()));
    }

    public CartorioResponse create(CartorioRequest request) {
        Cartorio cartorio = cartorioMapper.fromRequest(request);
        for (Long certidaoId : request.certidoes()) {
            Certidao certidao = certidaoRepository.findById(certidaoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Certidão", certidaoId));
            cartorio.addCertidao(certidao);
        }
        cartorioRepository.save(cartorio);
        return cartorioMapper.fromCartorio(cartorio);
    }

    public CartorioResponse update(Long id, CartorioRequest request) {
        Cartorio cartorio = cartorioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cartório", id));

        cartorioMapper.fromRequest(request, cartorio);

        cartorio.setCertidoes(new HashSet<>()); // limpando todo o set

        for (Long certidaoId : request.certidoes()) {
            Certidao certidao = certidaoRepository.findById(certidaoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Certidão", certidaoId));
            cartorio.addCertidao(certidao);
        }
        cartorioRepository.save(cartorio);
        return cartorioMapper.fromCartorio(cartorio);
    }

    public void delete(Long id) {
        Cartorio cartorio = cartorioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cartório", id));
        for (Certidao certidao : new HashSet<>(cartorio.getCertidoes())) {
            cartorio.removeCertidao(certidao);
        }
        cartorioRepository.delete(cartorio);
    }

    @Transactional
    public void linkCertidao(Long cartorioId, Long certidaoId) {
        Cartorio cartorio = cartorioRepository.findById(cartorioId)
                .orElseThrow(() -> new ResourceNotFoundException("Cartório", cartorioId));
        Certidao certidao = certidaoRepository.findById(certidaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Cartório", certidaoId));

        cartorio.addCertidao(certidao);
        certidao.addCartorio(cartorio);

        cartorioRepository.save(cartorio);
    }

    @Transactional
    public void unlinkCertidao(Long cartorioId, Long certidaoId) {
        Cartorio cartorio = cartorioRepository.findById(cartorioId)
                .orElseThrow(() -> new ResourceNotFoundException("Cartório", cartorioId));
        Certidao certidao = certidaoRepository.findById(certidaoId)
                .orElseThrow(() ->  new ResourceNotFoundException("Cartório", certidaoId));

        cartorio.removeCertidao(certidao);
        certidao.removeCartorio(cartorio);

        cartorioRepository.save(cartorio);
    }
}
