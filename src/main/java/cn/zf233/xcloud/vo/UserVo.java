package cn.zf233.xcloud.vo;

/**
 * Created by zf233 on 2020/12/25
 */
public class UserVo {
    private Integer id;
    private String headUrl;
    private String email;
    private String username;
    private Integer role;
    private Integer useCapacity;
    private Integer level;
    private Integer growthValue;

    public UserVo() {
    }

    public UserVo(Integer id, String headUrl, String email, String username, Integer role, Integer useCapacity, Integer level, Integer growthValue) {
        this.id = id;
        this.headUrl = headUrl;
        this.email = email;
        this.username = username;
        this.role = role;
        this.useCapacity = useCapacity;
        this.level = level;
        this.growthValue = growthValue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getUseCapacity() {
        return useCapacity;
    }

    public void setUseCapacity(Integer useCapacity) {
        this.useCapacity = useCapacity;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getGrowthValue() {
        return growthValue;
    }

    public void setGrowthValue(Integer growthValue) {
        this.growthValue = growthValue;
    }
}
