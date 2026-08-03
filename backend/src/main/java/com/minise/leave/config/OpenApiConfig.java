package com.minise.leave.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spec sinh ra o /v3/api-docs, xem qua /swagger-ui.html. Doi tac muon goi vao app nay
 * chi can doc do la biet co endpoint gi, gui gi, nhan lai gi - khong phai xin source code.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI leaveApprovalOpenApi() {
        String bearerScheme = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Leave Approval System API")
                        .version("v1")
                        .description("Mini SE project - internal leave-request workflow, enterprise-integration-ready."))
                .addSecurityItem(new SecurityRequirement().addList(bearerScheme))
                .components(new Components().addSecuritySchemes(bearerScheme,
                        new SecurityScheme()
                                .name(bearerScheme)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
