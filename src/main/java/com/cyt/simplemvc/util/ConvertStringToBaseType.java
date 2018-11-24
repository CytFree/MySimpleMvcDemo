package com.cyt.simplemvc.util;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Arrays;

/**
 * @author CaoYangTao
 * @date 2018年11月24日 10:14
 * @desc
 */
public class ConvertStringToBaseType {
    /**
     * 基础类型
     */
    static String[] baseValueTypes = {"int", "long", "double", "float", "char", "byte", "short", "boolean"};

    /**
     * 基础类型的引用类型
     */
    static Class[] baseValueQuoteTypes = {Integer.class, Long.class, Double.class, Float.class, Character.class, Byte.class, Short.class, Boolean.class};

    public static Object convert(String value, Class clazz) {
        String className = clazz.getName();
        if (Arrays.asList(baseValueTypes).contains(className)) {
            return getBaseValue(value, className);
        } else if (Arrays.asList(baseValueQuoteTypes).contains(clazz)) {
            return getBaseQuoteValue(value, clazz);
        }
        return null;
    }

    private static Object getBaseValue(String value, String className) {
        switch (className) {
            case "int":
                return NumberUtils.toInt(value);
            case "long":
                return NumberUtils.toLong(value);
            case "double":
                return NumberUtils.toDouble(value);
            case "float":
                return NumberUtils.toFloat(value);
            case "char":
                return CharUtils.toChar(value);
            case "byte":
                return NumberUtils.toByte(value);
            case "short":
                return NumberUtils.toShort(value);
            case "boolean":
                return BooleanUtils.toBoolean(value);
            default:
        }
        return null;
    }

    private static Object getBaseQuoteValue(String value, Class clazz) {
        if (Integer.class == clazz) {
            return Integer.parseInt(value);
        } else if (Long.class == clazz) {
            return Long.parseLong(value);
        } else if (Double.class == clazz) {
            return Double.parseDouble(value);
        } else if (Float.class == clazz) {
            return Float.parseFloat(value);
        } else if (Character.class == clazz) {
            return CharUtils.toCharacterObject(value);
        } else if (Byte.class == clazz) {
            return Byte.parseByte(value);
        } else if (Short.class == clazz) {
            return Short.parseShort(value);
        } else if (Boolean.class == clazz) {
            return Boolean.parseBoolean(value);
        }
        return null;
    }
}
