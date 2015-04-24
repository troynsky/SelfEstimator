package corelogic.logic;

import java.util.HashMap;
import java.util.Map;

public class UserSkills {

    private String userName;
    private Map<String, Skill> termSkills;

    public UserSkills() {
    }

    public UserSkills(String userName) {
        this.userName = userName;
        termSkills = new HashMap<>();
    }

    public void setSkill(String termName, Skill skill) {
        termSkills.put(termName, skill);
    }

    public Iterable<Map.Entry<String, Skill>> getSkills() {
        return termSkills.entrySet();
    }

    public String getUserName() {
        return userName;
    }
}
