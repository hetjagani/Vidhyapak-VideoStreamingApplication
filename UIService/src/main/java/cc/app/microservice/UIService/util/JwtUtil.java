package cc.app.microservice.UIService.util;

import io.jsonwebtoken.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JwtUtil {
    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 1000 * 60 * 60 * 10;
    private static final String SECRET_KEY = "secret";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Collection extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Collection authorities = Arrays.stream(claims.get("roles").toString().split(",")).collect(Collectors.toList());
        return authorities;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}
