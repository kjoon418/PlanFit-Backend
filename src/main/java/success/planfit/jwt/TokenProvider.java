package success.planfit.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import success.planfit.domain.user.User;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class TokenProvider {

    private static final String TOKEN_TYPE_CLAIM = "PlanFit/TokenType";
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";

    private final long validityTime;
    private final Key key;

    public TokenProvider(@Value("${keys.jwt.secret}") String jwtSecret, @Value("${keys.jwt.access-token-validity-in-milliseconds}") long validityTime) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.validityTime = validityTime;
    }

    public String createToken(User user, TokenType tokenType) {
        // 토큰의 타입에 맞게 만료 시간을 지정함(리프레쉬 토큰의 지속시간을 24배 길게 설정함)
        Date accessTokenExpiredTime;
        switch (tokenType) {
            case ACCESS -> {
                accessTokenExpiredTime = new Date(new Date().getTime() + validityTime);
            }
            case REFRESH -> {
                accessTokenExpiredTime = new Date(new Date().getTime() + (validityTime * 24));
            }
            case null, default ->  {
                throw new IllegalArgumentException("토큰 생성 실패: 부적절한 TokenType 입니다.");
            }
        }

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .setExpiration(accessTokenExpiredTime)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(claims.getSubject(), "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        authentication.setDetails(claims);

        return authentication;
    }

    /**
     * 토큰 앞의 "Bearer "를 제거하는 메서드
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(7);
        }

        // 토큰이 Bearer 형식이 아닐 경우, 부적절한 토큰이므로 null 을 반환함
        return null;
    }

    /**
     * 토큰의 유효성을 검증하는 메서드
     */
    public boolean validateToken(String token, TokenType tokenType) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return hasProperType(token, tokenType);
        } catch (UnsupportedJwtException | ExpiredJwtException | IllegalArgumentException | MalformedJwtException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 문자열 토큰을 Claims 로 변환하는 메서드
     */
    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (SignatureException e) {
            throw new IllegalArgumentException("토큰 복호화 실패: 부적절한 토큰입니다.");
        }
    }

    /**
     * 토큰의 타입이 파라미터로 넘긴 타입과 일치하는지를 반환하는 메서드
     */
    private boolean hasProperType(String token, TokenType tokenType) {
        Claims claims = parseClaims(token);
        String tokenTypeClaim = (String) claims.get(TOKEN_TYPE_CLAIM);

        return tokenType == TokenType.valueOf(tokenTypeClaim);
    }
}
