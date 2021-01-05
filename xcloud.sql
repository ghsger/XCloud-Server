/*

 Source Server Type    : MySQL
 Source Schema         : xcloud

 Date: 05/01/2021 15:45:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_file
-- ----------------------------
DROP TABLE IF EXISTS `t_file`;
CREATE TABLE `t_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '文件id(自增字段)',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `parent_id` int(11) DEFAULT NULL COMMENT '父节点id(-1-根节点)',
  `folder` int(11) DEFAULT NULL COMMENT '是否是文件夹(1-是,0-不是)',
  `group_name` varchar(10) DEFAULT NULL COMMENT 'fastdfs组名',
  `remote_file_path` varchar(100) DEFAULT NULL COMMENT 'fastdfs远程文件名',
  `old_file_name` varchar(100) DEFAULT NULL COMMENT '文件名',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小',
  `file_type` varchar(100) DEFAULT NULL COMMENT '文件类型',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `download_count` int(11) DEFAULT NULL COMMENT '下载次数',
  `redis_cache_name` varchar(32) DEFAULT NULL COMMENT '文件redis缓存key',
  `upload_time` bigint(20) DEFAULT NULL COMMENT '上传时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=401 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of t_file
-- ----------------------------
BEGIN;
INSERT INTO `t_file` VALUES (378, 24, -1, 1, NULL, NULL, NULL, NULL, NULL, '根节点', NULL, NULL, 1609768517712, 1609768517712);
INSERT INTO `t_file` VALUES (382, 24, 378, 1, NULL, NULL, '文档', NULL, NULL, NULL, NULL, NULL, 1609768582564, 1609768582564);
COMMIT;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户id(自增字段)',
  `username` varchar(10) DEFAULT NULL COMMENT '用户名',
  `nickname` varchar(50) DEFAULT NULL COMMENT '用户昵称',
  `password` varchar(32) DEFAULT NULL COMMENT '密码(MD5)',
  `question` varchar(32) DEFAULT NULL COMMENT '问题',
  `answer` varchar(32) DEFAULT NULL COMMENT '答案',
  `role` int(11) DEFAULT NULL COMMENT '角色',
  `level` int(11) DEFAULT NULL COMMENT '等级',
  `growth_value` int(11) DEFAULT NULL COMMENT '成长值',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of t_user
-- ----------------------------
BEGIN;
INSERT INTO `t_user` VALUES (24, 'test01', '测试01', 'e030a0f78f668e52cac090cb5a8ab042', NULL, NULL, 0, 1, 35, 1609768517688, 1609768517688);
COMMIT;

-- ----------------------------
-- Table structure for t_version_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_version_permission`;
CREATE TABLE `t_version_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(16) DEFAULT NULL COMMENT '版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of t_version_permission
-- ----------------------------
BEGIN;
INSERT INTO `t_version_permission` VALUES (1, 'xcloud_4.5.5');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
