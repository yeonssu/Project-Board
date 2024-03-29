package com.preonboarding.global.jwt.filter;

import com.preonboarding.global.dto.ErrorResponse;
import com.preonboarding.global.utils.ResponseUtils;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException | UsernameNotFoundException exception) {
            setErrorResponse(response, exception);
        }
    }

    private void setErrorResponse(HttpServletResponse response, RuntimeException exception) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED, exception.getMessage());
        ResponseUtils.setResponseStatus(response, HttpServletResponse.SC_UNAUTHORIZED, errorResponse);
    }
}
