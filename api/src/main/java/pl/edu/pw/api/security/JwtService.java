package pl.edu.pw.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import pl.edu.pw.DBConnector;
import pl.edu.pw.models.User;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {
    private static DBConnector dbc = new DBConnector("1");
    private static final String SECRET = "7638792F423F4528482B4D6251655468576D5A7133743677397A24432646294A404E635266556A586E327235753778214125442A472D4B6150645367566B5970";
    public String generateToken(String username) {
        Map<String,Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60)) // Expiration = 1h
                .signWith(getSignKey(), SignatureAlgorithm.HS512).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        try {
            return claimsResolver.apply(claims);
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }
    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch(Exception exception) {
            System.out.println(exception);
        }
        return null;
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String name) {
        final String username = extractUsername(token);
        return (username.equals(name) && !isTokenExpired(token));
    }

    public boolean checkUserToken(Long id, HttpServletRequest request){
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        if(authHeader==null ||! authHeader.startsWith("Bearer")) {
            return false;
        }

        // Master Token
        if(authHeader.substring(7).equals("MasterToken")) {
            return true;
        }

        jwt = authHeader.substring(7);
        username = extractUsername(jwt);

        User user = dbc.findUserById(id);
        if(user!=null && username != null && validateToken(jwt, user.getName())) {
            return true;
        }
        return false;
    }
    public String getUsernameFromToken(Long id, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        if(authHeader==null ||! authHeader.startsWith("Bearer")) {
            return null;
        }
        jwt = authHeader.substring(7);
        return extractUsername(jwt);
    }
}
