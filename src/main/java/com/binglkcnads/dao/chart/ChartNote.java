package com.binglkcnads.dao.chart;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;

/** 铺面的note数量 **/
@Data
@AllArgsConstructor
@Repository
public class ChartNote implements Serializable {
    @Serial
    private static final long serialVersionUID = 3L;
    public ChartNote(){
        super();
    }

    private Short EZ;
    private Short HD;
    private Short IN;
    private Short AT;
    private Short LG;
    private Short SP;
}
