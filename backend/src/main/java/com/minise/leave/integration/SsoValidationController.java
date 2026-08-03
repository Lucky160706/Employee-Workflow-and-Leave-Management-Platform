package com.minise.leave.integration;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Cho de SSO cua cong ty goi vao xac thuc nguoi dung, thay cho viec tu nhap email
 * va mat khau. Luu y: endpoint nay chua duoc noi vao luong dang nhap that, dang nhap
 * van chay bang JWT local - day moi chi la cho de san.
 *
 * GIOI HAN: token nao khong rong cung duoc coi la hop le. Muon lam that thi dung
 * Spring Security OAuth2 Resource Server de noi voi Okta, Keycloak hay Azure AD.
 */
@RestController
@RequestMapping("/api/v1/integration/sso")
public class SsoValidationController {

    public record SsoValidationRequest(String ssoToken) {
    }

    public record SsoValidationResponse(boolean valid, String email) {
    }

    @PostMapping("/validate")
    public SsoValidationResponse validate(@RequestBody SsoValidationRequest request) {
        if (request.ssoToken() == null || request.ssoToken().isBlank()) {
            return new SsoValidationResponse(false, null);
        }
        return new SsoValidationResponse(true, "employee1@co.com");
    }
}
