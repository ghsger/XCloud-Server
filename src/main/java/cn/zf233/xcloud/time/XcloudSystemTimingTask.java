package cn.zf233.xcloud.time;

import cn.zf233.xcloud.service.UserService;
import cn.zf233.xcloud.util.AspectLog;
import cn.zf233.xcloud.util.RedisUtil;
import cn.zf233.xcloud.util.RequestCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by zf233 on 2020/11/4
 */
@Component
public class XcloudSystemTimingTask {

    private final Logger logger = LoggerFactory.getLogger(AspectLog.class);

    @Resource
    private UserService userService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RequestCounter requestCounter;

    @Scheduled(cron = "0 0 0 * * ?")
    public void refreshUserLevel() {

        // 刷新用户等级
        userService.refreshUserLevelTask();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void clearUsersServerDetailCache() {
        userService.removeUserInfoOfRegistFailTask();

        // 清空缓存
        redisUtil.destroy();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void requestCountLogging() {
        // 记录当天请求
        logger.info(" ********TIMING TASK******* REQUEST COUNT ---------------> " + requestCounter.getRequestCount());
        logger.info(" ********TIMING TASK******* REQUEST SUCCESS -------------> " + requestCounter.getRequestSuccessCount());
        logger.info(" ********TIMING TASK******* REQUEST FAILURE -------------> " + requestCounter.getRequestFailureCount());
    }
}
