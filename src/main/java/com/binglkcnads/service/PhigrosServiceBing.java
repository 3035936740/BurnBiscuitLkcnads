package com.binglkcnads.service;

import com.binglkcnads.dao.AliasToMain;
import com.binglkcnads.dao.PhigrosMainData;
import com.binglkcnads.dao.PhigrosRating;
import com.binglkcnads.dao.param.AliasParam;
import com.binglkcnads.dao.param.DelAliasParam;

import java.util.List;

@SuppressWarnings("unused")
public interface PhigrosServiceBing {
    List<PhigrosMainData> findBySong(Integer id, String name);
    String delByAlias(DelAliasParam delAliasParam);
    String addByAlias(AliasParam aliasParam);
    AliasToMain findByAliasToMainTable(Integer id);
    Object findBySongWrapper(Integer id, String name);
    List<PhigrosRating> findByRatingToMainTable(Float rating1,Float rating2);
}
