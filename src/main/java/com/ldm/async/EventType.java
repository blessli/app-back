package com.ldm.async;

public enum EventType {
    LIKE(0),
    COMMENT(1),
    FOLLOW(2),
    REPLY(3);

    private int value;
    EventType(int value) { this.value = value; }
    public int getValue() { return value; }
}
