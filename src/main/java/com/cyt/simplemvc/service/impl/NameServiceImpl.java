package com.cyt.simplemvc.service.impl;

import com.cyt.simplemvc.annotation.MyService;
import com.cyt.simplemvc.service.NameService;

/**
 * @author CaoYangTao
 * @date 2018/11/23  16:01
 */
@MyService("nameService")
public class NameServiceImpl implements NameService {

    @Override
    public String getStr(String name) {
        return "名称：" + name;
    }
}
