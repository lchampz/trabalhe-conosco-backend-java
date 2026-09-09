package br.com.docket.cartorios.repository;

import br.com.docket.cartorios.model.Cartorio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartorioRepository extends JpaRepository<Cartorio, Long> {

    @EntityGraph(attributePaths = {"certidoes"})
    Page<Cartorio> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"certidoes"})
    Optional<Cartorio> findById(Long id);

    @EntityGraph(attributePaths = {"certidoes"})
    Page<Cartorio> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

}
