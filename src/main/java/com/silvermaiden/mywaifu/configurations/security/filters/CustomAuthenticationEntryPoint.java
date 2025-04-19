package com.silvermaiden.mywaifu.configurations.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvermaiden.mywaifu.models.dtos.http.ApiResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.silvermaiden.mywaifu.common.constants.ErrorCode.UNAUTHORIZED;
import static com.silvermaiden.mywaifu.common.constants.HTTPConstant.DEFAULT_CONTENT_TYPE;
import static com.silvermaiden.mywaifu.common.constants.SecurityConstant.ACCESS_DENIED_MESSAGE;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest req,
            HttpServletResponse res,
            AuthenticationException authException
    ) throws IOException {
        ApiResponseDTO<Void> apiResponse = ApiResponseDTO.error(ACCESS_DENIED_MESSAGE, UNAUTHORIZED);
        res.setContentType(DEFAULT_CONTENT_TYPE);
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
    }
}
