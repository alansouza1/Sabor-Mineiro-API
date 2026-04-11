package com.sabormineiro.api.entity;

public enum PaymentMethod {
    PIX("pix"),
    CARD("card"),
    CASH("cash");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PaymentMethod fromValue(String value) {
        if (value == null) return PIX;
        
        String normalized = value.toLowerCase().replace("_", "");
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.value.equalsIgnoreCase(normalized) || 
                (method == CARD && normalized.contains("card"))) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown payment method: " + value);
    }
}
