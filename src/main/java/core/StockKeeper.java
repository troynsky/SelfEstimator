package core;

import config.Beans;
import config.IConfigLoader;
import warehouse.DataBaseLoader;
import warehouse.FileLoader;
import warehouse.ILoadData;
import warehouse.MockupLoader;

import java.util.Collections;
import java.util.List;

public class StockKeeper {

    IConfigLoader loaderConfig;
    ILoadData loader;

    public StockKeeper(IConfigLoader loaderConfig) throws Exception {
        this.loaderConfig = loaderConfig;
        loader = getLoader();
        loader.init(loaderConfig);
    }

    public void addTerm(String name) throws Exception {
        getLoader().addTerm(new Term(name));
    }

    public void addTag(String name) throws Exception {
        getLoader().addTag(new Tag(name));
    }

    public void addTagToTerm(String tagName, String termName) throws Exception {
        getLoader().addTagToTerm(new Term(termName), new Tag(tagName));
    }

    public void setSkill(String termName, Skill skill) throws Exception {
        getLoader().setUserSkill(new User(loaderConfig.getUserName()), new Term(termName), skill);
    }

    public List<Term> getTerms() throws Exception {
        List<Term> list = getLoader().getTerms();
        Collections.sort(list);
        return list;
    }

    public List<Tag> getTags() throws Exception {
        List<Tag> list = getLoader().getTags();
        Collections.sort(list);
        return list;
    }

    public UserSkills getUserSkills() throws Exception {
        return getLoader().getUserSkills(new User(loaderConfig.getUserName()));
    }

    public ILoadData getLoader() {
        if (loader != null)
            return loader;
        switch (loaderConfig.getStorageType()) {
            case FileSystem:
                return (FileLoader) Beans.getBean("FileLoader");
            case DataBase:
                return (DataBaseLoader) Beans.getBean("DataBaseLoader");
            case MockupLoader:
                return new MockupLoader();
            default:
                return null;
        }
    }

    public void saveAll() throws Exception {
        getLoader().saveAll();
    }

    public List<Term> getDependenceTermsAndTags() throws Exception {
        return loader.getDependenceTermAndTag();
    }
}
