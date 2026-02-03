package com.haruanbu.haruanbu_api.auth;

import com.haruanbu.haruanbu_api.common.exception.ApiException;
import com.haruanbu.haruanbu_api.common.exception.ErrorCode;
import com.haruanbu.haruanbu_api.common.util.Util;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class AuthService {

    private static final Duration COOLDOWN = Duration.ofSeconds(60); // 연타방지 - 60초
    private static final Duration EXPIRES_IN = Duration.ofMinutes(10); // 이메일 인증 유효시간 10분

    private final EmailVerificationRepository emailRepo;
    private final Random random = new Random();

    public AuthService(EmailVerificationRepository emailRepo){
        this.emailRepo = emailRepo;
    }

    public Map<String, Object> sendEmailVerificationCode(String email){

        Map<String, Object> result = new HashMap<>();

        // 1) 쿨다운 체크
        Optional<EmailVerification> latestOpt = emailRepo.findTopByEmailOrderByDateCreatedDesc(email);
        if (latestOpt.isPresent()) {
            Instant last = latestOpt.get().getDateCreated();
            if (last.plus(COOLDOWN).isAfter(Instant.now())) {
                throw new ApiException(ErrorCode.TOO_MANY_REQUESTS, "60초 후 인증코드 재발송이 가능합니다.");
            }
        }

        // 2) 6자리 코드 생성 (앞자리 0 포함)
        String code = String.format("%06d", random.nextInt(1_000_000));

        // 3) 해시 생성
        String codeHash = Util.sha256(email + ":" + code);

        // 4) DB 저장
        Instant expiresAt = Instant.now().plus(EXPIRES_IN);
        emailRepo.save(new EmailVerification(email, codeHash, expiresAt));
        
        // 5) 이메일 발송 코드(일단 로그)
        System.out.println("[EMAIL_VERIFICATION] email=" + email + " code=" + code + " expiresAt=" + expiresAt);

        // 테스트용 하드코딩
        result.put("isSent", true);

        return result;
    }






}
