package br.com.docket.cartorios.model;

import br.com.docket.cartorios.dto.CartorioRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Entity
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "cartorio")
public class Cartorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150, unique = true)
    private String nome;

    @NotBlank
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(nullable = false, length = 8)
    private String cep;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String rua;

    @NotBlank
    @Column(nullable = false, length = 6)
    private String numero;

    @Column(length = 100)
    private String complemento;

    @Column(length = 100)
    private String bairro;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String cidade;

    @NotBlank
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(nullable = false, length = 2)
    private String uf;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "cartorio_certidao",
            joinColumns = @JoinColumn(name = "cartorio_id"),
            inverseJoinColumns = @JoinColumn(name = "certidao_id")
    )
    private Set<Certidao> certidoes = new HashSet<>();

    @Column(nullable = false)
    @CreatedDate
    private Instant createdAt;

    public Cartorio() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cartorio other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void addCertidao(Certidao certidao) {
        this.certidoes.add(certidao);
        certidao.getCartorios().add(this);
    }

    public void removeCertidao(Certidao certidao) {
        this.certidoes.remove(certidao);
        certidao.getCartorios().remove(this);
    }
}
