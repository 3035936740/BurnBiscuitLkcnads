package com.binglkcnads.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;

/** 这里是存别名的哦 **/
@Data
@AllArgsConstructor
@Repository
public class PhigrosAlias implements Serializable {
    @Serial
    private static final long serialVersionUID = 5L;
    public PhigrosAlias(){
        super();
    }

    private Integer id;
    private String alias;
    private Integer song_id;
}
