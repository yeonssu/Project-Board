package com.preonboarding.global.jwt.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.access-token-expiration-minutes}")
    private Long ACCESS_TOKEN_EXPIRATION;

    @Value("${jwt.secret}")
    private String secretKey;

    private final UserDetailsService userDetailsService;

    private Key key;

    @PostConstruct
    public void init() {
        log.info("[JwtTokenProvider] secretKey 초기화 시작 {}", secretKey);
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
        log.info("[JwtTokenProvider] secretKey 초기화 완료 {}", secretKey);
    }

    public String generateAccessToken(String email, List<String> roles) {
        log.info("[JwtTokenProvider] 액세스 토큰 생성");
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        log.info("[JwtTokenProvider] 토큰 인증 정보 조회 시작");
        UserDetails userDetails = userDetailsService.loadUserByUsername(getEmailByToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmailByToken(String token) {
        String email = getClaims(token)
                .getBody()
                .getSubject();
        log.info("[JwtTokenProvider] 토큰에서 회원 이메일 추출 : {}", email);
        return email;
    }

    public String resolveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.replace("Bearer ", "");
        }
        return null;
    }

    public Jws<Claims> getClaims(String token) {
        log.info("[JwtTokenProvider] 토큰 claims 가져오기 시작");
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (SecurityException e) {
            log.info("[JwtTokenProvider] Invalid JWT signature");
            throw new JwtException("유효하지 않은 시그니처입니다.");
        } catch (MalformedJwtException e) {
            log.info("[JwtTokenProvider] Invalid JWT Token", e);
            throw new JwtException("유효하지 않은 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("[JwtTokenProvider] Unsupported JWT Token", e);
            throw new JwtException("지원하지 않는 토큰입니다.");
        } catch (ExpiredJwtException e) {
            log.info("[JwtTokenProvider] Expired JWT Token", e);
            throw new JwtException("토큰 기한이 만료되었습니다.");
        }
    }

    public boolean validateToken(String token) {
        log.info("[JwtTokenProvider] 토큰 유효 체크 시작");
        return getClaims(token)
                .getBody()
                .getExpiration()
                .after(new Date());
    }

    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Authorization", "Bearer " + accessToken);
        log.info("[JwtTokenProvider] 액세스 토큰 헤더 설정");
    }
}
