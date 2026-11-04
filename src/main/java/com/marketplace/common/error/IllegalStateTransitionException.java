package com.marketplace.common.error;

import lombok.Getter;

@Getter
public class IllegalStateTransitionException extends RuntimeException {
    private final Enum<?> from;
    private final Enum<?> to;

    public IllegalStateTransitionException(Enum<?> from, Enum<?> to) {
        super("Illegal state transition from %s to %s".formatted(from, to));
        this.from = from;
        this.to = to;
    }
}
