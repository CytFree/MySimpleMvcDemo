package springbootdemo.service.dubbo.provider;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.cyt.service.demo.PersonService;
import org.springframework.stereotype.Component;

/**
 * @author CaoYangTao
 * @date 2018年05月07日 21:26
 * @desc
 */
@Component
@Service(group = "dubbo", interfaceClass = PersonService.class, version = "1.0.0")
public class PersonProviderServiceImpl implements PersonService{

    @Override
    public String getName() {
        return "CaoYangTao";
    }

    @Override
    public String getIdCard(){
        return "12132465766";
    }
}
