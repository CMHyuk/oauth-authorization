package com.oauth.authorization.domain.user.controller;

import com.oauth.authorization.domain.user.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/oauth2/sign-in")
    public String signIn() {
        return "login";
    }

    @PostMapping("/oauth/sign-in")
    public String ok() {
        return "home";
    }
}
