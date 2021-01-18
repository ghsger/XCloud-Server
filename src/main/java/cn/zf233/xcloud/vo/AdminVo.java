package cn.zf233.xcloud.vo;

/**
 * Created by zf233 on 2021/1/17
 */
public class AdminVo {

    private Integer id;
    private String email;
    private String username;
    private String nickname;
    private Integer role;
    private Integer level;
    private Integer useCapacity;
    private Integer capacity;
    private Integer growthValue;
    private String createTime;

    public AdminVo() {
    }

    public AdminVo(Integer id, String email, String username, String nickname, Integer role, Integer level, Integer useCapacity, Integer capacity, Integer growthValue, String createTime) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
        this.level = level;
        this.useCapacity = useCapacity;
        this.capacity = capacity;
        this.growthValue = growthValue;
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getUseCapacity() {
        return useCapacity;
    }

    public void setUseCapacity(Integer useCapacity) {
        this.useCapacity = useCapacity;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getGrowthValue() {
        return growthValue;
    }

    public void setGrowthValue(Integer growthValue) {
        this.growthValue = growthValue;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
