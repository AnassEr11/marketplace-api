package com.marketplace.common.error;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resource;
    private final Object id;

    public ResourceNotFoundException(String resource, Object id) {
        super("%s with id %s not found".formatted(resource, id));
        this.resource = resource;
        this.id = id;
    }
}
