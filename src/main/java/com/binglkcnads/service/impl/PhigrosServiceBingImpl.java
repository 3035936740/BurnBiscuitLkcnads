package com.binglkcnads.service.impl;

import com.alibaba.fastjson.JSON;
import com.binglkcnads.common.core.redis.RedisService;
import com.binglkcnads.dao.*;
import com.binglkcnads.dao.param.AliasParam;
import com.binglkcnads.dao.param.DelAliasParam;
import com.binglkcnads.exception.exceptpack.*;
import com.binglkcnads.mappers.PhigrosMainMapper;
import com.binglkcnads.service.PhigrosServiceBing;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class PhigrosServiceBingImpl implements PhigrosServiceBing {
    Log log = LogFactory.getLog(PhigrosServiceBingImpl.class);

    @SuppressWarnings("all")
    @Autowired
    private PhigrosMainMapper phigrosMainMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 用于查询曲目
     * @param id id的查询
     * @param name 别名或模糊的查询
     * @return 返回曲目列表
     * */
    @Override
    public List<PhigrosMainData> findBySong(@Nullable Integer id, @Nullable String name) {
        try {
            log.info("查询的曲目的id:"+id+"\t查询的曲目名称:"+name);
            if (!Objects.isNull(id) && !Objects.isNull(name))
            {
                log.error("这种事情怎么想都是不被允许的吧!两个都要就是有些太贪心了啊!");
                throw new ManyParameterSelectionsException();
            }
            List<PhigrosMainData> list = null;
            /* 0: 全查询 */
            if (Objects.isNull(id) && Objects.isNull(name))
            {
                log.info("0.全查询");
                list = phigrosMainMapper.findBySong(null,null);
                showQueryContent(list,0);
                return list;
            }else log.error("0.全查询不通过");
            /* 1: 精准查询*/
            Integer alias_id = phigrosMainMapper.findByAliasMainId(name);
            if (!Objects.isNull(alias_id)){
                log.info("1.精准别名查询成功!");
                list = phigrosMainMapper.findBySongSingleWhereId(alias_id);
                showQueryContent(list,1);
                return list;
            }else log.error("1.精准别名查询失败!");

            if (!Objects.isNull(name)){
                list = phigrosMainMapper.findBySongSingleWhereName(name);
            }
            
            if (!Objects.isNull(list) && !list.isEmpty()){
                log.info("2.精准一般查询成功!");
                showQueryContent(list,2);
                return list;
            }else log.error("2.精准一般查询失败!");

            /* 2: 模糊查询 */
            List<Integer> idList = phigrosMainMapper.findByAliasMainIdList(name);
            if (!Objects.isNull(id)){
                list = phigrosMainMapper.findBySong(Collections.singletonList(id),null);
            }
            if (!Objects.isNull(name)){
                list = phigrosMainMapper.findBySong(null,name);
            }
            if (!Objects.isNull(list) && !list.isEmpty()) {
                log.info("3.一般模糊查询");
                showQueryContent(list,3);
                return list;
            }
            else log.error("3.一般模糊查询不通过");
            if (!Objects.isNull(idList) && !idList.isEmpty()){
                log.info("4.别名模糊查询");
                list = phigrosMainMapper.findBySong(idList,null);
                showQueryContent(list,4);
                return list;
            }
            else log.error("4.别名模糊查询不通过");
            return phigrosMainMapper.findBySong(Collections.singletonList(-1),null);
        }catch (ManyParameterSelectionsException e){
               throw new ManyParameterSelectionsException();
        }catch (Exception e){
            log.error("5.发生未知错误");
            throw new OtherException(e.getMessage());
            //return phigrosMainMapper.findBySong(Collections.singletonList(-1),null);
        }
    }

    /**
     * 用于删除别名
     * @param delAliasParam 删除别名用的参数列表
     * @return 返回成功为ok
     * */
    @Override
    public String delByAlias(DelAliasParam delAliasParam) {
        String alias = delAliasParam.getAlias();
        Integer id = delAliasParam.getId();
        String msg;
        if(!Objects.isNull(alias) && !Objects.isNull(id) ||
            Objects.isNull(alias) && Objects.isNull(id)) {
            msg = "参数填写有误或参数为空";
            log.error(msg);
            throw new OtherException(msg);
        }
        try {
            String cache_str = null;
            if(!Objects.isNull(alias)) {
                if(phigrosMainMapper.contentWhetherExist(null, alias) != 0){
                    cache_str = "song_cache::SimpleKey [null," + alias + "]";
                    phigrosMainMapper.delByAlias(alias, null);
                    redisService.deleteObject(cache_str);
                    log.info("别名删除别名,alias="+alias);
                }else {
                    msg = "别名不存在哦";
                    log.error(msg);
                    throw new NotExistContentException(msg);
                }
            }else {
                if(phigrosMainMapper.contentWhetherExist(id, null) != 0){
                    final String temporary = phigrosMainMapper.findByAliasWhereId(id);
                    redisService.deleteObject("song_cache::SimpleKey [null," + temporary + "]");
                    cache_str = "song_cache::SimpleKey [null," + temporary + "]";
                    phigrosMainMapper.delByAlias(null, id);
                    log.info("id删除别名,id="+id);
                }else {
                    msg = "别名id不存在哦";
                    log.error(msg);
                    throw new NotExistContentException(msg);
                }
            }
            log.info("缓存清除成功->"+cache_str);
            return "ok";
        }catch (Exception e){
            msg = "别名删除异常";
            assert log != null;
            log.error(msg);
            throw new OtherException(msg);
        }
    }

    /**
     * 用于添加别名
     * @param aliasParam 添加别名用的参数列表
     * @return 返回成功为ok
     * */
    @Override
    public String addByAlias(AliasParam aliasParam) {
        // name为查找名称,alias为添加别名
        String name,alias;
        name = aliasParam.getName();
        alias = aliasParam.getAlias();
        Integer aliasCorrespondingId = aliasParam.getSong_id();
        String msg;
        // song_id为查找到的id
        Integer song_id;
        try {
            boolean is_alias_exist = !Objects.isNull(alias);
            if(is_alias_exist && !Objects.isNull(aliasCorrespondingId)){
                log.info("id存在");
                clearPhiCacheTable(alias, aliasCorrespondingId);
                return "ok";
            }else {
                log.error("id添加失败");
            }
            if(is_alias_exist && !Objects.isNull(name)){
                song_id = phigrosMainMapper.findByAliasMainId(name);
                if (Objects.isNull(song_id)){
                    log.info("貌似找不到存在的别名");
                    song_id = phigrosMainMapper.findByIdWhereName(name);
                    log.info("通过名称查找到了!");
                }
                if (Objects.isNull(song_id)){
                    log.error("没查找到id");
                }else {
                    clearPhiCacheTable(alias, song_id);
                    return "ok";
                }
            }
            msg = "别名添加失败";
            log.error(msg);
            throw new AddContentFailureException(msg);
        }catch (DuplicateKeyException e) {
            msg = "别名添加异常,有可能是别名已经存在了";
            log.error(msg);
            throw new DuplicateKeyException(msg);
        }catch (DataIntegrityViolationException e){
            msg = "糟糕填写的是一个不存在的曲名";
            log.error(msg);
            throw new DataIntegrityViolationException(msg);
        }catch (Exception e){
            msg = "发生未知的错误";
            log.error(msg);
            throw new UnknownException(msg);
        }
    }

    /**
     * 查找别名列表
     * @param id 通过曲目id查全部别名
     * @return 返回数据
     * */
    @Override
    public AliasToMain findByAliasToMainTable(@Nullable Integer id) {
        AliasToMain aliasToMain = new AliasToMain();
        try {
            aliasToMain.Clear();
            PhigrosMainData mainData;
            if(!Objects.isNull(id)){
                log.info("一般查询");
                mainData = phigrosMainMapper.findBySongSelectIdAndName(id,null);
                log.info("曲目:"+mainData.getSong_name()+"(id:"+ mainData.getSong_id() + ")");
            }else {
                log.error("id好像不存在呢");
                throw new NotExistContentException("id好像不存在呢");
            }
            aliasToMain.setSong_name(mainData.getSong_name());
            aliasToMain.setSong_id(mainData.getSong_id());
            aliasToMain.setAlias_list(phigrosMainMapper.findByAliasSeldom(mainData.getSong_id()));
            log.info("success-查询成功");
            return aliasToMain;
        }catch (NullPointerException e){
            log.error("err-糟糕!这个id不存在");
            throw new NullPointerException("糟糕!这个id不存在");
        } catch (Exception e){
            log.error("err-发生了未知的错误");
            throw new UnknownException("我也不到为什么发生这种事情啊!");
        }
    }

    //获取服务器的url
    @Value("${bing-lkcnads.server-ip-port}")
    private String URL;
    /**
     * 查询别名与曲目的封装(缝合)
     * @param id id查询
     * @return name 曲目查询
     * */
    @Override
    public Object findBySongWrapper(@Nullable Integer id,@Nullable String name) {
        final String single_line = "----------------------------------------------------";
        log.info(single_line);
        List<PhigrosMainDataWrapper> mainDataWrapperList = new ArrayList<>();
        String url_main_list  = "http://" + this.URL + "/api/pgr/findBySong?";
        if (!Objects.isNull(id) && !Objects.isNull(name))
        {
            throw new ManyParameterSelectionsException("参数选择过多");
        }
        try{
            if (!Objects.isNull(id)){
                url_main_list += "id="+id;
            }else if (!Objects.isNull(name)){
                url_main_list += "name="+name;
            }
            log.info("全歌请求url:" + url_main_list);

            List result = restTemplate.exchange(url_main_list, HttpMethod.GET, null, List.class).getBody();

            String s = JSON.toJSONString(result); //json转换有问题，需要重新转，并指明类型
            mainDataWrapperList = JSON.parseArray(s, PhigrosMainDataWrapper.class);// 指定转换的类型

            int count = 0;
            for (PhigrosMainDataWrapper mainDataWrapper : mainDataWrapperList) {
                Integer song_id = mainDataWrapper.getSong_id();
                log.info("曲目id -> " + song_id);
                String url_alias_list = "http://" + this.URL + "/api/pgr/findByAliasToMain?id=";
                url_alias_list += song_id;
                log.info("别名请求url:" + url_alias_list);
                AliasToMain temporary = restTemplate.exchange(url_alias_list, HttpMethod.GET, null, AliasToMain.class).getBody();
                assert temporary != null;
                mainDataWrapperList.get(count).setAlias_list(Objects.requireNonNull(temporary).getAlias_list());
                ++count;
            }
            log.info(single_line);
            return mainDataWrapperList;
        }
        catch (ManyParameterSelectionsException e){
            log.error("两个都要就是有些太贪婪啦~");
            log.info(single_line);
            throw new ManyParameterSelectionsException("参数选择过多,不要太贪婪了哦");
        }
        catch (Exception e){
            log.error(e.getMessage());
            log.info(single_line);
            throw new UnknownException("未知异常");
        }
    }

    /**
     * 通过定数查询曲目
     * @param rating1 定数区间1
     * @param rating2 定数区间2
     * @return 返回数据
     * */
    @Override
    public List<PhigrosRating> findByRatingToMainTable(final Float rating1,final Float rating2) {
        if (Objects.isNull(rating2)){
            log.info("查询:{"+ rating1 + "}");
            return phigrosMainMapper.findByRatingPhigrosRating(rating1 - 0.01f,rating1 + 0.01f);
        }else {
            log.info("查询:["+ rating1 + "," + rating2 +"]");
            return phigrosMainMapper.findByRatingPhigrosRating(rating1 - 0.01f,rating2 + 0.01f);
        }
    }

    /**
     * 用于展示查询显示的内容
     * @param list 列表
     * @param order_number 编号
     * */
    private void showQueryContent(@NotNull List<PhigrosMainData> list,@Nullable Integer order_number){
        Short count = 0;
        for (PhigrosMainData mainData:list) {
            ++count;
            log.info(order_number + "->(" + count + ").曲目:"+mainData.getSong_name()+"(id:"+ mainData.getSong_id() + ")");
        }
    }


    /**
     * 清除缓存列表
     * @param alias 别名
     * @param id 曲目id
     * */
    private void clearPhiCacheTable(String alias, Integer id) {
        phigrosMainMapper.addByAlias(alias,id);
        final String cache_str = "song_cache::SimpleKey [null," + alias + "]";
        final boolean hasDelete = redisService.deleteObject(cache_str);
        log.info("缓存清除成功->" + cache_str);
        log.info("是否清除上次的缓存->"+hasDelete);
        log.info("别名查询成功!");
        log.info("添加的别名为:"+alias+"(id:"+ id +")");
    }
}
