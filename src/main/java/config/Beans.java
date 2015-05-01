package config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by troy on 30.04.15.
 */
public class Beans {

    private static ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

    public static Object getBean(String id) {
       return context.getBean(id);
    }
}
