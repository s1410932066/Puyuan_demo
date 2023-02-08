package com.example.puyuan_demo.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.aspectj.weaver.patterns.IToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {
    private static  final  String SECRET_KEY = "59703373367639792F423F4528482B4D6251655468576D5A7134743777217A25";

    /**
     * 提取用戶名資訊
     * @param token
     * @return
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 提取Claims訊息
     * @param token
     * @param claimsResolver
     * @return
     * @param <T>
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * JWT 解析
     * 解析Token,確保訊息不備篡改
     * @param token
     * @return
     */
    private Claims extractAllClaims(String token){
        return Jwts
                //創建JWS解析器
                .parserBuilder()
                //設定簽名密鑰
                .setSigningKey(getSignInKey())
                .build()
                //解析token
                .parseClaimsJws(token)
                //返回解析後的JWT存在Claims
                .getBody();
    }

    /**
     * 密鑰生成
     * @return
     */
    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 簽發生成JWT
     */
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                //簽發時間,System.currentTimeMillis()為當前時間
                .setIssuedAt(new Date(System.currentTimeMillis()))
                //24小時候到期
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                //密鑰與演算方式
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 驗證用戶名與Token是否過期
     * @param token
     * @param userDetails
     * @return
     */
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * 查看Token是否過期
     * @param token
     * @return
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}


