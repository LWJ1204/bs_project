package com.lwj.FinalServer.common.Utils;

import com.lwj.FinalServer.common.Exception.myException;
import com.lwj.FinalServer.common.result.ResultCodeEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private static SecretKey key =Keys.hmacShaKeyFor("lwj08213036qwer123456987456123qw".getBytes());
    public static String createToke(String uid,String UserName,Integer role,String deviceFingerprint,String ip){

         String jwt=Jwts.builder().setExpiration(new Date(System.currentTimeMillis()+3600000))
                .setSubject("Login_user")
                .claim("Id",uid)
                .claim("role",role)
                .claim("username",UserName)
                 .claim("fingerprint", deviceFingerprint)  // 新增设备指纹
                 .claim("IP",ip)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }

    public static Claims parseToken(String token){
        try
        {
            JwtParser parser = Jwts.parser().setSigningKey(key).build();
            Jws<Claims>jws=parser.parseClaimsJws(token);
            return jws.getBody();
        }
        catch (ExpiredJwtException e){
            throw new myException(ResultCodeEnum.TOKEN_INVALID);
        }catch (JwtException e){
            throw new myException(ResultCodeEnum.TOKEN_EXPIRED);
        }
    }
    public static void main(String[] args) {
        String pwd= DigestUtils.md5Hex("lwj20021204.");
        System.out.println("pwd:"+pwd);
    }
}
