package com.memory.nexigntasks.Entities;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CallType {

    OUTGOING("01"),
    INCOMING("02");

    private final String dbCode; // "01" или "02"

    CallType(String dbCode) {
        this.dbCode = dbCode;
    }

    @JsonValue
    public String getDbCode() {
        return dbCode;
    }

    public static CallType fromDbCode(String code) {
        return switch (code) {
            case "01" -> OUTGOING;
            case "02" -> INCOMING;
            default -> throw new IllegalArgumentException("Unknown dbCode: " + code);
        };
    }
}
