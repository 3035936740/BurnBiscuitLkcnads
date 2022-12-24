package com.binglkcnads.dao.chart;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;

/** 铺面设计 **/
@Data
@AllArgsConstructor
@Repository
public class ChartDesign implements Serializable {
    @Serial
    private static final long serialVersionUID = 4L;
    public ChartDesign(){
        super();
    }

    private String EZ;
    private String HD;
    private String IN;
    private String AT;
    private String LG;
    private String SP;
}
