package springbootdemo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CaoYangTao
 * @date 2018年05月05日 19:42
 * @desc
 */
@RestController
public class HelloController {
    @RequestMapping("Hello")
    public String hello() {
        return "Hello SpringBoot";
    }
}
