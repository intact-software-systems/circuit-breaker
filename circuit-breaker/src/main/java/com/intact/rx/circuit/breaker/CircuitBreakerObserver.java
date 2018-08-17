package com.intact.rx.circuit.breaker;

public interface CircuitBreakerObserver {
    void onOpen(CircuitId handle);

    void onClose(CircuitId handle);

    void onHalfOpen(CircuitId handle);
}
