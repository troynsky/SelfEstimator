package warehouse;

import core.Tag;
import core.Term;
import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by troy on 25.04.15.
 */
public class FileLoaderTest {

    private static ApplicationContext contextTest = new ClassPathXmlApplicationContext("beansTest.xml");

    static FileLoader fileLoader;

    @BeforeClass
    public static void onlyOnce() {
        fileLoader = (FileLoader) contextTest.getBean("FileLoaderTest");
    }

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testAddTerm() throws Exception {
        Term term = new Term("Test");
        fileLoader.addTerm(term);
        Assert.assertTrue(fileLoader.getCoreTerms().contains(term));
    }

    @Test @Ignore
    public void testAddTag() throws Exception {
        Tag tag = new Tag("testTag");
        fileLoader.addTag(tag);
        Assert.assertTrue(fileLoader.getCoreTags().contains(tag));
    }

    @Test
    public void testAddTagToTerm() throws Exception {
        Term term = new Term("Test2");
        Tag tag = new Tag("Test2");
        fileLoader.addTagToTerm(term, tag);
        Assert.assertTrue(fileLoader.getCoreTermTags().contains(term));
    }

}
