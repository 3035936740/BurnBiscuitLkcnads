package com.binglkcnads.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/** 这个是根据定数存取曲目的地方 **/
@Data
@AllArgsConstructor
@Repository
public class PhigrosRating implements Serializable {
    @Serial
    private static final long serialVersionUID = 7L;
    public PhigrosRating(){
        super();
    }

    private PhigrosMainData song_list;
}
