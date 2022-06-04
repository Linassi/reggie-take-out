package com.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        /*
         * 1. 获取本次请求URI
         * 2. 判断本次请求是否需要处理
         * 3. 如果不需要处理，则直接放行
         * 4. 判断登录状态，如果已登录，则直接放行
         * 5. 如果未登录则返回未登录结果
         * */

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        /*log.info("拦截到请求：{}",request.getRequestURI());
        filterChain.doFilter(request,response);*/
        //1. 获取本次请求URI
        log.info("拦截到请求：{}",request.getRequestURI());
        String requestURI = request.getRequestURI();
        //定义不需要处理的请求路径
        String[] urls = new String[]{"/employee/login","/employee/logout","/backend/**","/front/**"};
        //2. 判断本次请求是否需要处理
        boolean check = check(urls,requestURI);
        //3. 如果不需要处理，则直接放行
        if (check){
            log.info("无需处理，直接放行");
            filterChain.doFilter(request,response);
            return;
        }
        //4. 判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null){

            long id = Thread.currentThread().getId();
            log.info("过滤器线程id为：{}",id);

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            log.info("{} 已登录",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }
        //5. 如果未登录则返回未登录结果,通过输出流方式
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配
     * @param requestURI
     * @param urls
     * @return
     */
    public boolean check(String[] urls, String requestURI){
        for (String url: urls
             ) {
            boolean match = PATH_MATCHER.match(url,requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
