package core;

import java.util.Map;
import java.util.TreeMap;

public class UserSkills {
    private Long id;
    private User user;
    private Map<Term, Skill> termSkills;

    protected UserSkills() {
    }

    public UserSkills(User user) {
        this.user = user;
        termSkills = new TreeMap<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<Term, Skill> getTermSkills() {
        return termSkills;
    }

    public void setTermSkills(Map<Term, Skill> termSkills) {
        this.termSkills = termSkills;
    }

    public void setSkill(Term term, Skill skill) {
        termSkills.put(term, skill);
    }

    public Iterable<Map.Entry<Term, Skill>> getSkills() {
        return termSkills.entrySet();
    }

}
