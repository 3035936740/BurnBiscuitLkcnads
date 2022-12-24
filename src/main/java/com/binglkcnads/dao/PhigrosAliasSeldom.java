package com.binglkcnads.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;

/** 这里是存少量别名的哦 **/
@Data
@AllArgsConstructor
@Repository
public class PhigrosAliasSeldom implements Serializable {
    @Serial
    private static final long serialVersionUID = 8L;
    public PhigrosAliasSeldom(){
        super();
    }

    private Integer id;
    private String alias;
}
