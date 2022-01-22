package com.diagramm.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Produces("application/json")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@RegisterRestClient(configKey = "kroki")
public interface KrokiClient {

    @POST
    @Path("c4plantuml/svg")
    String svg(
            String body
    );

}
