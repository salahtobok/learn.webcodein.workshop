package com.webcodein.boundedcontext.order.acl;

import com.webcodein.boundedcontext.catalog.CatalogService;
import com.webcodein.boundedcontext.catalog.dto.ProductResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CatalogAclAdapterTest {

    @Mock
    private CatalogService catalogService;

    @InjectMocks
    private CatalogAclAdapter catalogAclAdapter;

    @Test
    @DisplayName("Should translate external Catalog DTO into Order ProductSnapshot")
    void shouldTranslateExternalDtoToInternalSnapshot() {
        ProductResponseDto dto = new ProductResponseDto(
            UUID.randomUUID(),
            "SKU-ACL",
            "ACL Monitor",
            "4K Ultra-wide",
            599.99
        );

        given(catalogService.getProductBySku("SKU-ACL"))
            .willReturn(Optional.of(dto));

        Optional<ProductSnapshot> snapshot = catalogAclAdapter.fetchProductBySku("SKU-ACL");

        assertThat(snapshot).isPresent();
        assertThat(snapshot.get().sku()).isEqualTo("SKU-ACL");
        assertThat(snapshot.get().name()).isEqualTo("ACL Monitor");
        assertThat(snapshot.get().price()).isEqualTo(599.99);
    }

    @Test
    @DisplayName("Should return empty optional when product not found in Catalog")
    void shouldReturnEmptyWhenNotFound() {
        given(catalogService.getProductBySku("UNKNOWN"))
            .willReturn(Optional.empty());

        Optional<ProductSnapshot> snapshot = catalogAclAdapter.fetchProductBySku("UNKNOWN");

        assertThat(snapshot).isEmpty();
    }
}
