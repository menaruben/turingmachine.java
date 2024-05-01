package org.tuma;

public final class Result<T1, T2, T3> {
    private T1 t1;
    private T2 t2;
    private T3 t3;

    public Result() {}

    public Result(T1 t1, T2 t2, T3 t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    public void setItem1(T1 t1) {
        this.t1 = t1;
    }

    public void setItem2(T2 t2) {
        this.t2 = t2;
    }

    public void setItem3(T3 t3) {
        this.t3 = t3;
    }

    public T1 getItem1() {
        return t1;
    }

    public T2 getItem2() {
        return t2;
    }

    public T3 getItem3() {
        return t3;
    }

    public String toString() {
        return "tape: " + t1 + ", verdict: " + t2 + ", end state: " + t3;
    }
}
