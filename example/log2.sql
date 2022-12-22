SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for http_log
-- ----------------------------
DROP TABLE IF EXISTS `http_log`;
CREATE TABLE `http_log`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `req_time` datetime(3) NULL DEFAULT NULL COMMENT '请求时间',
  `req_headers` json NULL COMMENT '请求头',
  `req_body` longblob NULL COMMENT '请求体',
  `src_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '源地址',
  `dst_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目标地址',
  `port` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '端口号',
  `req_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求路径',
  `req_parameters` json NULL COMMENT '请求参数',
  `req_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求方法',
  `res_time` datetime(3) NULL DEFAULT NULL COMMENT '响应时间',
  `res_code` int(11) NULL DEFAULT NULL COMMENT '响应码',
  `res_headers` json NULL COMMENT '响应头',
  `res_body` longblob NULL COMMENT '响应体',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for log_target
-- ----------------------------
DROP TABLE IF EXISTS `log_target`;
CREATE TABLE `log_target`  (
  `id` int(11) NOT NULL,
  `host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `port` int(11) NULL DEFAULT NULL,
  `network_interface` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `log_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
