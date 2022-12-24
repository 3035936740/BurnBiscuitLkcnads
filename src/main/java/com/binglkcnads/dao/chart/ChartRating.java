package com.binglkcnads.dao.chart;

import com.binglkcnads.dao.PhigrosMainData;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/** 铺面的定数 **/
@Data
@AllArgsConstructor
@Repository
public class ChartRating implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
    public ChartRating(){
        super();
    }

    private Float EZ;
    private Float HD;
    private Float IN;
    private Float AT;
    private Float LG;
    private Float SP;
}
