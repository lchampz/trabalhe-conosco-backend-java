package br.com.docket.cartorios.mapper;
import br.com.docket.cartorios.dto.CartorioRequest;
import br.com.docket.cartorios.dto.CartorioResponse;
import br.com.docket.cartorios.model.Cartorio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = CertidaoMapper.class)
public interface CartorioMapper {

    @Mapping(target = "totalCertidoes", expression = "java(cartorio.getCertidoes().size())")
    CartorioResponse fromCartorio(Cartorio cartorio);

    @Mapping(target = "certidoes", ignore = true)
    Cartorio fromRequest(CartorioRequest request);

    @Mapping(target = "certidoes", ignore = true)
    void fromRequest(CartorioRequest request, @MappingTarget Cartorio cartorio);

}
