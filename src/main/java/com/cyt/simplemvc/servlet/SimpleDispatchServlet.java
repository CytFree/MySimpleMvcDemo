package com.cyt.simplemvc.servlet;

import com.cyt.simplemvc.annotation.*;
import com.cyt.simplemvc.controller.MyTestController;
import com.cyt.simplemvc.util.ConvertStringToBaseType;
import com.cyt.simplemvc.util.ScanClassUtil;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author CaoYangTao
 * @date 2018年11月12日 21:43
 * @desc
 */
public class SimpleDispatchServlet extends HttpServlet {
    private Map<String, Object> beans = new ConcurrentHashMap<>(8);
    private Map<String, Object> urlBeans = new ConcurrentHashMap<>(8);
    private Map<String, Method> urlMethods = new ConcurrentHashMap<>(8);

    @Override
    public void init() throws ServletException {
        /**
         * 1、扫包，把所有controller的bean加入上下文
         * 2、获取controller里面带了请求注解的方法，并拼接成具体的URL
         * 3、将url和方法关联起来放入Map
         */
        String scanPackage = getServletContext().getInitParameter("scanPackage");
        initBeans(scanPackage);
        initIoc();
        initUrlMapping();
    }

    private Object findByType(Class<?> type) {
        if (beans != null && beans.size() > 0) {
            for (Object object : beans.values()) {
                if (type.isAssignableFrom(object.getClass())) {
                    return object;
                }
            }
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=utf-8");
        /**
         * 1、获取请求URL
         * 2、找URL对应的Controller的bean
         * 3、根据URL找对应的方法
         * 4、根据反射机制获取方法
         * 5、根据反射机制执行方法
         */
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String url = uri.replaceFirst(contextPath, "");
        if (!urlBeans.containsKey(url)) {
            resp.getWriter().write("没有找到对应的Controller");
            return;
        }
        if (!urlMethods.containsKey(url)) {
            resp.getWriter().write("没有找到对应的Mapping");
            return;
        }
        Object obj = urlBeans.get(url);
        Method method = urlMethods.get(url);
        try {
            Object page;
            Parameter[] parameters = method.getParameters();
            if (parameters.length <= 0) {
                 page = method.invoke(obj);
            } else {
                Object[] parameterValues = new Object[parameters.length];
                for (int i=0; i<parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    Class clazz = parameter.getType();
                    if (clazz.isAssignableFrom(HttpServletResponse.class) || clazz.isAssignableFrom(HttpServletRequest.class)
                            || clazz.isAssignableFrom(HttpSession.class)) {
                        parameterValues[i] = getDefaultParamValue(clazz, req, resp);
                    } else {
                        MyRequestParam myRequestParam = parameter.getAnnotation(MyRequestParam.class);
                        String paramName = parameter.getName();
                        if(myRequestParam != null && StringUtils.isNotBlank(myRequestParam.value())) {
                            paramName = myRequestParam.value();
                        }

                        if (clazz.isArray()) {
                            String[] paramValue = req.getParameterValues(paramName);
                        } else {
                            String paramValue = req.getParameter(paramName);
                            if (clazz == String.class) {
                                parameterValues[i] = paramValue;
                            } else {
                                parameterValues[i] = ConvertStringToBaseType.convert(paramValue, clazz);
                            }
                        }
                    }
                }
                page = method.invoke(obj, parameterValues);
            }
            if (page != null) {
                req.getRequestDispatcher(modelView(page.toString())).forward(req, resp);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private Object getDefaultParamValue(Class clazz, HttpServletRequest req, HttpServletResponse resp) {
        if (clazz.isAssignableFrom(HttpServletRequest.class)) {
            return req;
        }
        if (clazz.isAssignableFrom(HttpServletResponse.class)) {
            return resp;
        }
        if (clazz.isAssignableFrom(HttpSession.class)) {
            return req.getSession();
        }
        return null;
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    private void initBeans(String scanPackage) {
        List<Class<?>> classList = ScanClassUtil.getClassList(scanPackage, true);
        for (Class clazz : classList) {
            try {
                if (clazz.isInterface() || clazz.isAnnotation() || clazz.isAnonymousClass()) {
                    //接口或者注解直接过
                    continue;
                }
                Object object = clazz.newInstance();
                if (clazz.isAnnotationPresent(MyController.class)) {
                    MyController myController = (MyController) clazz.getAnnotation(MyController.class);
                    String beanName = myController.value();
                    beans.put(StringUtils.isBlank(beanName) ? clazz.getSimpleName() : beanName, object);
                } else if (clazz.isAnnotationPresent(MyService.class)) {
                    MyService myService = (MyService) clazz.getAnnotation(MyService.class);
                    String beanName = myService.value();
                    beans.put(StringUtils.isBlank(beanName) ? clazz.getSimpleName() : beanName, object);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void initUrlMapping() {
        for (Object obj : beans.values()) {
            MyController myController = obj.getClass().getAnnotation(MyController.class);
            if (myController == null) {
                continue;
            }
            StringBuffer urlPrefix = new StringBuffer();
            MyRequestMapping requestMapping = obj.getClass().getAnnotation(MyRequestMapping.class);
            if (requestMapping != null) {
                urlPrefix.append(requestMapping.value());
            }

            Method[] methods = obj.getClass().getDeclaredMethods();
            for (Method method : methods) {
                StringBuffer url = urlPrefix;
                MyRequestMapping methodAnnotation = method.getAnnotation(MyRequestMapping.class);
                if (methodAnnotation != null) {
                    url.append(methodAnnotation.value());
                    urlBeans.put(url.toString(), obj);
                    urlMethods.put(url.toString(), method);
                }
            }
        }
    }

    private String modelView(String page) {
        if (!page.startsWith("/")) {
            page = "/" + page;
        }
        return page + ".jsp";
    }

    private void initIoc() {
        //依赖注入
        if (beans != null && beans.size() > 0) {
            for (Object object : beans.values()) {
                Class clazz = object.getClass();
                Field[] fields = clazz.getDeclaredFields();
                if (fields != null && fields.length > 0) {
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(MyAutowired.class)) {
                            MyAutowired myAutowired = field.getAnnotation(MyAutowired.class);
                            String beanName = myAutowired.value();
                            Object bean;
                            if (StringUtils.isNotBlank(beanName)) {
                                bean = beans.get(beanName);
                            } else {
                                bean = findByType(field.getType());
                            }

                            try {
                                field.setAccessible(true);
                                field.set(object, bean);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        MyTestController controller = new MyTestController();
        Method[] methods = controller.getClass().getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method.getName() + "::");
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                Class clazz = parameter.getType();
                if (clazz.isAssignableFrom(HttpServletResponse.class) || clazz.isAssignableFrom(HttpServletRequest.class)
                        || clazz.isAssignableFrom(HttpSession.class)) {

                } else {
                    if (parameter.isNamePresent()) {
                        System.out.println("参数名称`   ```````：" + parameter.getName());
                        System.out.println("参数类型`   ```````：" + parameter.getType());
                        System.out.println(Number.class.isAssignableFrom(parameter.getType()));
                    }
                }
            }
        }
    }
}
