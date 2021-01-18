/*
 Navicat Premium Data Transfer

 Source Server         : AL_ECS_MySql
 Source Server Type    : MySQL
 Source Server Version : 50648
 Source Host           : 182.92.233.100:3306
 Source Schema         : xcloud

 Target Server Type    : MySQL
 Target Server Version : 50648
 File Encoding         : 65001

 Date: 18/01/2021 10:49:19
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
  `random_file_name` varchar(100) DEFAULT NULL COMMENT '随机文件名',
  `old_file_name` varchar(100) DEFAULT NULL COMMENT '文件名',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小',
  `file_type` varchar(100) DEFAULT NULL COMMENT '文件类型',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `download_count` int(11) DEFAULT NULL COMMENT '下载次数',
  `upload_time` bigint(20) DEFAULT NULL COMMENT '上传时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=817 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户id(自增字段)',
  `open_id` varchar(64) DEFAULT NULL COMMENT 'QQ互联ID',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `username` varchar(10) DEFAULT NULL COMMENT '用户名',
  `nickname` varchar(50) DEFAULT NULL COMMENT '用户昵称',
  `password` varchar(32) DEFAULT NULL COMMENT '密码(MD5)',
  `question` varchar(32) DEFAULT NULL COMMENT '问题',
  `answer` varchar(32) DEFAULT NULL COMMENT '答案',
  `role` int(11) DEFAULT NULL COMMENT '角色(-1-待邮箱确认、0-注册用户、2-管理员)',
  `level` int(11) DEFAULT NULL COMMENT '等级',
  `growth_value` int(11) DEFAULT NULL COMMENT '成长值',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_version_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_version_permission`;
CREATE TABLE `t_version_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(16) DEFAULT NULL COMMENT '版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
