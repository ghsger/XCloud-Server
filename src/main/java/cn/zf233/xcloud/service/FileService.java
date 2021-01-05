package cn.zf233.xcloud.service;

import cn.zf233.xcloud.commom.ServerResponse;
import cn.zf233.xcloud.entity.AbsolutePath;
import cn.zf233.xcloud.entity.File;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.vo.FileVo;
import org.csource.common.MyException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by zf233 on 2020/11/4
 */
public interface FileService {

    ServerResponse<List<FileVo>> home(User user, Integer parentId, Integer sortFlag, Integer sortType, String matchCode);

    ServerResponse saveFile(User user, File file, Integer parentId);

    ServerResponse createFolder(User user, String name, Integer parentId);

    ServerResponse removeFileOrFolder(Integer fileId, User user);

    File getFileByFileId(Integer fileId, Integer userId);

    List<AbsolutePath> getAbsolutePath(Integer userId, Integer parentId);

    void findChildParentId(Set<File> categorySet, Integer categoryId, Integer userId);

    void filePersistenceTask();

    void requestFileFastTask() throws IOException, MyException;

}