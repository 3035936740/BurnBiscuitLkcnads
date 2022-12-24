package com.binglkcnads.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * Jwt Token 生成的工具类
 */

@Component
public class JWTUtil {
    @Value("${jwt.secret}")
    public String secret;
    @Value("${jwt.expire}")
    public long expire;
    @Value("${jwt.header}")
    public String header;

    /**
     * 生成token
     * @param subject 主题
     * @return
     */
    @SuppressWarnings("unused")
    public String createToken (final String subject,final Map<String, Object> claims){
        return createToken(subject, null, null,
                this.expire * 1000L,this.secret,claims);
    }

    /**
     * 生成token
     * @param id 唯一标识(唯一标识,防止重复使用)
     * @param subject 主题
     * @param claims 自定义载荷
     * @return
     */
    public String createToken (final String subject,final String id,final Map<String, Object> claims){
        return createToken(subject, id, null,
                this.expire * 1000L,this.secret,claims);
    }

    /**
     * 生成token
     * @param subject 主题
     * @param expire 到期时间(毫秒)
     * @param claims 自定义载荷
     * @return
     */
    public String createToken (final String subject,final Long expire,final Map<String, Object> claims){
        return createToken(subject, null, null,
                expire,this.secret,claims);
    }
    /**
     * 生成更多token
     * @param id 唯一标识(唯一标识,防止重复使用)
     * @param issuer 颁布者(匹配URL)
     * @param subject 主题
     * @param expire 到期时间(毫秒)
     * @param signature 设置签名(Key)
     * @param claims 自定义载荷
     * @return
     */
    public String createToken (final String subject,final String id,final String issuer
    ,final Long expire,final String signature,final Map<String, Object> claims
    ){
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + expire);
        JwtBuilder builder = Jwts.builder();
        //1.头(不设置,默认) 2 载荷(数据) 3. 签名
        builder.setSubject(subject)//就是描述 jwt的信息
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256,signature)
                .setId(id)
                .setIssuer(issuer);//生成令牌的一方
        // 3.可以自定义载荷
        /*
            例子
            claims.put("myaddress","cn");
            claims.put("mycity","sz");
        */
        builder.addClaims(claims);
        return builder.compact();
    }
    /**
     * 获取token中注册信息
     * @param token
     * @return
     */
    public Claims getTokenClaim (final String token) {
        try {
            return getTokenClaim(token,this.secret);
        }catch (Exception e){
            return null;
        }
    }

    public Claims getTokenClaim (final String token,final String secret) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        }catch (Exception e){
            return null;
        }
    }
    /**
     * 验证token是否过期失效
     * @param expirationTime
     * @return
     */
    @SuppressWarnings("unused")
    public boolean isTokenExpired (Date expirationTime) {
        return expirationTime.before(new Date());
    }

    /**
     * 获取token失效时间
     * @param token
     * @return
     */
    @SuppressWarnings("unused")
    public Date getExpirationDateFromToken(final String token) {
        return getTokenClaim(token).getExpiration();
    }
    /**
     * 获取用户名从token中
     */
    @SuppressWarnings("unused")
    public String getUsernameFromToken(final String token) {
        return getTokenClaim(token).getSubject();
    }

    /**
     * 获取jwt发布时间
     */
    @SuppressWarnings("unused")
    public Date getIssuedAtDateFromToken(final String token) {
        return getTokenClaim(token).getIssuedAt();
    }
}
