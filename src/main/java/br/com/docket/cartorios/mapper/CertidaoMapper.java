package br.com.docket.cartorios.mapper;

import br.com.docket.cartorios.dto.CertidaoRequest;
import br.com.docket.cartorios.dto.CertidaoResponse;
import br.com.docket.cartorios.model.Certidao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CertidaoMapper {
    CertidaoResponse fromCertidao(Certidao certidao);
    List<CertidaoResponse> fromCertidao(List<Certidao> certidaoList);

    @Mapping(target = "cartorios", ignore = true)
    Certidao fromRequest(CertidaoRequest request);

    @Mapping(target = "cartorios", ignore = true)
    void fromRequest(CertidaoRequest request, @MappingTarget Certidao certidao);
}
