package com.maple.script;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by maple on 2019/12/2 11:34
 * 对应app中数据的id;
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface App {
    String value();//APP内对应的内容resource-id
}
