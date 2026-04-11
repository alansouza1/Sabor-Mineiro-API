package com.sabormineiro.api.entity;

public enum Category {
    ENTRADAS("Entradas"),
    PRATOS_PRINCIPAIS("Pratos Principais"),
    BEBIDAS("Bebidas"),
    SOBREMESAS("Sobremesas");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Category fromValue(String value) {
        for (Category category : Category.values()) {
            if (category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + value);
    }
}
