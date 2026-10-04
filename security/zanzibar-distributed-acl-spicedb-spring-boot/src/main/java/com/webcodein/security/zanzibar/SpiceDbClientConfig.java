package com.webcodein.security.zanzibar;

import com.authzed.api.v1.PermissionsServiceGrpc;
import com.authzed.api.v1.SchemaServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpiceDbClientConfig {

    @Value("${spicedb.host:localhost}")
    private String host;

    @Value("${spicedb.port:50051}")
    private int port;

    @Value("${spicedb.preshared-key:somerandomkeyhere}")
    private String presharedKey;

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext() // Used for local development
                .build();
    }

    @Bean
    public PermissionsServiceGrpc.PermissionsServiceBlockingStub permissionsServiceBlockingStub(ManagedChannel channel) {
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER), "Bearer " + presharedKey);
        return PermissionsServiceGrpc.newBlockingStub(channel)
                .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata));
    }

    @Bean
    public SchemaServiceGrpc.SchemaServiceBlockingStub schemaServiceBlockingStub(ManagedChannel channel) {
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER), "Bearer " + presharedKey);
        return SchemaServiceGrpc.newBlockingStub(channel)
                .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata));
    }
}
