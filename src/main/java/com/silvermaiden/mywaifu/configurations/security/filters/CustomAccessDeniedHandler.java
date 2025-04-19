package com.silvermaiden.mywaifu.configurations.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvermaiden.mywaifu.models.dtos.http.ApiResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.silvermaiden.mywaifu.common.constants.ErrorCode.FORBIDDEN;
import static com.silvermaiden.mywaifu.common.constants.HTTPConstant.DEFAULT_CONTENT_TYPE;
import static com.silvermaiden.mywaifu.common.constants.SecurityConstant.FORBIDDEN_MESSAGE;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(
            HttpServletRequest req,
            HttpServletResponse res,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        ApiResponseDTO<Void> apiResponse = ApiResponseDTO.error(FORBIDDEN_MESSAGE, FORBIDDEN);
        res.setContentType(DEFAULT_CONTENT_TYPE);
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        res.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
    }
}
