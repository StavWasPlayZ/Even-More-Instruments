package com.cstav.evenmoreinstruments.util;

public record Tuple3<A, B, C>(A a, B b, C c) {

    @Override
    public boolean equals(Object o) {
        return (o instanceof Tuple3<?, ?, ?> oTuple)
            && a.equals(oTuple.a)
            && b.equals(oTuple.b)
            && c.equals(oTuple.c)
        ;
    }
}
