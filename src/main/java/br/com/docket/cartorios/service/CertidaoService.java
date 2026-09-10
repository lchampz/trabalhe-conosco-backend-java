package br.com.docket.cartorios.service;

import br.com.docket.cartorios.dto.CertidaoRequest;
import br.com.docket.cartorios.dto.CertidaoResponse;
import br.com.docket.cartorios.mapper.CertidaoMapper;
import br.com.docket.cartorios.model.Certidao;
import br.com.docket.cartorios.repository.CertidaoRepository;
import br.com.docket.cartorios.shared.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class CertidaoService {

    private final CertidaoRepository certidaoRepository;
    private final CertidaoMapper certidaoMapper;

    @Transactional(readOnly = true)
    public Page<CertidaoResponse> list(String nome, Pageable pageable) {
        Page<Certidao> page = StringUtils.hasText(nome)
                ? certidaoRepository.findByNomeContainingIgnoreCase(nome, pageable)
                : certidaoRepository.findAll(pageable);
        return page.map(certidaoMapper::fromCertidao);
    }

    @Transactional(readOnly = true)
    public CertidaoResponse findById(Long id) {
        return certidaoMapper.fromCertidao(certidaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certidão", id)));
    }

    public CertidaoResponse create(CertidaoRequest request) {
        Certidao certidao = certidaoMapper.fromRequest(request);
        certidaoRepository.save(certidao);
        return certidaoMapper.fromCertidao(certidao);
    }

    public CertidaoResponse update(Long id, CertidaoRequest request) {
        Certidao certidao = certidaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cartório", id));
        certidaoMapper.fromRequest(request, certidao);
        certidaoRepository.save(certidao);
        return certidaoMapper.fromCertidao(certidao);
    }

    public void delete(Long id) {
        certidaoRepository.deleteById(id);
    }
}
