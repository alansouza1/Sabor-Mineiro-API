package com.sabormineiro.api.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CREATED("Created"),
    CLIENT_CANCELLATION_REQUESTED("Client cancellation requested"),
    CANCELLED_BY_CLIENT("Cancelled by client"),
    AWAITING_PIX_PAYMENT("Awaiting PIX payment"),
    PIX_PAYMENT_STARTED("PIX payment started"),
    PIX_TIMEOUT_EXPIRED("Pix payment expired by timeout"),
    PRODUCTION_AND_DELIVERY_ORDERED("Production and delivery ordered"),
    PIX_PAYMENT_RECEIVED("PIX payment received"),
    IN_PRODUCTION("In production"),
    ESTABLISHMENT_CANCELLATION_REQUESTED("Establishment cancellation requested"),
    CANCELLED_AND_REFUNDED("Cancelled and refunded"),
    PRODUCTION_PROBLEMS("Production issues detected"),
    PRODUCED_AWAITING_DELIVERY("Produced and awaiting delivery"),
    OUT_FOR_DELIVERY("Out for delivery"),
    PACKAGING_PROBLEMS("Packaging issues detected"),
    DELIVERY_PROBLEMS("Delivery issues detected"),
    DELIVERED("Delivered"),
    FINISHED("Finished");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public static OrderStatus fromDescription(String description) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.description.equalsIgnoreCase(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status description: " + description);
    }
}
