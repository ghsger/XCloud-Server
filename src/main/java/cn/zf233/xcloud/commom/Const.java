package cn.zf233.xcloud.commom;

/**
 * Created by zf233 on 2020/12/25
 */
public class Const {

    public static final String CURRENT_USER = "currentUser";
    public static final String CURRENT_ADMIN_USER = "currentAdminUser";
    public static final String DOMAIN_NAME = "zf233";
    public static final String PARENTID = "parentid";
    public static final String ABSOLUTEPATH = "absolutePath";
    public static final String OSS_PATH_PREFIX = "https://zf233.oss-cn-beijing.aliyuncs.com/";
    public static final String SHARE_QR_REAL_PATH = "/www/server/static/img/share_qr";

    // 可排序字段枚举
    public enum SortFieldENUM {

        SORT_FILED_ENUM("old_file_name", 0),
        FILE_TYPE("file_type", 1),
        FILE_SIZE("file_size", 2),
        UPLOAD_TIME("upload_time", 3);

        private Integer index;
        private String Field;

        public static SortFieldENUM fieldOf(int index) {
            for (SortFieldENUM sortFieldENUM : values()) {
                if (sortFieldENUM.getIndex() == index) {
                    return sortFieldENUM;
                }
            }
            throw new RuntimeException("么有找到对应的枚举");
        }

        SortFieldENUM(String field, Integer index) {
            this.index = index;
            Field = field;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getField() {
            return Field;
        }

        public void setField(String field) {
            Field = field;
        }
    }

    // 邮件验证枚举
    public enum CheckEmailENUM {

        TIME_OUT("timeOut", 0), // 超时
        NOT_CHECK("notCheck", 1), // 未验证
        CHECK_FAIL("checkFail", 2), // 验证失败
        CHECK_SUCCESS("checkSuccess", 3), // 验证成功
        USER_NOT_EXISTS("userNotExists", 4), // 用户不存在（信息已经移除）
        ALREADY_CHECK("alreadyCheck", 5), // 重复验证
        UUID_EXISTS("uuidExists", 6); // UUID存在，需要冷却

        private final String desc;
        private final Integer code;

        public static CheckEmailENUM exists(Integer code) {
            for (CheckEmailENUM checkEmailENUM : values()) {
                if (checkEmailENUM.getCode() == code) {
                    return checkEmailENUM;
                }
            }
            return null;
        }

        CheckEmailENUM(String desc, Integer code) {
            this.desc = desc;
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public Integer getCode() {
            return code;
        }
    }

    // 页面名枚举
    public enum PageNameENUM {
        PAGE_HOME("home", 0),
        PAGE_INDEX("index", 1),
        PAGE_LOGIN("login", 2),
        PAGE_REGIST("regist", 3),
        PAGE_USER_DETAIL("user_detail", 4),
        PAGE_NOTICE("notice", 5);

        private final String name;
        private final Integer index;

        public static Boolean exists(String name) {
            for (PageNameENUM pageNameENUM : values()) {
                if (pageNameENUM.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }

        PageNameENUM(String name, Integer index) {
            this.index = index;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Integer getIndex() {
            return index;
        }
    }

    public interface SessionAttributeCode {
        String FILE_VOS = "fileVos"; // 文件展示对象
        String ERROR_MSG = "errorMsg"; // 错误信息
        String ERROR_BACK = "errorBack"; // 错误跳转页面
        String NOTICE_MSG = "noticeMsg"; // 通知信息
        String NOTICE_BACK = "noticeBack"; // 通知跳转页面
        String NOTICE_TITLE = "noticeTitle"; // 通知标题
    }

}
