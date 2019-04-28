package com.ldtteam.structurize.repomanagement.repostructure;

public class Trinuple<A, B, C> {
    private A a;
    private B b;
    private C c;

    public Trinuple(A aIn, B bIn, C cIn) {
        this.a = aIn;
        this.b = bIn;
        this.c = cIn;
    }

    public A getFirst() {
        return this.a;
    }

    public B getSecond() {
        return this.b;
    }

    public C getThird() {
        return this.c;
    }

    public void setFirst(final A a) {
        this.a = a;
    }

    public void setSecond(final B b) {
        this.b = b;
    }

    public void setThird(final C c) {
        this.c = c;
    }
}