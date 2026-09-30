package com.webcodein.dop.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CreditCard.class, name = "creditCard"),
    @JsonSubTypes.Type(value = PayPal.class, name = "paypal"),
    @JsonSubTypes.Type(value = Crypto.class, name = "crypto")
})
public sealed interface PaymentMethod permits CreditCard, PayPal, Crypto {}
