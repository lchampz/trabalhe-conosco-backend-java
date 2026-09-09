package br.com.docket.cartorios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "certidao")
public class Certidao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 150)
    private String nome;

    @ManyToMany(mappedBy = "certidoes")
    private final Set<Cartorio> cartorios = new HashSet<>();

    @Column(nullable = false)
    @CreatedDate
    private Instant createdAt;

    protected Certidao() {
    }

    public Certidao(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Certidao other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


    @PreRemove
    private void removeAllLinkWithCartorios() {
        for (Cartorio cartorio : cartorios) {
            cartorio.getCertidoes().remove(this);
        }
        cartorios.clear();
    }

    public void addCartorio(Cartorio cartorio) {
        this.cartorios.add(cartorio);
        cartorio.getCertidoes().add(this);
    }

    public void removeCartorio(Cartorio cartorio) {
        this.cartorios.remove(cartorio);
        cartorio.getCertidoes().remove(this);
    }
}
