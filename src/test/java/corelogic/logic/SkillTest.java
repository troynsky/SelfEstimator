package corelogic.logic;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by troy on 26.04.15.
 */
public class SkillTest {

    @Test
    public void getSkillByNumber_getString_returnedInt() {
        Assert.assertEquals(2, Skill.getSkillByNumber("2").getValue());
    }
}