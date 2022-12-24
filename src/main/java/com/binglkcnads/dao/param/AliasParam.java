package com.binglkcnads.dao.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

/** post传输别名参数 添加别名 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Repository
public class AliasParam {
    private String name;
    private Integer song_id;
    private String alias;
}
