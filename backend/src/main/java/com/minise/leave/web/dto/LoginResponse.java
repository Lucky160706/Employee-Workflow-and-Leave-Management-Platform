package com.minise.leave.web.dto;

public record LoginResponse(String token, String role, String fullName) {
}
