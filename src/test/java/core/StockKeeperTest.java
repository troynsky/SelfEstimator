package core;

import config.Config;
import config.IConfigLoader;
import config.StorageType;
import org.junit.Assert;
import org.junit.Test;
import warehouse.ILoadData;
import warehouse.MockupLoader;

/**
 * Created by troy on 26.04.15.
 */
public class StockKeeperTest {
    IConfigLoader config;

    public StockKeeperTest() {
        config = new Config();
        config.setStorageType(StorageType.MockupLoader);
    }

    @Test
    public void getLoader() throws Exception {
        StockKeeper keeper = new StockKeeper(config);
        ILoadData loader = keeper.getLoader();
        Assert.assertTrue(loader instanceof MockupLoader);
        org.junit.Assert.assertNotNull(loader);
    }
}
