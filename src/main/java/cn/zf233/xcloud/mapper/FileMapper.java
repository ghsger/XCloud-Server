package cn.zf233.xcloud.mapper;

import cn.zf233.xcloud.entity.File;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileMapper {

    File selectByPrimaryKey(Integer id);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(File file);

    File selectRootNodeOfUserByPrimaryKey(Integer userid);

    List<File> selectFilesByUserIDAndParentID(@Param(value = "userid") Integer userID, @Param(value = "parentid") Integer parentID);

    List<File> selectFiles();

    Integer updateByPrimaryKeySelective(File file);

    List<File> selectFilesByUserIDAndMatchCode(@Param(value = "userid") Integer id, @Param(value = "matchcode") String matchCode);

    List<File> selectFilesByUserIDAndParentIDSortByTypeAsce(@Param(value = "userid") Integer id, @Param(value = "parentid") Integer parentId, @Param(value = "filed") String field);

    List<File> selectFilesByUserIDAndParentIDSortByTypeDesc(@Param(value = "userid") Integer id, @Param(value = "parentid") Integer parentId, @Param(value = "filed") String field);
}