package com.zuul.apigeteway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;


/**
 * 登录过滤器
 */
@Component
public class LoginFilter extends ZuulFilter {
    /**
     * 指定过滤器类型，为前置过滤器
     * @return
     */
    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    /**
     * 过滤器顺序，数值越小越先执行
     * @return
     */
    @Override
    public int filterOrder() {
        return 4;
    }

    /**
     * 设置过滤器是否生效，true or false
     * @return
     */
    @Override
    public boolean shouldFilter() {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        System.out.println(request.getRequestURI());
        System.out.println(request.getRequestURL());
        if("/apigeteway/order/api/v1/order/save".equalsIgnoreCase(request.getRequestURI())){
            return true;
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        System.out.println("进行拦截成功！");
        //JWT
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        //获取token
        String token = request.getHeader("token");
        //如果从header中获取不到，则从parameter中获取
        if(StringUtils.isBlank(token)){
            token = request.getParameter("token");
        }
        //登录检验逻辑
        if(StringUtils.isBlank(token)){
            //如果没有token，则设置zuul不往下走流程，再设置返回的错误码
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        return null;
    }
}
