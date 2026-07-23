package com.minise.leave.integration;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mo phong "cho noi" ma mot SSO/IdP that (SAML/OIDC) cua cong ty se goi vao de
 * xac thuc nguoi dung, thay vi nguoi dung tu nhap email/mat khau nhu Stage 2.
 * KHONG duoc noi vao luong dang nhap thuc te (dang nhap van dung JWT local) -
 * day chi la endpoint minh hoa cho phan "tich hop doanh nghiep".
 *
 * ponytail: tra ve ket qua gia dinh san (bat ky token khong rong nao cung "valid").
 * Nang cap that: thay bang Spring Security OAuth2 Resource Server / oauth2-client
 * de xac thuc voi 1 IdP that (Okta, Keycloak, Azure AD...).
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
