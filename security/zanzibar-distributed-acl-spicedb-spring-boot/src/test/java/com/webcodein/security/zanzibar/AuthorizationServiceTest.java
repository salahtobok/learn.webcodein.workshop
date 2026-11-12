package com.webcodein.security.zanzibar;

import com.authzed.api.v1.CheckPermissionRequest;
import com.authzed.api.v1.CheckPermissionResponse;
import com.authzed.api.v1.PermissionsServiceGrpc;
import com.authzed.api.v1.SchemaServiceGrpc;
import com.authzed.api.v1.WriteSchemaRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService;

    @Mock
    private SchemaServiceGrpc.SchemaServiceBlockingStub schemaService;

    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        authorizationService = new AuthorizationService(permissionsService, schemaService);
    }

    @Test
    @DisplayName("checkPermission returns true when SpiceDB returns PERMISSIONSHIP_HAS_PERMISSION")
    void checkPermission_ReturnsTrueWhenAllowed() {
        CheckPermissionResponse response = CheckPermissionResponse.newBuilder()
                .setPermissionship(CheckPermissionResponse.Permissionship.PERMISSIONSHIP_HAS_PERMISSION)
                .build();

        when(permissionsService.checkPermission(any(CheckPermissionRequest.class))).thenReturn(response);

        boolean allowed = authorizationService.checkPermission("document", "doc-1", "read", "user", "alice");

        assertTrue(allowed, "Expected authorization to be granted");

        ArgumentCaptor<CheckPermissionRequest> captor = ArgumentCaptor.forClass(CheckPermissionRequest.class);
        verify(permissionsService).checkPermission(captor.capture());

        CheckPermissionRequest req = captor.getValue();
        assertEquals("document", req.getResource().getObjectType());
        assertEquals("doc-1", req.getResource().getObjectId());
        assertEquals("read", req.getPermission());
        assertEquals("user", req.getSubject().getObject().getObjectType());
        assertEquals("alice", req.getSubject().getObject().getObjectId());
        assertTrue(req.getConsistency().getFullyConsistent());
    }

    @Test
    @DisplayName("checkPermission returns false when SpiceDB returns PERMISSIONSHIP_NO_PERMISSION")
    void checkPermission_ReturnsFalseWhenDenied() {
        CheckPermissionResponse response = CheckPermissionResponse.newBuilder()
                .setPermissionship(CheckPermissionResponse.Permissionship.PERMISSIONSHIP_NO_PERMISSION)
                .build();

        when(permissionsService.checkPermission(any(CheckPermissionRequest.class))).thenReturn(response);

        boolean allowed = authorizationService.checkPermission("document", "doc-1", "read", "user", "bob");

        assertFalse(allowed, "Expected authorization to be denied");
    }

    @Test
    @DisplayName("writeSchema dispatches WriteSchemaRequest to SchemaService")
    void writeSchema_DispatchesCorrectSchemaText() {
        String testSchema = "definition user {}\ndefinition document { relation viewer: user }";

        authorizationService.writeSchema(testSchema);

        ArgumentCaptor<WriteSchemaRequest> captor = ArgumentCaptor.forClass(WriteSchemaRequest.class);
        verify(schemaService).writeSchema(captor.capture());

        assertEquals(testSchema, captor.getValue().getSchema());
    }
}
