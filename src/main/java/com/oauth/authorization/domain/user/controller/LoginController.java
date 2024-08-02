package com.oauth.authorization.domain.user.controller;

import com.oauth.authorization.domain.user.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/sign-in")
    public String signIn() {
        return "login";
    }
}
