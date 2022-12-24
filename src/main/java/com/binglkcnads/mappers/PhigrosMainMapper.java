package com.binglkcnads.mappers;

import com.binglkcnads.dao.*;
import com.binglkcnads.dao.chart.ChartDesign;
import com.binglkcnads.dao.chart.ChartNote;
import com.binglkcnads.dao.chart.ChartRating;
import org.apache.ibatis.annotations.*;
import reactor.util.annotation.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;

/** 肥鸽的映射 **/
@Mapper
public interface PhigrosMainMapper {
    /**
     * 模糊查询
     * 获取曲目
     * 不填写参数将会获取全部曲目
     * @param id 查询id
     * @param name 模糊查询名称
     * */
    List<PhigrosMainData> findBySong(@Nullable @Param("id") List<Integer> id,
                                     @Nullable @Param("name")  String name);
    /**
     * 单向查询
     * 通过name获取当个曲目
     * @param name 精准查询名称
     * */
    List<PhigrosMainData> findBySongSingleWhereName(@NotNull @Param("name")  String name);
    /**
     * 单向查询
     * 通过id获取当个曲目
     * @param id 精准查询id
     * */
    List<PhigrosMainData> findBySongSingleWhereId(@NotNull @Param("id") Integer id);
    /**
     * 查定数
     * @param rating1 查定数
     * @param rating2 查rating1到rating2之间的定数
     * */
    List<PhigrosRating> findByRatingPhigrosRating(@NotNull @Param("rating1") Float rating1,
                                            @Nullable @Param("rating2")Float rating2);
    /**
     * 查询定数
     * @param id 查询id
     * */
    @SuppressWarnings("unused")
    @Select("select * from phigros_table_new where song_id = #{id} or song_name = #{name};")
    PhigrosMainData findBySongSelectIdAndName(@Nullable @Param("id") Integer id,
                                              @Nullable @Param("name") String name);
    /**
     * 查询定数
     * @param id 查询id
     * */
    @SuppressWarnings("unused")
    @Select("select * from chart_rating where id = #{id};")
    ChartRating findByRatingChart(@Param("id")Integer id);
    /**
     * 查询Note数量
     * @param id 查询id
     * */
    @SuppressWarnings("unused")
    @Select("select * from chart_note where id = #{id};")
    ChartNote findByNoteChart(@Param("id")Integer id);
    /**
     * 查询是哪个神必写的谱子
     * @param id 查询id
     * */
    @SuppressWarnings("unused")
    @Select("select * from chart_design where id = #{id};")
    ChartDesign findByDesignChart(@Param("id")Integer id);
    /**
     * 查询到谱的id
     * @param name 通过曲名找到曲目id
     * */
    @SuppressWarnings("unused")
    @Select("select song_id from phigros_table_new where song_name = #{name};")
    Integer findByIdWhereName(@Param("name")String name);
    /**
     * 查询到谱的id
     * @param alias 通过别名找到曲目id
     * */
    @SuppressWarnings("unused")
    @Select("select song_id from pgr_alias where alias = #{alias};")
    Integer findByAliasMainId(@Param("alias")String alias);
    /**
     * 查询到谱的id列表
     * @param alias 通过别名找到曲目的多个id
     * */
    @SuppressWarnings("unused")
    @Select("select song_id from pgr_alias where alias like concat('%', #{alias},'%');")
    List<Integer> findByAliasMainIdList(@Param("alias")String alias);
    /**
     * 获取指定的曲目id全部别名
     * @param song_id 查询曲目id
     * */
    @SuppressWarnings("unused")
    @Select("select * from pgr_alias where song_id = #{song_id};")
    List<PhigrosAlias> findByAliasAll(@Param("song_id")Integer song_id);
    /**
     * 获取指定的曲目id少一样东西的查询
     * @param song_id 查询曲目id
     * */
    @SuppressWarnings("unused")
    @Select("select * from pgr_alias where song_id = #{song_id};")
    List<PhigrosAliasSeldom> findByAliasSeldom(@Param("song_id")Integer song_id);
    /**
     * 通过别名的id查询到别名
     * @param id 别名的id
     * */
    @SuppressWarnings("unused")
    @Select("select alias from pgr_alias where id = #{id};")
    String findByAliasWhereId(@Param("id")Integer id);

    /**
     * 判断是否存在数据
     * @param alias 通过曲目的id添加别名
     * @param id 别名的id
     * */
    @SuppressWarnings("unused")
    @Select("select COUNT(alias)+COUNT(id) from pgr_alias where id = #{id} or alias = #{alias};")
    Byte contentWhetherExist(@Nullable @Param("id") Integer id,@Nullable @Param("alias") String alias);


    /**
     * 添加别名
     * @param alias 通过曲目的id添加别名
     * */
    @SuppressWarnings("unused")
    @Insert("insert into pgr_alias(alias, song_id) values(#{alias}, #{song_id});")
    void addByAlias(@Param("alias")String alias,@Param("song_id")Integer song_id);

    /**
     * 删除别名
     * @param alias 通过别名名称删除别名
     * @param id 通过曲目id删除别名
     * */
    @SuppressWarnings("unused")
    @Delete("delete from pgr_alias where id = #{id} or alias = #{alias}")
    void delByAlias(@Param("alias")String alias,@Param("id")Integer id);
}
