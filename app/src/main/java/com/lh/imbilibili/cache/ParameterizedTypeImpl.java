package com.lh.imbilibili.cache;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by liuhui on 2016/11/17.
 */

public class ParameterizedTypeImpl implements ParameterizedType {

    private Class raw;
    private Type[] types;

    @Override
    public Type[] getActualTypeArguments() {
        return types;
    }

    @Override
    public Type getRawType() {
        return raw;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
