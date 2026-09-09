package br.com.docket.cartorios.repository;

import br.com.docket.cartorios.model.Certidao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertidaoRepository extends JpaRepository<Certidao, Long> {
    Page<Certidao> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
