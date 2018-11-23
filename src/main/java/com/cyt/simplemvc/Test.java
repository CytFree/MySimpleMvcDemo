package com.cyt.simplemvc;

import com.cyt.simplemvc.util.ScanClassUtil;

import java.util.List;

/**
 * @author CaoYangTao
 * @date 2018/11/13  20:28
 */
public class Test {
    public static void main(String[] args) {
        // 标识是否要遍历该包路径下子包的类名
        boolean recursive = true;
        // 指定的包名
        String pkg = "com.cyt";
        List list;
        // 增加 author.class的过滤项，即可只选出ClassTestDemo
        list = ScanClassUtil.getClassList(pkg, recursive);

        for (int i = 0; i < list.size(); i++) {
            System.out.println(i + ":" + list.get(i));
        }
    }
}
