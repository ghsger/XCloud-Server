package cn.zf233.xcloud.service.impl;

import cn.zf233.xcloud.commom.Const;
import cn.zf233.xcloud.commom.ResponseCodeENUM;
import cn.zf233.xcloud.commom.ServerResponse;
import cn.zf233.xcloud.entity.AbsolutePath;
import cn.zf233.xcloud.entity.File;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.mapper.FileMapper;
import cn.zf233.xcloud.service.FileService;
import cn.zf233.xcloud.service.UserService;
import cn.zf233.xcloud.util.OSSUtil;
import cn.zf233.xcloud.util.QRCodeUtil;
import cn.zf233.xcloud.vo.FileVo;
import cn.zf233.xcloud.vo.UserVo;
import com.aliyun.oss.OSS;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.FileOutputStream;
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
    private UserService userService;

    @Resource
    private OSSUtil ossUtil;

    @Override
    public ServerResponse<List<FileVo>> home(User user,
                                             Integer parentId,
                                             Integer sortFlag,
                                             Integer sortType,
                                             String matchCode) {
        List<File> files;

        if (parentId == null || parentId == -1) {
            File rootNode = fileMapper.selectRootNodeOfUserByPrimaryKey(user.getId());
            parentId = rootNode.getId();
        }

        File file = fileMapper.selectByPrimaryKey(parentId);
        if (file == null) {
            return ServerResponse.createByErrorCodeMessage(
                    ResponseCodeENUM.ILLEGAL_ARGUMENT.getCode(),
                    "文件夹不存在，请下拉以刷新");
        }

        List<AbsolutePath> absolutePath = null;
        if (StringUtils.isNotBlank(matchCode)) { // 模糊搜索

            files = fileMapper.selectFilesByUserIDAndMatchCode(user.getId(), matchCode);
        } else if (sortFlag != null) { // 排序

            if (sortType == 1) { // 升序

                files = fileMapper.selectFilesByUserIDAndParentIDSortByTypeDesc(user.getId(), parentId, Const.SortFieldENUM.fieldOf(sortFlag).getField());
            } else { // 降序
                files = fileMapper.selectFilesByUserIDAndParentIDSortByTypeAsce(user.getId(), parentId, Const.SortFieldENUM.fieldOf(sortFlag).getField());
            }
        } else { // 直接显示

            files = fileMapper.selectFilesByUserIDAndParentID(user.getId(), parentId);
            absolutePath = getAbsolutePath(user.getId(), parentId);
        }

        // 包装
        List<FileVo> fileVos = assembleFileVoDetail(files);

        return ServerResponse.createBySuccessAbsolutePathMsg("获取成功", fileVos, absolutePath);
    }

    // 保存文件信息到数据库
    @Override
    @Transactional
    public ServerResponse saveFile(User user, List<File> files, String remark, Integer parentId) {
        OSS ossClient = ossUtil.getOSSClient();

        UserVo userVoByPrimarykey = userService.getUserVoByPrimarykey(user.getId());
        if (userVoByPrimarykey.getUseCapacity() + files.size() > userVoByPrimarykey.getLevel() * 10) {
            for (File file : files) {
                ossUtil.delete(ossClient, file.getRandomFileName());
            }
            return ServerResponse.createByErrorMessage("上传失败(空间已满)");
        }

        if (parentId == null || parentId == -1) {

            File rootNode = fileMapper.selectRootNodeOfUserByPrimaryKey(user.getId());
            parentId = rootNode.getId();
        } else {

            File fileOfParentId = fileMapper.selectByPrimaryKey(parentId);
            if (fileOfParentId == null) {
                return ServerResponse.createByErrorIllegalArgument("文件夹不存在,下拉以刷新");
            }
        }

        for (File file : files) {
            file.setParentId(parentId);
            Integer resultFlag = fileMapper.insert(file);
            if (resultFlag < 0) {
                ossUtil.delete(ossClient, file.getRandomFileName());
            }
        }

        userService.updateUserGrowthValueByPrimaryKey(user.getId());
        ossClient.shutdown();

        return ServerResponse.createBySuccessMessage("上传成功");
    }

    // 创建文件夹
    @Override
    @Transactional
    public ServerResponse createFolder(User user, String folderName, Integer parentId) {
        if (StringUtils.isBlank(folderName)) {
            return ServerResponse.createByErrorMessage("创建失败");
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

    // 删除单个文件、文件夹或递归删除整个文件夹
    @Override
    @Transactional
    public ServerResponse removeFileOrFolder(Integer[] fileIds, User user) {
        OSS ossClient = ossUtil.getOSSClient();

        int count = 0;
        for (Integer fileId : fileIds) {

            File targetFile = fileMapper.selectByPrimaryKey(fileId);
            if (targetFile == null) {
                return ServerResponse.createByErrorIllegalArgument("文件或文件夹不存在");
            }

            Set<File> filesSet = new HashSet<>();
            findChildParentId(filesSet, fileId, user.getId());
            for (File file : filesSet) {

                if (file.getFolder() == 0) {

                    if (ossUtil.objectNameExists(ossClient, file.getRandomFileName())) {
                        ossUtil.delete(ossClient, file.getRandomFileName());
                    }
                }
                fileMapper.deleteByPrimaryKey(file.getId());
                count++;
            }
        }

        ossClient.shutdown();

        return ServerResponse.createBySuccessMessage("共计删除:" + count + "个文件或文件夹");
    }

    // 通过id获取文件
    @Override
    public File getFileByFileId(Integer fileId, Integer userId) {
        File file = fileMapper.selectByPrimaryKey(fileId);
        if (file.getFolder() == 0) {
            userService.updateUserGrowthValueByPrimaryKey(userId);
        }
        return file;
    }

    @Override
    public ServerResponse getFileShareQrURL(Integer fileId, User user) {
        if (fileId != null) {

            try {
                File targetFile = getFileByFileId(fileId, user.getId());

                java.io.File folder = new java.io.File(Const.SHARE_QR_REAL_PATH);
                if (!folder.exists()) {

                    if (!folder.mkdir()) {
                        return ServerResponse.createByErrorMessage("分享失败");
                    }
                }

                String shareFileName = "file_share_" + UUID.randomUUID().toString() + ".jpg";
                java.io.File file = new java.io.File(Const.SHARE_QR_REAL_PATH, shareFileName);
                FileOutputStream os = new FileOutputStream(file);
                QRCodeUtil.encode(Const.OSS_PATH_PREFIX + targetFile.getRandomFileName(), "/www/server/static/img/test.png", os, true);
                os.close();

                return ServerResponse.createBySuccess("获取分享二维码成功", "https://www.xcloud.show/static/img/share_qr/" + shareFileName);
            } catch (Exception e) {
                e.printStackTrace();

                return ServerResponse.createByErrorMessage("分享失败");
            }
        }

        return ServerResponse.createByErrorMessage("文件ID为空");
    }

    // 递归查询parentId所在绝对路径
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

    // 递归查询所在parentId所在路径
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

    // 组装文件展示对象
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
            fileVo.setDownloadURL(Const.OSS_PATH_PREFIX + file.getRandomFileName());
            fileVos.add(fileVo);
        }

        return fileVos;
    }
}
