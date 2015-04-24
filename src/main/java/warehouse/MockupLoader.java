package warehouse;

import config.IConfigLoader;
import corelogic.logic.Tag;
import corelogic.logic.Term;
import corelogic.logic.UserSkills;

import java.util.List;

public class MockupLoader implements ILoadData{


    @Override
    public void init(IConfigLoader loaderConfig) throws Exception {

    }

    @Override
    public void addTag(Tag tag) throws Exception {

    }

    @Override
    public boolean deleteTagSoft(Tag tag) throws Exception {
        return false;
    }

    @Override
    public void deleteTagHard(Tag tag) throws Exception {

    }

    @Override
    public List<Tag> getTags() throws Exception {
        return null;
    }

    @Override
    public void addTerm(Term term) throws Exception {

    }

    @Override
    public boolean deleteTermSoft(Term term) throws Exception {
        return false;
    }

    @Override
    public void deleteTermHard(Term term) throws Exception {

    }

    @Override
    public void deleteTermFromAllTermTags(Term term) throws Exception {

    }

    @Override
    public List<Term> getTerms() throws Exception {
        return null;
    }

    @Override
    public void addTagToTerm(Term term, Tag tag) throws Exception {

    }

    @Override
    public void deleteTagFromTerm(Term term, Tag tag) throws Exception {

    }

    @Override
    public void deleteTagFromAllTerms(Tag tag) throws Exception {

    }

    @Override
    public void setUserSkill(String userName, Term term, int skill) throws Exception {

    }

    @Override
    public UserSkills getUserSkills(String userName) throws Exception {
        return null;
    }

    @Override
    public void saveAll() throws Exception {

    }

    @Override
    public List<Term> getDependenceTermAndTag() throws Exception {
        return null;
    }
}
