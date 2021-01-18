package cn.zf233.xcloud.entity;

public class User {
    private Integer id;

    private String openId;

    private String email;

    private String username;

    private String nickname;

    private String password;

    private String question;

    private String answer;

    private Integer role;

    private Integer level;

    private Integer growthValue;

    private Long createTime;

    private Long updateTime;

    public User() {
    }

    public User(Integer id, String openId, String email, String username, String nickname, String password, String question, String answer, Integer role, Integer level, Integer growthValue, Long createTime, Long updateTime) {
        this.id = id;
        this.openId = openId;
        this.email = email;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.question = question;
        this.answer = answer;
        this.role = role;
        this.level = level;
        this.growthValue = growthValue;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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

    public Integer getGrowthValue() {
        return growthValue;
    }

    public void setGrowthValue(Integer growthValue) {
        this.growthValue = growthValue;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}