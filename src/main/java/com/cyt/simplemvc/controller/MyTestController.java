package com.cyt.simplemvc.controller;

import com.cyt.simplemvc.annotation.MyAutowired;
import com.cyt.simplemvc.annotation.MyController;
import com.cyt.simplemvc.annotation.MyRequestMapping;
import com.cyt.simplemvc.annotation.MyRequestParam;
import com.cyt.simplemvc.service.NameService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author CaoYangTao
 * @date 2018/11/13  20:39
 */
@MyController
@MyRequestMapping("/test/")
public class MyTestController {

    @MyAutowired("nameService")
    private NameService nameService;

    @MyRequestMapping("index")
    public String index(@MyRequestParam(value = "cyt") String name, int age, HttpServletResponse response) {
        try {
            response.getWriter().write(nameService.getStr(name));
            System.out.println(nameService.getStr("index"));
            nameService.getStr("index");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "index";
    }
}
