package warehouse;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by troy on 25.04.15.
 */
public class DataBaseLoaderTest {

    Connection c = null;
    java.sql.Statement st = null;

    @Before
    public void setUp() throws Exception {
        c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres?user=postgres&password=alex");
        st = c.createStatement();
    }

    @After
    public void tearDown() throws Exception {
        st.close();
        c.close();
        st = null;
        c = null;

    }

    @Test
    public void openConnection_getConnection_returnNotNullConnection() {
        Assert.assertNotNull(c);
    }
}
