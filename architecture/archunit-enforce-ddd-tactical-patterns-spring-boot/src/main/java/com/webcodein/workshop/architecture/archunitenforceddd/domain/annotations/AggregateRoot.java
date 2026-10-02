package com.webcodein.workshop.architecture.archunitenforceddd.domain.annotations;
import java.lang.annotation.*;
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AggregateRoot {}
