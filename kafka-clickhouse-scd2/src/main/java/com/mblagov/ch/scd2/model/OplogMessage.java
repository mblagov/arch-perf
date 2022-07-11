package com.mblagov.ch.scd2.model;

public class OplogMessage {

    private String op;
    private O o;
    private Long ts;

    public OplogMessage() {
    }

    public OplogMessage(String op, O o, Long ts) {
        this.op = op;
        this.o = o;
        this.ts = ts;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public O getO() {
        return o;
    }

    public void setO(O o) {
        this.o = o;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "OplogMessage{" +
                "op='" + op + '\'' +
                ", o=" + o +
                ", ts=" + ts +
                '}';
    }
}
