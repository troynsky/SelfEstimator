package selfestimator;

import selfestimator.config.Config;
import selfestimator.config.IConfig;
import selfestimator.config.StorageType;
import selfestimator.userinterface.ConsoleUI;
import selfestimator.userinterface.IRunApplication;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class SelfEstimator {

    public static void main(String[] args) throws Exception {

        IConfig config = new Config();

        Properties properties = new Properties();
        properties.load(new FileReader(new File("/home/troy/IdeaProjects/SelfEstimator/configuration.properties")));

        if (properties.getProperty("storageType").equals("FileSystem")) {
            config.setStorageType(StorageType.FileSystem);
            config.setTagFileName(properties.getProperty("tags"));
            config.setTermFileName(properties.getProperty("terms"));
            config.setTermTagsFileName(properties.getProperty("termTags"));
            config.setUserSkillsFileName(properties.getProperty("termSkills"));
        } else if (properties.getProperty("storageType").equals("DataBase")) {
            config.setStorageType(StorageType.DataBase);
            config.setDBConnectionString(properties.getProperty("postgresql"));
        }

        IRunApplication ui = new ConsoleUI();
        ui.run(config);

    }
}
