package com.instabot.client;

import com.instabot.ResponseStatusMapper;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Produces("application/json")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@RegisterRestClient(configKey = "client")
@RegisterProvider(value = ResponseStatusMapper.class, priority = 50)
public interface ApiClient {
    @GET
    @Path("/get")
    OauthResponse oauth(
            @QueryParam("param") String param
    );


}
