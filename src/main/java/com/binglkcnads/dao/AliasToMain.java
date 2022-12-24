package com.binglkcnads.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/** 别名对曲目主列表 **/
@Data
@AllArgsConstructor
@Repository
public class AliasToMain implements Serializable {
    @Serial
    private static final long serialVersionUID = 7L;

    public AliasToMain(){
        super();
    }
    private Integer song_id;
    private String song_name;
    List<PhigrosAliasSeldom> alias_list;

    public void Clear(){
        setSong_id(null);
        setSong_name(null);
        setAlias_list(null);
    }
}
