package config;

public enum StorageType {
    FileSystem("FileSystem"),
    DataBase("DataBase"),
    MockupLoader("MockupLoader");

    private String storageType;

    StorageType(String storageType) {
        this.storageType = storageType;
    }
}
