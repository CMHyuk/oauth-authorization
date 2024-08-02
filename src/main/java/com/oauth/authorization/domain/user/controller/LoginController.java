package com.oauth.authorization.domain.user.controller;

import com.oauth.authorization.domain.user.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

//    @GetMapping("/login")
//    public String signIn() {
//        return "login";
//    }
//
//    @PostMapping("/login")
//    @ResponseBody
//    public String ok() {
//        return "ok";
//    }
}
