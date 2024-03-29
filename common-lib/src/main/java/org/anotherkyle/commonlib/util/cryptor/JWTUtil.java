package org.anotherkyle.commonlib.util.cryptor;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.InputStream;
import java.security.KeyPair;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class JWTUtil {
    private static final String ISSUER = "anotherKyleFrom1994";
    private static JWTUtil instance;
    private final KeyPair keyPair;

    private JWTUtil(InputStream keyStoreData, String keyStorePwd) throws Exception {
        this.keyPair = RSAUtil.getInstance(keyStoreData, keyStorePwd).getKeyPair();
    }

    public static JWTUtil getInstance(InputStream keyStoreData, String keyStorePwd) throws Exception {
        if (instance == null) instance = new JWTUtil(keyStoreData, keyStorePwd);
        return instance;
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(keyPair.getPrivate()).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateTokenRS256(String subject, Map<String, Object> claims) {
        return buildBasicJwtInfo(claims, subject)
                .signWith(SignatureAlgorithm.RS256, keyPair.getPrivate()).compact();
    }

    public String generateTokenHS256(String subject, Map<String, Object> claims, String secret) {
        return buildBasicJwtInfo(claims, subject)
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    private JwtBuilder buildBasicJwtInfo(Map<String, Object> claims, String subject) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setHeader(headers)
                .setId(UUID.randomUUID().toString())
                .setIssuer(ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)); // Set expiration time
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}
