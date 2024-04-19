package com.cstav.evenmoreinstruments.util;

import java.util.Objects;

public class BiKey<K1, K2> {
    public final K1 k1;
    public final K2 k2;

    public BiKey(K1 loc, K2 pos) {
        this.k1 = loc;
        this.k2 = pos;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof BiKey<?, ?> oe) && k1.equals(oe.k1) && k2.equals(oe.k2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(k1, k2);
    }
}
