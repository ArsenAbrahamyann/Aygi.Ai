
package com.example.demo.security;

import com.example.demo.entity.User;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTTokenProvider {
    public static final Logger LOG = LoggerFactory.getLogger(JWTTokenProvider.class);

    public String generateToken(Authentication authentication) {
        Map<String, Object> claimsMap = new HashMap<>();
        String userId = "";
        String username = "";

        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            userId = Long.toString(user.getId());
            username = user.getEmail();
            claimsMap.put("id", userId);
            claimsMap.put("username", username);
        } else if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            username = oAuth2User.getAttribute("email");
            claimsMap.put("username", username);

        }

        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + SecurityConstants.EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(username)
                .addClaims(claimsMap)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SecurityConstants.SECRET)
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException |
                 MalformedJwtException |
                 ExpiredJwtException |
                 UnsupportedJwtException |
                 IllegalArgumentException ex) {
            LOG.error(ex.getMessage());
            return false;
        }
    }

    public int getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityConstants.SECRET)
                .parseClaimsJws(token)
                .getBody();
        String id = (String) claims.get("id");
        return Integer.parseInt(id);
    }
}
