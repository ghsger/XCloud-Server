package cn.zf233.xcloud.commom;

/**
 * Created by zf233 on 2020/12/25
 */
public class Const {

    public static final String CURRENT_USER = "currentUser";
    public static final String INVITE_CODE = "????????";
    public static final String DOMAIN_NAME = "?????";
    public static final String PARENTID = "parentid";
    public static final String ABSOLUTEPATH = "absolutePath";

    public enum SortFieldENUM {

        SORT_FILED_ENUM("old_file_name", 0),
        FILE_TYPE("file_type", 1),
        FILE_SIZE("file_size", 2),
        UPLOAD_TIME("upload_time", 3),
        DOWNLOAD_COUNT("download_count", 4);

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

    public interface SessionAttributeCode {
        String FILE_VOS = "fileVos";
        String FILE_NULL_TYPE = "fileNullType";
        String ERROR_MSG = "errorMsg";
        String ERROR_BACK = "errorBack";
    }
}
