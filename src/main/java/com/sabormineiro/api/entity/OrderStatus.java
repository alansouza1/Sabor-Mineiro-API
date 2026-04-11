package com.sabormineiro.api.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CRIADO("Criado"),
    SOLICITADO_CANCELAMENTO_CLIENTE("Solicitado cancelamento cliente"),
    CANCELADO_PELO_CLIENTE("Cancelado pelo cliente"),
    AGUARDANDO_PAGAMENTO_PIX("Aguardando pagamento PIX"),
    PAGAMENTO_PIX_INICIADO("Pagamento em PIX iniciado"),
    PIX_NAO_REALIZADO_TIMEOUT("Pix não realizado via timeout"),
    ORDEM_PRODUCAO_E_ENTREGA_ORDENADA("Produção e entrega ordenada"),
    PAGAMENTO_RECEBIDO_PIX("Pagamento recebido em PIX"),
    EM_PRODUCAO("Em produção"),
    SOLICITADO_CANCELAMENTO_ESTABELECIMENTO("Solicitado cancelamento estabelecimento"),
    CANCELADO_ESTABELECIMENTO_E_ESTORNADO("Cancelado estabelecimento e estornado"),
    COM_PROBLEMAS_NA_PRODUCAO("Com problemas na produção"),
    PRODUZIDO_E_AGUARDANDO_ENTREGA("Produzido e aguardando entrega"),
    SAIU_PARA_ENTREGA("Saiu para entrega"),
    COM_PROBLEMAS_NO_PACOTE("Com problemas no pacote"),
    COM_PROBLEMAS_NA_ENTREGA("Com problemas na entrega"),
    ENTREGUE("Entregue"),
    FINALIZADO("Finalizado");

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
        throw new IllegalArgumentException("Unknown status: " + description);
    }
}
