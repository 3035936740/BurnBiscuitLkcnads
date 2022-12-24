package com.binglkcnads.dao.Result;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;

@Data
@AllArgsConstructor
@Repository
public class ErrResult{

    @SuppressWarnings("unused")
    public static Map<String,Object> failedWith(final Date date, final Integer code, final String msg, final String comment){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String,Object> result = new HashMap<>();
        result.put("date",dateFormat.format(date));
        result.put("code",code);
        result.put("msg",msg);
        if(!Objects.isNull(comment))result.put("comment",comment);
        return result;
    }

    @SuppressWarnings("unused")
    public static Map<String,Object> failedWith(final Date date, final Integer code, final String msg){
        return failedWith(date, code, msg,null);
    }
}
