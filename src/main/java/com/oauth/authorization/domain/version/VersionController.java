package com.oauth.authorization.domain.version;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    @Value("${app.version}")
    private String version;

    @GetMapping(value = "/api/version")
    public String version() {
        return version;
    }
}
