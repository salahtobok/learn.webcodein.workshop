package com.webcodein.boundedcontext.order.acl;

import java.util.Optional;

public interface CatalogClient {
    Optional<ProductSnapshot> fetchProductBySku(String sku);
}
