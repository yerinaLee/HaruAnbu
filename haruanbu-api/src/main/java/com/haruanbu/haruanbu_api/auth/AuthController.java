package com.haruanbu.haruanbu_api.auth;

import com.haruanbu.haruanbu_api.auth.DTO.SendEmailRequestDto;
import com.haruanbu.haruanbu_api.auth.DTO.SendEmailResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/email/send")
    public SendEmailResponseDto sendVerificateEmailJSON(@Valid @RequestBody SendEmailRequestDto req){
        Map<String, Object> result = new HashMap<>();
        result = authService.sendEmailVerificationCode(req.email());
        return new SendEmailResponseDto((Boolean)result.get("isSent"), 60);
    }

}
