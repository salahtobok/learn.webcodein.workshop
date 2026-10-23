package com.webcodein.hikaricp.dto;

import java.math.BigDecimal;

public record OrderRequest(
        String customerName,
        String productCode,
        BigDecimal amount
) {
}
