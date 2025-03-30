package com.portaria.portaria_ws.models.enums;

public enum TicketType {
    FIXO("fixo"), DINAMICO("dinamico");

    private String type;

    TicketType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
