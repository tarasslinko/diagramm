package com.diagramm;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Tag(name = "resource")
@Path("/diagramm")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Resource {

    @Inject
    SomeSystem someSystem;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Operation(summary = "Hello")
    @Path("{num}")
    public String login(@PathParam("num") Integer num) {
        return someSystem.prepareWorkspace(num);
    }

}
