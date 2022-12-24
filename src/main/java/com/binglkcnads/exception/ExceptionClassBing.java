package com.binglkcnads.exception;

import com.binglkcnads.dao.Result.ErrResult;
import com.binglkcnads.exception.exceptpack.*;
import io.jsonwebtoken.SignatureException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** 处理异常页面 **/
@ControllerAdvice
public class ExceptionClassBing {
    Log log = LogFactory.getLog(ExceptionClassBing.class);
    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,Object> httpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e){
        log.error("请求方式错误");
        return ErrResult.failedWith(new Date(), 405,"请求方式错误","你是不是POST请求用GET请求了?或者说是发过来的?");
    }

    @ExceptionHandler(value = {NoSuchAlgorithmException.class})
    @ResponseBody
    public Map<String,Object> noSuchAlgorithm(NoSuchAlgorithmException e){
        log.error("无搜索算法异常");
        return ErrResult.failedWith(new Date(), 500,"无搜索算法异常");
    }

    @ExceptionHandler(value = {InvalidKeyException.class})
    @ResponseBody
    public Map<String,Object> invalidKey(InvalidKeyException e){
        log.error("密钥无效");
        return ErrResult.failedWith(new Date(), 401,"密钥无效");
    }
    @ExceptionHandler(value = {NullPointerException.class})
    @ResponseBody
    public Map<String,Object> nullPointer(NullPointerException e){
        log.error(e.getMessage());
        return ErrResult.failedWith(new Date(), 500,e.getMessage());
    }

    @ExceptionHandler(value = {AddContentFailureException.class})
    @ResponseBody
    public Map<String,Object> addContentFailure(AddContentFailureException e){
        log.error(e.getMessage());
        return ErrResult.failedWith(new Date(), 400,e.getMessage());
    }

    @ExceptionHandler(value = {DuplicateKeyException.class})
    @ResponseBody
    public Map<String,Object> duplicateKey(DuplicateKeyException e){
        log.error(e.getMessage());
        return ErrResult.failedWith(new Date(), 400,e.getMessage());
    }
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    @ResponseBody
    public Map<String,Object> missingServletRequestParameter(MissingServletRequestParameterException e){
        log.error("HTTP不可读!!");
        return ErrResult.failedWith(new Date(), 405,"没有传参数","怎么两手空空的来找我?");
    }

    @ExceptionHandler(value = {SignatureException.class})
    @ResponseBody
    public Map<String,Object> signature(SignatureException e){
        log.error("禁止访问!");
        return ErrResult.failedWith(new Date(), 403,"禁止访问","要是随便访问岂不是乱透了?");
    }

    @ExceptionHandler(value = {ManyParameterSelectionsException.class})
    @ResponseBody
    public Map<String,Object> manyParameterSelections(ManyParameterSelectionsException e){
        log.error("参数选择过多!");
        return ErrResult.failedWith(new Date(), 405,"参数选择过多","选择太多是一种很贪心的表现啦");
    }

    @ExceptionHandler(value = {NotExistContentException.class})
    @ResponseBody
    public Map<String,Object> notExistContent(NotExistContentException e){
        log.error(e.getMessage());
        return ErrResult.failedWith(new Date(), 404,e.getMessage());
    }

    @ExceptionHandler(value = {UnknownException.class})
    @ResponseBody
    public Map<String,Object> unknown(UnknownException e){
        log.error("发生未知错误!");
        return ErrResult.failedWith(new Date(), 400,"发生未知错误","希腊奶希腊奶,我真不知道这是发生了什么逆天的异常");
    }

    @ExceptionHandler(value = {OtherException.class})
    @ResponseBody
    public Map<String,Object> other(OtherException e){
        log.error(e.getMessage());
        return ErrResult.failedWith(new Date(), 400,e.getMessage());
    }
    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    @ResponseBody
    public Map<String,Object> httpMessageNotReadable(HttpMessageNotReadableException e){
        log.error("HTTP不可读!!");
        return ErrResult.failedWith(new Date(), 500,"HTTP解析异常","解析出错了呜呜");
    }
    @ExceptionHandler(value = {IOException.class})
    @ResponseBody
    public Map<String,Object> io(IOException e){
        log.error("IO流异常!!");
        return ErrResult.failedWith(new Date(), 500,"IO流异常");
    }
    @ExceptionHandler(value = {RuntimeException.class})
    @ResponseBody
    public Map<String,Object> runtime(RuntimeException e){
        log.error("运行时异常");
        return ErrResult.failedWith(new Date(), 500,"运行时异常");
    }

    @ExceptionHandler(value = {NoSuchMethodError.class})
    @ResponseBody
    public Map<String,Object> noSuchMethod(NoSuchMethodError e){
        log.error("无搜索方法错误");
        return ErrResult.failedWith(new Date(), 500,"无搜索方法错误");
    }

    @ExceptionHandler(value = {NullParameterException.class})
    @ResponseBody
    public Map<String,Object> nullParameter(NullParameterException e){
        log.error(e.getMessage());
        return ErrResult.failedWith(new Date(), 405,e.getMessage());
    }

    @ExceptionHandler(value = {SerializationException.class})
    @ResponseBody
    public Map<String,Object> serialization(SerializationException e){
        log.error(e.getMessage());
        return ErrResult.failedWith(new Date(), 500,"redis序列化异常");
    }
    // 统一处理异常
    /*@ExceptionHandler(value = Exception.class)
    public String exceptionPage(){
        log.error("页面异常!!!");
        return "/error";
    }*/

    /*
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String,Object> RestPageException(Exception ex){
        Map<String,Object> result = new HashMap<>();
        result.put("meg",ex.getMessage());
        return result;
    }
    */
}
