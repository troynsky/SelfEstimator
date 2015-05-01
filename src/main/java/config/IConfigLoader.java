package config;

public interface IConfigLoader {
     void setUserName(String name);

    String getUserName();

    void setStorageType(StorageType storageType);

    StorageType getStorageType();

    void setDbConnectionString(String dbConnectionString);

    String getDbConnectionString();

    void setTermFileName(String termFileName);

    String getTermFileName();

    void setTagFileName(String tagFileName);

    String getTagFileName();

    void setTermTagsFileName(String termTagsFileName);

    String getTermTagsFileName();

    void setUserSkillsFileName(String userSkillsFileName);

    String getUserSkillsFileName();

}
