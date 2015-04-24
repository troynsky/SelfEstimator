package config;

public class Config implements IConfig {

    private String userName;
    private StorageType storageType;
    private String dbConnectionString;
    private String termFileName;
    private String tagFileName;
    private String termTagsFileName;
    private String userSkillsFileName;

    @Override
    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    @Override
    public StorageType getStorageType() {
        return storageType;
    }

    @Override
    public String getDBConnectionString() {
        return dbConnectionString;
    }

    @Override
    public void setDBConnectionString(String dbConnectionString) {
        this.dbConnectionString = dbConnectionString;
    }

    @Override
    public String getTermFileName() {
        return termFileName;
    }

    @Override
    public void setTermFileName(String termFileName) {
        this.termFileName = termFileName;
    }

    @Override
    public String getTagFileName() {
        return tagFileName;
    }

    @Override
    public void setTagFileName(String tagFileName) {
        this.tagFileName = tagFileName;
    }

    @Override
    public String getTermTagsFileName() {
        return termTagsFileName;
    }

    @Override
    public void setTermTagsFileName(String termTagsFileName) {
        this.termTagsFileName = termTagsFileName;
    }

    @Override
    public String getUserSkillsFileName() {
        return userSkillsFileName;
    }

    @Override
    public void setUserSkillsFileName(String userSkillsFileName) {
        this.userSkillsFileName = userSkillsFileName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserName() {
        return userName;
    }
}
