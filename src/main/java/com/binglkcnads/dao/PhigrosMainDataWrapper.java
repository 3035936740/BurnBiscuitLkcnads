package com.binglkcnads.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/** 这个就是查歌的包装 **/
@Data
@AllArgsConstructor
@Repository
public class PhigrosMainDataWrapper extends PhigrosMainData implements Serializable {
    @Serial
    private static final long serialVersionUID = 10L;

    public PhigrosMainDataWrapper(){
        super();
    }
    private List<PhigrosAliasSeldom> alias_list;


    @Override
    public String toString() {
        return "PhigrosMainData{" +
                "song_id=" + super.song_id +
                ", song_name='" + super.song_name + '\'' +
                ", song_illustration_url='" + super.song_illustration_url + '\'' +
                ", song_audio_url='" + super.song_audio_url + '\'' +
                ", rating=" + super.rating +
                ", note=" + super.note +
                ", design=" + super.design +
                ", artist='" + super.artist + '\'' +
                ", illustration='" + super.illustration + '\'' +
                ", duration='" + super.duration + '\'' +
                ", bpm='" + super.bpm + '\'' +
                ", chapter='" + super.chapter + '\'' +
                ", alias_list='" + alias_list + '\'' +
                '}';
    }
}
