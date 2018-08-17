package com.intact.rx.circuit.breaker;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intact.rx.circuit.breaker.id.CacheHandle;


/**
 * Add a monitor that proactively checks the circuits?
 */
public class CircuitBreakerCache implements CircuitBreakerObserver {
    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerCache.class);

    private static final CircuitBreakerSubject circuitBreakerSubject = new CircuitBreakerSubject();
    private static final CircuitBreakerCache instance = new CircuitBreakerCache();
    private static final Map<CacheHandle, Map<CircuitId, CircuitBreaker>> circuitCache = new ConcurrentHashMap<>();


    public static CircuitBreakerSubject observeAll() {
        return circuitBreakerSubject;
    }

    public static Map<CacheHandle, Map<CircuitId, CircuitBreaker>> circuitCache() {
        return circuitCache;
    }

    public static CircuitBreaker circuit(CircuitId circuitId, CircuitBreakerPolicy circuitBreakerPolicy) {
        if (circuitId.isNone() || circuitBreakerPolicy.isUnlimited()) {
            return CircuitBreakerAlwaysAllow.instance;
        }

        return cache(circuitId.getCacheHandle())
                .computeIfAbsent(
                        circuitId,
                        id -> {
                            CircuitBreaker circuitBreaker = new RxCircuitBreaker(circuitId, circuitBreakerPolicy);
                            circuitBreaker
                                    .onOpenDo(instance::onOpen)
                                    .onCloseDo(instance::onClose)
                                    .onHalfOpenDo(instance::onHalfOpen);
                            return circuitBreaker;
                        }
                );
    }

    public static Optional<CircuitBreaker> find(CircuitId circuitId) {
        return Optional.ofNullable(cache(circuitId.getCacheHandle()).get(circuitId));
    }

    private static Map<CircuitId, CircuitBreaker> cache(CacheHandle handle) {
        return circuitCache.computeIfAbsent(handle, cacheHandle -> new ConcurrentHashMap<>());
    }

    // --------------------------------------------
    // Interface CircuitBreakerObserver
    // --------------------------------------------

    @Override
    public void onOpen(CircuitId handle) {
        log.info("Circuit OPEN: {}", handle);
        circuitBreakerSubject.onOpen(handle);
    }

    @Override
    public void onClose(CircuitId handle) {
        log.info("Circuit CLOSE: {}", handle);
        circuitBreakerSubject.onClose(handle);
    }

    @Override
    public void onHalfOpen(CircuitId handle) {
        log.info("Circuit HALF-OPEN: {}", handle);
        circuitBreakerSubject.onHalfOpen(handle);
    }
}
