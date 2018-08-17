package com.intact.rx.circuit.breaker;

import java.util.concurrent.atomic.AtomicReference;

import com.intact.rx.circuit.breaker.id.CacheHandle;
import com.intact.rx.circuit.breaker.id.DomainCacheId;
import com.intact.rx.circuit.breaker.id.MasterCacheId;

public class RxDefault {
    private static final AtomicReference<DomainCacheId> defaultRxCommandDomainCacheId = new AtomicReference<>(new DomainCacheId("RxCommand.DefaultRxCommandCacheId"));

    private static final CacheHandle globalCircuitCacheHandle = CacheHandle.create(defaultRxCommandDomainCacheId.get(), MasterCacheId.create("Rx.globalCircuitBreakerScope"), CircuitBreakerPolicy.class);

    public static CacheHandle getActCircuitCacheHandle() {
        return globalCircuitCacheHandle;
    }

    public static DomainCacheId getDefaultRxCommandDomainCacheId() {
        return defaultRxCommandDomainCacheId.get();
    }
}
