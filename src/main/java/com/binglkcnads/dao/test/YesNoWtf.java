package com.binglkcnads.dao.test;

import com.binglkcnads.dao.PhigrosAliasSeldom;
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
public class YesNoWtf implements Serializable {
    @Serial
    private static final long serialVersionUID = 10L;

    public YesNoWtf(){
        super();
    }
    private String answer;
    private boolean forced;
    private String image;
}
