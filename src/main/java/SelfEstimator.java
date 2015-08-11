import config.Beans;
import config.IConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import userinterface.ConsoleUI;
import userinterface.IRunApplication;

public class SelfEstimator {

    private static final Logger logger = LogManager.getLogger(SelfEstimator.class);

    public static void main(String[] args) throws Exception {
        logger.debug("Program start");

//        IConfig config = (IConfig) Beans.getBean("configFileLoader");
        IConfig config = (IConfig) Beans.getBean("configDataBaseLoader");

        IRunApplication ui = new ConsoleUI();
        ui.run(config);
    }
}
