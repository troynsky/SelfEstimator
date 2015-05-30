package warehouse;

import config.IConfigLoader;
import core.*;

import java.util.List;

public interface ILoadData {

    void init(IConfigLoader loaderConfig) throws Exception;

    void addTag(Tag tag) throws Exception;

    boolean deleteTagSoft(Tag tag) throws Exception;

    void deleteTagHard(Tag tag) throws Exception;

    List<Tag> getTags() throws Exception;

    void addTerm(Term term) throws Exception;

    boolean deleteTermSoft(Term term) throws Exception;

    void deleteTermHard(Term term) throws Exception;

    void deleteTermFromAllTermTags(Term term) throws Exception;

    List<Term> getTerms() throws Exception;

    void addTagToTerm(Term term, Tag tag) throws Exception;

    void deleteTagFromTerm(Term term, Tag tag) throws Exception;

    void deleteTagFromAllTerms(Tag tag) throws Exception;

    void setUserSkill(User user, Term term, Skill skill) throws Exception;

    UserSkills getUserSkills(User user) throws Exception;

    void saveAll();

    List<Term> getDependenceTermAndTag() throws Exception;
}
