package br.com.docket.cartorios.shared.error;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super("%s %s não encontrado".formatted(resource, id));
    }
}
