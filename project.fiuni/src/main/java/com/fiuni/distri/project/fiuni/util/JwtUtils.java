package com.fiuni.distri.project.fiuni.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt-secret:secret}")
    private String jwtSecret;

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.jwtSecret));
    }

    public Object getClaim(String token, String key) {
        return getClaims(token).get(key);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parse(token);
            return true;
        }catch (JwtException e){
            String errorMessage;
            if (e instanceof ExpiredJwtException) {
                errorMessage = "El token JWT ha expirado.";
                logger.error(errorMessage, e.getMessage());
            } else if (e instanceof MalformedJwtException) {
                errorMessage = "El token JWT es inválido.";
                logger.error(errorMessage, e.getMessage());
            } else if (e instanceof SignatureException) {
                errorMessage = "La validación de la firma del token ha fallado.";
                logger.error(errorMessage, e.getMessage());
            } else {
                errorMessage = "Error en el procesamiento del token JWT.";
                logger.error(errorMessage, e.getMessage());
            }
            throw new JwtException(errorMessage, e);
        }
    }
}
