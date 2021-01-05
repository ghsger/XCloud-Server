package cn.zf233.xcloud.time;

import cn.zf233.xcloud.service.FileService;
import cn.zf233.xcloud.service.UserService;
import org.csource.common.MyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Created by zf233 on 2020/11/4
 */
@Component
@EnableAsync
public class XcloudSystemTimingTask {

    @Resource
    private UserService userService;
    @Resource
    private FileService fileService;

    @Scheduled(cron = "0 0 1 * * ?")
    @Async(value = "taskScheduler")
    public void refreshUserLevel() {
        userService.refreshUserLevelTask();
    }

    @Scheduled(cron = "0 0 1 * * ?")
    @Async(value = "taskScheduler")
    public void clearUserLoginDetail() {
        userService.clearUserLoginDetailTask();
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Async(value = "taskScheduler")
    public void filePersistence() {
        try {
            fileService.filePersistenceTask();
            fileService.requestFileFastTask();
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
    }
}
