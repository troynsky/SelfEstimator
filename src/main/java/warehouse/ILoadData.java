package warehouse;

import config.IConfigLoader;
import corelogic.logic.Tag;
import corelogic.logic.Term;
import corelogic.logic.UserSkills;

import java.util.List;

public interface ILoadData {
    public void init(IConfigLoader loaderConfig) throws Exception;

    public void addTag(Tag tag)throws Exception;
    public boolean deleteTagSoft(Tag tag)throws Exception;
    public void deleteTagHard(Tag tag)throws Exception;
    public List<Tag> getTags()throws Exception;
    
    public void addTerm(Term term)throws Exception;
    public boolean deleteTermSoft(Term term)throws Exception;
    public void deleteTermHard(Term term)throws Exception;
    public void deleteTermFromAllTermTags(Term term)throws Exception;
    public List<Term> getTerms()throws Exception;
    
    public void addTagToTerm(Term term, Tag tag)throws Exception;
    public void deleteTagFromTerm(Term term, Tag tag)throws Exception;
    public void deleteTagFromAllTerms(Tag tag)throws Exception;
    
    public void setUserSkill(String userName, Term term, int skill)throws Exception;
    public UserSkills getUserSkills(String userName)throws Exception;

    public void saveAll() throws Exception;
    public List<Term> getDependenceTermAndTag()throws Exception;
}
