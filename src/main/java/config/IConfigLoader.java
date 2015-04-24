package config;

public interface IConfigLoader{
    public void setUserName(String name);
    public String getUserName();
    
    public void setStorageType(StorageType storageType);
    public StorageType getStorageType();
    public void setDBConnectionString(String dbConnectionString);
    public String getDBConnectionString();
    
    public void setTermFileName(String termFileName);
    public String getTermFileName();
    public void setTagFileName(String tagFileName);
    public String getTagFileName();
    public void setTermTagsFileName(String termTagsFileName);
    public String getTermTagsFileName();
    public void setUserSkillsFileName(String userSkillsFileName);
    public String getUserSkillsFileName();
    
}
