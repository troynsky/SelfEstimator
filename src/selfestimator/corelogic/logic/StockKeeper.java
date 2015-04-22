package selfestimator.corelogic.logic;

import selfestimator.config.IConfigLoader;
import selfestimator.warehouse.DataBaseLoader;
import selfestimator.warehouse.FileLoader;
import selfestimator.warehouse.ILoadData;
import selfestimator.warehouse.MockupLoader;

import java.util.ArrayList;
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
        getLoader().setUserSkill(loaderConfig.getUserName(), new Term(termName), skill.getValue());
    }

    public List<Term> getTerms() throws Exception {
        return getLoader().getTerms();
    }

    public List<Tag> getTags() throws Exception {
        return getLoader().getTags();
    }

    public UserSkills getUserSkills() throws Exception {
        return getLoader().getUserSkills(loaderConfig.getUserName());
    }

    public ILoadData getLoader() throws Exception {
        if (loader != null)
            return loader;
        switch (loaderConfig.getStorageType()) {
            case FileSystem:
                return new FileLoader(loaderConfig);
            case DataBase:
                return new DataBaseLoader(loaderConfig);
            case MockupLoader:
                return new MockupLoader();
            default:
                return null;
        }
    }

    public void saveAll() throws Exception{
        getLoader().saveAll();
    }

    public List<Term> getDependenceTermsAndTags() throws Exception {
        return loader.getDependenceTermAndTag();
    }
}
