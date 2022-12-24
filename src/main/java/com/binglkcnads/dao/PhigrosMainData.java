package com.binglkcnads.dao;

import com.binglkcnads.dao.chart.ChartDesign;
import com.binglkcnads.dao.chart.ChartNote;
import com.binglkcnads.dao.chart.ChartRating;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/** 这个就是查歌的了 **/
@Data
@AllArgsConstructor
@Repository
public class PhigrosMainData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public PhigrosMainData(){
        super();
    }

    Integer song_id;
    String song_name;
    String song_illustration_url;
    String song_audio_url;
    ChartRating rating;
    ChartNote note;
    ChartDesign design;
    String artist;
    String illustration;
    String duration;
    String bpm;
    String chapter;
}
