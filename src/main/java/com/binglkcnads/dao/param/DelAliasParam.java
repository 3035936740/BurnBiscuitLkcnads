package com.binglkcnads.dao.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

/** post传输别名参数 删除别名 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Repository
public class DelAliasParam {
    private Integer id;
    private String alias;
}
