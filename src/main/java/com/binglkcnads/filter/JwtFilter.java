package com.binglkcnads.filter;

import com.binglkcnads.common.utils.JWTUtil;
import com.binglkcnads.common.utils.RSAUtil;
import com.binglkcnads.common.utils.StringUtils;
import com.binglkcnads.dao.resource.GlobalStaticClass;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Component
public class JwtFilter extends HandlerInterceptorAdapter {
    public static final String LOGIN_URL = "/login";

    @Resource
    private JWTUtil jwtTokenUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws SignatureException {
        String uri = request.getRequestURI();
        if(uri.contains(LOGIN_URL) || uri.contains("/doc.html") || uri.contains("/swagger-resources") ){
            return true;
        }
        //获取私钥
        String privateKey = GlobalStaticClass.getRSAPrivateKey();
        //获取token
        String token = request.getHeader(jwtTokenUtil.header);
        if(StringUtils.isEmpty(token)){
            token = request.getParameter(jwtTokenUtil.header);
        }
        if(StringUtils.isEmpty(token)){
            throw new SignatureException(jwtTokenUtil.header+"不能为空");
        }
        try {
            token = RSAUtil.privateDecrypt(token, privateKey); //传入密文和私钥,得到明文
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("RSA解密失败!");
        }
        //判断token是否超时
        Claims claims = jwtTokenUtil.getTokenClaim(token);
        if(null == claims || jwtTokenUtil.isTokenExpired(claims.getExpiration())){
            throw new SignatureException(jwtTokenUtil.header+"失效，请重新登录");
        }
        return true;
    }
}
