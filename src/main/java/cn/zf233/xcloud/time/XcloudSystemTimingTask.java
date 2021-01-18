package cn.zf233.xcloud.time;

import cn.zf233.xcloud.service.UserService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by zf233 on 2020/11/4
 */
@Component
@EnableAsync
public class XcloudSystemTimingTask {

    @Resource
    private UserService userService;

    @Scheduled(cron = "0 0 0 * * ?")
    @Async(value = "taskScheduler")
    public void refreshUserLevel() {
        userService.refreshUserLevelTask();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Async(value = "taskScheduler")
    public void clearUsersServerDetailCache() {
        userService.clearUsersServerDetailCacheTask();
    }

}
