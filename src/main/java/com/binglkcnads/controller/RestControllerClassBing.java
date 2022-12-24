package com.binglkcnads.controller;

import com.binglkcnads.common.utils.ip.IpUtils;
import com.binglkcnads.dao.AliasToMain;
import com.binglkcnads.dao.PhigrosMainData;
import com.binglkcnads.dao.PhigrosRating;
import com.binglkcnads.dao.param.AliasParam;
import com.binglkcnads.dao.param.DelAliasParam;
import com.binglkcnads.service.PhigrosServiceBing;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/** 统一的api页面 **/
@RestController
@RequestMapping("/api/pgr")
public class RestControllerClassBing {
    Log log = LogFactory.getLog(RestControllerClassBing.class);
    @Autowired
    private PhigrosServiceBing phigrosServiceBing;

    // 查找曲目
    @Cacheable(value = "song_cache")
    @GetMapping("findBySong")
    public List<PhigrosMainData> findBySong(@RequestParam(required = false) Integer id,
                                            @RequestParam(required = false) String name){
        return phigrosServiceBing.findBySong(id,name);
    }

    // 删除别名Delete(id/alias)
    @DeleteMapping("delAlias")
    public String delByAlias(@RequestBody DelAliasParam delAliasParam){
        return phigrosServiceBing.delByAlias(delAliasParam);
    }
    // 删除别名Get(id/alias)
    @GetMapping("delAliasGet")
    public String delByAlias(@RequestParam(required = false) String alias,
                             @RequestParam(required = false) Integer id){
        return phigrosServiceBing.delByAlias(new DelAliasParam(id,alias));
    }

    // 添加别名Post(song_id/name/alias)
    @PostMapping("addAlias")
    public String addByAlias(@RequestBody AliasParam aliasParam){
        return phigrosServiceBing.addByAlias(aliasParam);
    }

    // 添加别名Get(song_id/name/alias)
    @GetMapping("addAliasGet")
    public String addByAlias(@RequestParam String alias,
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) Integer song_id){
        return phigrosServiceBing.addByAlias(new AliasParam(name,song_id,alias));
    }

    // 查询别名列表
    @GetMapping("findByAliasToMain")
    public AliasToMain findByAliasToMain(@RequestParam Integer id){
        return phigrosServiceBing.findByAliasToMainTable(id);
    }

    // 查询定数
    @Cacheable(value = "song_cache")
    @GetMapping("findByRating")
    public List<PhigrosRating> findByRatingToMainData(@RequestParam                   Float rating1,
                                                      @RequestParam(required = false) Float rating2){
        return phigrosServiceBing.findByRatingToMainTable(rating1, rating2);
    }

    //对查歌的包装且可以获取别名
    @GetMapping("findBySongWrapper")
    public Object findByAliasToMain(@RequestParam(required = false) Integer id,
                                    @RequestParam(required = false) String name){
        return phigrosServiceBing.findBySongWrapper(id,name);
    }

    //对查歌的包装且可以获取别名(可缓存)
    @Cacheable(value = "song_wrapper_cache")
    @GetMapping("findBySongWrapperCache")
    public Object findByAliasToMainCache(@RequestParam(required = false) Integer id,
                                    @RequestParam(required = false) String name){
        return findByAliasToMain(id,name);
    }
    // 获取每次请求的ip地址
    @ModelAttribute
    public void ModelMethod(HttpServletRequest request){
        log.info("============================");
        log.info("访问 phigros 的 api");
        log.info("来自IP地址:" + IpUtils.getIpAddr(request));
        log.info("============================");
    }
}