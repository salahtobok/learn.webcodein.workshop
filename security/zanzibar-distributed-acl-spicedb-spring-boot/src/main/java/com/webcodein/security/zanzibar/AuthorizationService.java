package com.webcodein.security.zanzibar;

import com.authzed.api.v1.CheckPermissionRequest;
import com.authzed.api.v1.CheckPermissionResponse;
import com.authzed.api.v1.ObjectReference;
import com.authzed.api.v1.PermissionRelationshipTree;
import com.authzed.api.v1.PermissionsServiceGrpc;
import com.authzed.api.v1.SubjectReference;
import com.authzed.api.v1.SchemaServiceGrpc;
import com.authzed.api.v1.WriteSchemaRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private final PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService;
    private final SchemaServiceGrpc.SchemaServiceBlockingStub schemaService;

    public AuthorizationService(PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService,
                                SchemaServiceGrpc.SchemaServiceBlockingStub schemaService) {
        this.permissionsService = permissionsService;
        this.schemaService = schemaService;
    }

    public boolean checkPermission(String resourceType, String resourceId, String permission, String subjectType, String subjectId) {
        ObjectReference resource = ObjectReference.newBuilder()
                .setObjectType(resourceType)
                .setObjectId(resourceId)
                .build();

        SubjectReference subject = SubjectReference.newBuilder()
                .setObject(ObjectReference.newBuilder()
                        .setObjectType(subjectType)
                        .setObjectId(subjectId)
                        .build())
                .build();

        CheckPermissionRequest request = CheckPermissionRequest.newBuilder()
                .setResource(resource)
                .setPermission(permission)
                .setSubject(subject)
                .build();

        CheckPermissionResponse response = permissionsService.checkPermission(request);
        return response.getPermissionship() == CheckPermissionResponse.Permissionship.PERMISSIONSHIP_HAS_PERMISSION;
    }

    public void writeSchema(String schemaText) {
        WriteSchemaRequest request = WriteSchemaRequest.newBuilder()
                .setSchema(schemaText)
                .build();
        schemaService.writeSchema(request);
    }
}
