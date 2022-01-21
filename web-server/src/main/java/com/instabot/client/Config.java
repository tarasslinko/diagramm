package com.instabot.client;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "client")
public interface Config {

    String id();
    String secret();
}
