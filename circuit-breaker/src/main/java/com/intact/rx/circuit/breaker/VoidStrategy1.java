package com.intact.rx.circuit.breaker;

@FunctionalInterface
public interface VoidStrategy1<Arg1> {
    void perform(Arg1 arg1);
}