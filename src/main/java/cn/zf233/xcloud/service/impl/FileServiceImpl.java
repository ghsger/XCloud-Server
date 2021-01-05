package cn.zf233.xcloud.service.impl;

import cn.zf233.xcloud.commom.Const;
import cn.zf233.xcloud.commom.ResponseCodeENUM;
import cn.zf233.xcloud.commom.ServerResponse;
import cn.zf233.xcloud.entity.AbsolutePath;
import cn.zf233.xcloud.entity.File;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.mapper.FileMapper;
import cn.zf233.xcloud.mapper.UserMapper;
import cn.zf233.xcloud.service.FileService;
import cn.zf233.xcloud.util.FastDFSUtil;
import cn.zf233.xcloud.util.RedisUtil;
import cn.zf233.xcloud.vo.FileVo;
import org.apache.commons.lang.StringUtils;
import org.csource.common.MyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Created by zf233 on 2020/12/25
 */
@Service
public class FileServiceImpl implements FileService {

    @Resource
    private FileMapper fileMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private RedisUtil filePermissionRedisUtil;

    @Override
    public ServerResponse<List<FileVo>> home(User user, Integer parentId, Integer sortFlag, Integer sortType, String matchCode) {
        List<File> files;
        if (parentId == null || parentId == -1) {
            File rootNode = fileMapper.selectRootNodeOfUserByPrimaryKey(user.getId());
            parentId = rootNode.getId();
        }
        File file = fileMapper.selectByPrimaryKey(parentId);
        if (file == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCodeENUM.ILLEGAL_ARGUMENT.getCode(), "文件夹不存在，请下拉以刷新");
        }
        List<AbsolutePath> absolutePath = null;
        if (StringUtils.isNotBlank(matchCode)) {
            files = fileMapper.selectFilesByUserIDAndMatchCode(user.getId(), matchCode);
        } else if (sortFlag != null) {
            if (sortType == 1) {
                files = fileMapper.selectFilesByUserIDAndParentIDSortByTypeDesc(user.getId(), parentId, Const.SortFieldENUM.fieldOf(sortFlag).getField());
            } else {
                files = fileMapper.selectFilesByUserIDAndParentIDSortByTypeAsce(user.getId(), parentId, Const.SortFieldENUM.fieldOf(sortFlag).getField());
            }
        } else {
            files = fileMapper.selectFilesByUserIDAndParentID(user.getId(), parentId);
            absolutePath = getAbsolutePath(user.getId(), parentId);
        }
        List<FileVo> fileVos = assembleFileVoDetail(files);
        return ServerResponse.createBySuccessAbsolutePathMsg("获取成功", fileVos, absolutePath);
    }

    @Override
    @Transactional
    public ServerResponse saveFile(User user, File file, Integer parentId) {
        if (parentId == null || parentId == -1) {
            File rootNode = fileMapper.selectRootNodeOfUserByPrimaryKey(user.getId());
            file.setParentId(rootNode.getId());
        } else {
            File fileOfParentId = fileMapper.selectByPrimaryKey(parentId);
            if (fileOfParentId == null) {
                return ServerResponse.createByErrorIllegalArgument("文件夹不存在,下拉以刷新");
            }
            file.setParentId(parentId);
        }
        User targetUser = userMapper.selectByPrimaryKey(user.getId());
        if (redisUtil.readUserUseCapacity(user) >= targetUser.getLevel() * 10) {
            return ServerResponse.createByErrorMessage("上传失败(空间已满)");
        }
        int resultFlag = fileMapper.insert(file);
        if (resultFlag > 0) {
            redisUtil.updateAddUserUseCapacity(user);
            return ServerResponse.createBySuccessMessage("上传成功");
        }
        return ServerResponse.createByErrorMessage("上传失败");
    }

    @Override
    @Transactional
    public ServerResponse createFolder(User user, String folderName, Integer parentId) {
        if (StringUtils.isBlank(folderName)) {
            return ServerResponse.createBySuccessMessage("创建失败");
        }
        File file = new File();
        if (parentId == null || parentId == -1) {
            File rootNode = fileMapper.selectRootNodeOfUserByPrimaryKey(user.getId());
            file.setParentId(rootNode.getId());
        } else {
            File fileOfParentId = fileMapper.selectByPrimaryKey(parentId);
            if (fileOfParentId == null) {
                return ServerResponse.createByErrorIllegalArgument("文件夹不存在,下拉以刷新");
            }
            file.setParentId(parentId);
        }
        file.setOldFileName(folderName);
        file.setUserId(user.getId());
        file.setFolder(1);
        file.setUploadTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        file.setUpdateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        int resultFlag = fileMapper.insert(file);
        if (resultFlag > 0) {
            return ServerResponse.createBySuccessMessage("创建成功");
        }
        return ServerResponse.createBySuccessMessage("创建失败");
    }

    @Override
    @Transactional
    public ServerResponse removeFileOrFolder(Integer fileId, User user) {
        File targetFile = fileMapper.selectByPrimaryKey(fileId);
        if (targetFile == null) {
            return ServerResponse.createByErrorIllegalArgument("文件或文件夹不存在");
        }
        Set<File> filesSet = new HashSet<>();
        findChildParentId(filesSet, fileId, user.getId());
        for (File file : filesSet) {
            if (file.getFolder() == 0) {
                if (StringUtils.isNotBlank(file.getRedisCacheName())) {
                    redisUtil.removeFile(file.getRedisCacheName());
                }
                if (StringUtils.isNotBlank(file.getGroupName()) && StringUtils.isNotBlank(file.getRemoteFilePath())) {
                    try {
                        FastDFSUtil.delete(file.getGroupName(), file.getRemoteFilePath());
                    } catch (IOException | MyException e) {
                        e.printStackTrace();
                        ServerResponse.createByErrorMessage("删除异常");
                    }
                }
                redisUtil.updateReduceUserUseCapacity(user);
            }
            fileMapper.deleteByPrimaryKey(file.getId());
        }
        return ServerResponse.createBySuccessMessage("共计删除:" + filesSet.size() + "个文件或文件夹");
    }

    @Override
    public File getFileByFileId(Integer fileId, Integer userId) {
        File file = fileMapper.selectByPrimaryKey(fileId);
        if (file.getFolder() == 0) {
            file.setDownloadCount(file.getDownloadCount() + 1);
            fileMapper.updateByPrimaryKeySelective(file);
        }
        return file;
    }

    @Override
    public List<AbsolutePath> getAbsolutePath(Integer userId, Integer parentId) {
        File fileOfParentId = fileMapper.selectByPrimaryKey(parentId);
        if (fileOfParentId == null) {
            File rootNode = fileMapper.selectRootNodeOfUserByPrimaryKey(userId);
            parentId = rootNode.getId();
        }
        List<AbsolutePath> absolutePath = new ArrayList<>();
        absolutePath = findAbsolutePathByParentId(absolutePath, parentId);
        Collections.reverse(absolutePath);
        return absolutePath;
    }

    // 递归查询所有子节点(文件&文件夹)
    @Override
    public void findChildParentId(Set<File> filesSet, Integer categoryId, Integer userId) {
        File fileOfParentId = fileMapper.selectByPrimaryKey(categoryId);
        if (fileOfParentId != null) {
            filesSet.add(fileOfParentId);
        }
        List<File> filesOfParentId = fileMapper.selectFilesByUserIDAndParentID(userId, categoryId);
        for (File file : filesOfParentId) {
            findChildParentId(filesSet, file.getId(), userId);
        }
    }

    @Override
    @Transactional
    public void filePersistenceTask() {
        List<File> files = fileMapper.selectFiles();
        for (File file : files) {
            if (file.getFolder() == 0) {
                if (StringUtils.isBlank(file.getGroupName()) && StringUtils.isBlank(file.getRemoteFilePath())) {
                    try {
                        byte[] fileBytes = filePermissionRedisUtil.getFile(file.getRedisCacheName());
                        String fileExtName = file.getOldFileName().substring(file.getOldFileName().lastIndexOf(".") + 1);
                        String[] upload = FastDFSUtil.upload(fileBytes, fileExtName);
                        file.setGroupName(upload[0]);
                        file.setRemoteFilePath(upload[1]);
                        fileMapper.updateByPrimaryKeySelective(file);
                    } catch (IOException | MyException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void requestFileFastTask() throws IOException, MyException {
        List<File> files = fileMapper.selectFiles();
        for (File file : files) {
            if (file.getFolder() == 0 & StringUtils.isNotBlank(file.getGroupName()) && StringUtils.isNotBlank(file.getRemoteFilePath())) {
                byte[] download;
                if (StringUtils.isNotBlank(file.getRedisCacheName())) {
                    download = filePermissionRedisUtil.getFile(file.getRedisCacheName());
                    filePermissionRedisUtil.removeFile(file.getRedisCacheName());
                } else {
                    download = FastDFSUtil.download(file.getGroupName(), file.getRemoteFilePath());
                }
                String redisCacheName = filePermissionRedisUtil.saveFile(download);
                file.setRedisCacheName(redisCacheName);
                fileMapper.updateByPrimaryKeySelective(file);
            }
        }
    }

    public List<AbsolutePath> findAbsolutePathByParentId(List<AbsolutePath> absolutePath, Integer parentId) {
        File fileOfParentId = fileMapper.selectByPrimaryKey(parentId);
        if (fileOfParentId != null) {
            if (fileOfParentId.getParentId() != -1) {
                AbsolutePath nodeOfFile = new AbsolutePath();
                nodeOfFile.setParentid(fileOfParentId.getId());
                nodeOfFile.setFolderName(fileOfParentId.getOldFileName());
                absolutePath.add(nodeOfFile);
                File fileOfGrandParent = fileMapper.selectByPrimaryKey(fileOfParentId.getParentId());
                findAbsolutePathByParentId(absolutePath, fileOfGrandParent.getId());
            }
        }
        return absolutePath;
    }

    private List<FileVo> assembleFileVoDetail(List<File> files) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<FileVo> fileVos = new ArrayList<>();
        FileVo fileVo;
        for (File file : files) {
            fileVo = new FileVo();
            if (file.getFolder() == 0) {
                String fileExtName = file.getOldFileName().substring(file.getOldFileName().lastIndexOf(".") + 1);
                Long fileSize = file.getFileSize();
                StringBuilder builder = new StringBuilder();
                if (fileSize < 1000000) {
                    float fileSizeFloat = (float) fileSize / 1000;
                    builder.append(String.format("%.2f", fileSizeFloat)).append("KB");
                } else {
                    float fileSizeFloat = (float) fileSize / 1000000;
                    builder.append(String.format("%.2f", fileSizeFloat)).append("MB");
                }

                fileVo.setFileType(fileExtName);
                fileVo.setFileSize(builder.toString());
                fileVo.setRemark(file.getRemark());
                fileVo.setDownloadCount(file.getDownloadCount());
            }
            if (file.getOldFileName().indexOf(".") > 0) {
                fileVo.setFileName(file.getOldFileName().substring(0, file.getOldFileName().lastIndexOf(".")));
            } else {
                fileVo.setFileName(file.getOldFileName());
            }
            fileVo.setUploadTime(simpleDateFormat.format(file.getUploadTime()));
            fileVo.setFolder(file.getFolder());
            fileVo.setId(file.getId());
            fileVo.setParentId(file.getParentId());
            fileVos.add(fileVo);
        }
        return fileVos;
    }
}
