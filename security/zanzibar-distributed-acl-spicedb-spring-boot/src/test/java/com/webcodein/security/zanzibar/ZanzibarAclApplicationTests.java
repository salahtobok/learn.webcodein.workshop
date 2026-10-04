package com.webcodein.security.zanzibar;

import com.authzed.api.v1.RelationshipUpdate;
import com.authzed.api.v1.WriteRelationshipsRequest;
import com.authzed.api.v1.PermissionsServiceGrpc;
import com.authzed.api.v1.Relationship;
import com.authzed.api.v1.ObjectReference;
import com.authzed.api.v1.SubjectReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class ZanzibarAclApplicationTests {

    @Container
    static final GenericContainer<?> spicedb = new GenericContainer<>("authzed/spicedb:v1.30.0")
            .withCommand("serve", "--grpc-preshared-key", "test-key-1234")
            .withExposedPorts(50051);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spicedb.host", spicedb::getHost);
        registry.add("spicedb.port", spicedb::getFirstMappedPort);
        registry.add("spicedb.preshared-key", () -> "test-key-1234");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsService;

    @BeforeEach
    void setupSpiceDB() {
        // Write the Schema
        String schema = "definition user {}\n" +
                "definition document {\n" +
                "    relation viewer: user\n" +
                "    permission read = viewer\n" +
                "}";
        authorizationService.writeSchema(schema);

        // Write a relationship: alice is a viewer of document:1
        Relationship relationship = Relationship.newBuilder()
                .setResource(ObjectReference.newBuilder().setObjectType("document").setObjectId("1").build())
                .setRelation("viewer")
                .setSubject(SubjectReference.newBuilder().setObject(
                        ObjectReference.newBuilder().setObjectType("user").setObjectId("alice").build()
                ).build())
                .build();

        RelationshipUpdate update = RelationshipUpdate.newBuilder()
                .setOperation(RelationshipUpdate.Operation.OPERATION_CREATE)
                .setRelationship(relationship)
                .build();

        permissionsService.writeRelationships(WriteRelationshipsRequest.newBuilder()
                .addUpdates(update)
                .build());
    }

    @Test
    void aliceCanReadDocument1() throws Exception {
        mockMvc.perform(get("/api/documents/1").with(httpBasic("alice", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void bobCannotReadDocument1() throws Exception {
        mockMvc.perform(get("/api/documents/1").with(httpBasic("bob", "password")))
                .andExpect(status().isForbidden());
    }
}
