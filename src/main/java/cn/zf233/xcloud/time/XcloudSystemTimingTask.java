package cn.zf233.xcloud.time;

import cn.zf233.xcloud.service.FileService;
import cn.zf233.xcloud.service.UserService;
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

    private final Logger logger = LoggerFactory.getLogger(XcloudSystemTimingTask.class);

    @Resource
    private UserService userService;

    @Resource
    private FileService fileService;

    @Resource
    private RedisUtil redisUtil;

    @Scheduled(cron = "0 0 0 * * ?")
    public void refreshUserLevel() {
        // 刷新用户等级
        userService.refreshUserLevelTask();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void clearUsersServerDetailCache() {
        userService.removeUserInfoOfRegistFailTask();
        // 清空Redis缓存
        redisUtil.destroy();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void clearOSSWildFile() {
        // 清除无持久化记录
        fileService.removeDBWildFile();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void requestCountLogging() {
        // 记录当天请求
        logger.info("#####################################################################");
        logger.info(" ********TIMING TASK******* REQUEST COUNT ---------------> " + RequestCounter.getInstance().getRequestCount());
        logger.info(" ********TIMING TASK******* REQUEST SUCCESS -------------> " + RequestCounter.getInstance().getRequestSuccessCount());
        logger.info(" ********TIMING TASK******* REQUEST FAILURE -------------> " + RequestCounter.getInstance().getRequestFailureCount());
        logger.info("#####################################################################");
    }
}