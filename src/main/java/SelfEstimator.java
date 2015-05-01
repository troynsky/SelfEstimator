import config.Beans;
import config.IConfig;
import userinterface.ConsoleUI;
import userinterface.IRunApplication;

public class SelfEstimator {

    public static void main(String[] args) throws Exception {

       // IConfig config = (IConfig) Beans.getBean("configFileLoader");
        IConfig config = (IConfig) Beans.getBean("configDataBaseLoader");

        IRunApplication ui = new ConsoleUI();
        ui.run(config);

    }
}
