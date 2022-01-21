package com.instabot.client;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Service {

    @Inject
    @RestClient
    ApiClient apiClient;

    @Inject
    Config config;

    public void login() {

    }


}
