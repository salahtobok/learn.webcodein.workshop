package com.webcodein.boundedcontext.order.acl;

import com.webcodein.boundedcontext.catalog.CatalogService;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Anti-Corruption Layer (ACL) Adapter.
 * Translates external Catalog DTOs into the Order context's internal ProductSnapshot value object.
 * This ensures changes to the Catalog context domain models do not corrupt the Order context logic.
 */
@Component
public class CatalogAclAdapter implements CatalogClient {

    private final CatalogService catalogService;

    public CatalogAclAdapter(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public Optional<ProductSnapshot> fetchProductBySku(String sku) {
        return catalogService.getProductBySku(sku)
            .map(dto -> new ProductSnapshot(
                dto.sku(),
                dto.name(),
                dto.price()
            ));
    }
}
