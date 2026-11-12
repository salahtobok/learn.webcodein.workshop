package com.webcodein.documents.interfaces.rest;

import com.webcodein.documents.application.service.DocumentService;
import com.webcodein.documents.domain.model.Document;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Path("/documents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentResource {

    private final DocumentService documentService;

    public DocumentResource(DocumentService documentService) {
        this.documentService = documentService;
    }

    public record DocumentRequest(String title) {}

    public record DocumentResponse(UUID id, String title, String status) {
        public static DocumentResponse from(Document document) {
            return new DocumentResponse(
                    document.getId(),
                    document.getTitle().getValue(),
                    document.getStatus().name()
            );
        }
    }

    @POST
    public Response createDocument(DocumentRequest request) {
        Document doc = documentService.createDocument(request.title());
        return Response.status(Response.Status.CREATED).entity(DocumentResponse.from(doc)).build();
    }

    @GET
    @Path("/{id}")
    public Response getDocument(@PathParam("id") UUID id) {
        try {
            Document doc = documentService.getDocument(id);
            return Response.ok(DocumentResponse.from(doc)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/{id}/publish")
    public Response publishDocument(@PathParam("id") UUID id) {
        try {
            documentService.publishDocument(id);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/archive")
    public Response archiveDocument(@PathParam("id") UUID id) {
        try {
            documentService.archiveDocument(id);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }
}
