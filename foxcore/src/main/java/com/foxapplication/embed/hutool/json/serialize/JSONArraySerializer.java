package com.foxapplication.embed.hutool.json.serialize;

import com.foxapplication.embed.hutool.json.JSONArray;

/**
 * JSON列表的序列化接口，用于将特定对象序列化为{@link JSONArray}
 * 
 * @param <V> 对象类型
 * 
 * @author Looly
 */
@FunctionalInterface
public interface JSONArraySerializer<V> extends JSONSerializer<JSONArray, V>{}
