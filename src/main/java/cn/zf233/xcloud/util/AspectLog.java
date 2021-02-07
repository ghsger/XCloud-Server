package cn.zf233.xcloud.util;

import cn.zf233.xcloud.commom.ServerResponse;
import cn.zf233.xcloud.entity.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Created by zf233 on 2021/1/19
 */
@Aspect
@Component
public class AspectLog {

    private final Logger logger = LoggerFactory.getLogger(AspectLog.class);

    // 切入点描述 这个是controller包的切入点
    @Pointcut("execution(public * cn.zf233.xcloud.web.*.*(..))")
    public void controllerLog() {
    }

    // 切入点描述 这个是service包的切入点
    @Pointcut("execution(public cn.zf233.xcloud.commom.ServerResponse cn.zf233.xcloud.service.impl.*.*(..))")
    public void serviceLog() {
    }

    @Around("controllerLog()")
    public Object aroundControllerLog(ProceedingJoinPoint joinPoint) throws Throwable {

        RequestCounter.getInstance().newRequestReceive();
        long beforeMillis = System.currentTimeMillis();

        // 这个RequestContextHolder是Springmvc提供来获得请求的东西
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            logger.info(" ********XCLOUD AOP******** REQUEST BEGIN ---------------> ");
            logger.info(" ********XCLOUD AOP******** URL -------------------------> " + request.getRequestURL().toString());
            logger.info(" ********XCLOUD AOP******** HTTP_METHOD -----------------> " + request.getMethod());
            logger.info(" ********XCLOUD AOP******** IP --------------------------> " + getIpAddress(request));
        }

        try {
            logger.info(" ********XCLOUD AOP******** THE ARGS OF THE CONTROLLER --> " + Arrays.toString(joinPoint.getArgs()));
            logger.info(" ********XCLOUD AOP******** CLASS_METHOD ----------------> " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());

            Object proceed = joinPoint.proceed();
            long afterMillis = System.currentTimeMillis();

            logger.info(" ********XCLOUD AOP******** RESPONSE TIME MILLIS COUNT --> " + (afterMillis - beforeMillis));
            logger.info(" ********XCLOUD AOP******** REQUEST END -----------------> ");

            return proceed;
        } catch (Throwable throwable) {
            logger.info(" ********XCLOUD AOP******** EXCEPTION -------------------> " + throwable.getMessage());

            throw throwable;
        }
    }

    @Around("serviceLog()")
    public Object aroundServiceLog(ProceedingJoinPoint joinPoint) throws Throwable {

        try {
            logger.info(" ********XCLOUD SERVICE**** SERVICE START ---------------> ");
            logger.info(" ********XCLOUD SERVICE**** THE ARGS OF THE CONTROLLER --> " + Arrays.toString(joinPoint.getArgs()));

            Object[] args = joinPoint.getArgs();
            User userInfo = null;

            for (Object arg : args) {
                if (arg instanceof User) {
                    userInfo = (User) arg;
                }
            }

            logger.info(" ********XCLOUD SERVICE**** CLASS_METHOD ----------------> " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());

            Object proceed = joinPoint.proceed();
            if (proceed instanceof ServerResponse) {
                ServerResponse serverResponseInfo = (ServerResponse) proceed;

                if (serverResponseInfo.isSuccess()) {

                    RequestCounter.getInstance().requestSuccess();

                    if (userInfo != null) {

                        logger.info(" ********XCLOUD SERVICE**** REQUEST SERVICE -------------> " + (userInfo.getId() == null ? userInfo.getUsername() : userInfo.getId()) + " 服务处理成功");
                    } else {
                        logger.info(" ********XCLOUD SERVICE**** REQUEST SERVICE -------------> " + "未知用户服务处理成功");
                    }
                } else {

                    RequestCounter.getInstance().requestFailure();

                    if (userInfo != null) {
                        logger.info(" ********XCLOUD SERVICE**** REQUEST SERVICE -------------> " + (userInfo.getId() == null ? userInfo.getUsername() : userInfo.getId()) + " 服务处理失败");
                    } else {
                        logger.info(" ********XCLOUD SERVICE**** REQUEST SERVICE -------------> " + "未知用户服务处理失败");
                    }
                }
            }

            logger.info(" ********XCLOUD SERVICE**** SERVICE END -----------------> ");

            return proceed;
        } catch (Throwable throwable) {
            logger.info(" ********XCLOUD SERVICE**** EXCEPTION -------------------> " + throwable.getMessage());

            throw throwable;
        }
    }

    // 获取原始IP
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个IP才是真实IP
            if (ip.contains(",")) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
