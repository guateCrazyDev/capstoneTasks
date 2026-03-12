package com.supportTicket.supportTicket.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtTokenUtil {
    private String secret = "48a3419bb4a84dde52f300f5a341b9f84f73979189aaef0658803b992333888991ca40d6eb8c958e575ab8eb88e2353f08df3cdf31d56c1fc42342165d97ddc094d5e64aeeda3e12fd28e016c83065cbf67e3c0bb097a98835bd3677402cb27c76587bdebc2259ef021501c1de274a09a37a9c0aa13034b34bf7a5ad51f02cb4fcaa01f323661cb7a5fc54291865dfe5d8fd42b7506b231c08c6c4ee1d5ced67abb31e75b636f9d92c00683e99692d43eb64c333a8cde83f4ff99edcd3ef28ffa0e9642cfcbc92b4b948b812ae039c8f7ed4039e30beb2d736108c852a60b60139442707530c92524f602ece4de17cd01db1ae6598d578407dadb48034f0aa67ca5cafdb03f87d8d266c1ab9bcb2cadd38858f31ffcd9a17e10d99feb1181d658033f5af72f3ed7e72152b04a12a39d7ecd647d52e5ceac66f33ecb25acd6aae";

    private Long expiration = 3600000L;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
