package warehouse;

import config.IConfigLoader;
import core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

public class FileLoader implements ILoadData {

    private static final Logger logger = LogManager.getLogger(FileLoader.class);

    private IConfigLoader loaderConfig;
    private List<Term> coreTerms;
    private List<Tag> coreTags;
    private List<Term> coreTermTags;
    private List<UserSkills> userSkills;
    private Date nextTimeToSave;

    public FileLoader(IConfigLoader loaderConfig) {
        this.loaderConfig = loaderConfig;
        coreTerms = loadCoreTerms();
        coreTags = loadCoreTags();
        coreTermTags = loadCoreTermTags();
        userSkills = loadUserSkills();
    }

    public FileLoader(IConfigLoader loaderConfig, List<Term> coreTerms, List<Tag> coreTags, List<Term> coreTermTags, List<UserSkills> userSkills) {
        this.loaderConfig = loaderConfig;
        this.coreTerms = coreTerms;
        this.coreTags = coreTags;
        this.coreTermTags = coreTermTags;
        this.userSkills = userSkills;
    }

    public IConfigLoader getLoaderConfig() {
        return loaderConfig;
    }

    public void setLoaderConfig(IConfigLoader loaderConfig) {
        this.loaderConfig = loaderConfig;
    }

    public List<Term> getCoreTerms() {
        return coreTerms;
    }

    public void setCoreTerms(List<Term> coreTerms) {
        this.coreTerms = coreTerms;
    }

    public List<Tag> getCoreTags() {
        return coreTags;
    }

    public void setCoreTags(List<Tag> coreTags) {
        this.coreTags = coreTags;
    }

    public List<Term> getCoreTermTags() {
        return coreTermTags;
    }

    public void setCoreTermTags(List<Term> coreTermTags) {
        this.coreTermTags = coreTermTags;
    }

    public List<UserSkills> getUserSkills() {
        return userSkills;
    }

    public void setUserSkills(List<UserSkills> userSkills) {
        this.userSkills = userSkills;
    }

    public Date getNextTimeToSave() {
        return nextTimeToSave;
    }

    public void setNextTimeToSave(Date nextTimeToSave) {
        this.nextTimeToSave = nextTimeToSave;
    }

    private List<Term> loadCoreTerms() {
        logger.debug("Start loading all terms from file");
        List<Term> result = new ArrayList<>();
        List<String> termNames = getTermNamesFromFile();
        for (String ss : termNames) {
            result.add(new Term(ss));
        }
        logger.debug("Loaded all Terms");
        return result;
    }

    private List<Tag> loadCoreTags() {
        logger.debug("Start loading all tags from file");
        List<Tag> result = new ArrayList<>();
        List<String> tagNames = getTagNamesFromFile();
        for (String ss : tagNames) {
            result.add(new Tag(ss));
        }
        logger.debug("Loaded all tags");
        return result;
    }

    private List<Term> loadCoreTermTags() {
        logger.debug("Start loading all terms with tags from file");
        List<Term> result = new ArrayList<>();
        List<String> list = getTermTagsFromFile();
        for (String s : list) {
            String[] lines = s.split(":");
            Term term = new Term(lines[0]);
            String[] tagNames = lines[1].split(",");
            for (String tagName : tagNames) {
                term.addTag(new Tag(tagName.trim()));
            }
            result.add(term);
        }
        logger.debug("Loaded all terms with tags");
        return result;
    }

    private List<UserSkills> loadUserSkills() {
        logger.debug("Start loading all user's skills from file");
        List<UserSkills> result = new ArrayList<>();
        List<String> list = getUserSkillsFromFile();
        for (String s : list) {
            String[] lines = s.split(":");
            UserSkills us = new UserSkills(new User(lines[0]));
            us.setSkill(new Term(lines[1]), new Skill(Integer.parseInt(lines[2])));
            result.add(us);
        }
        logger.debug("Loaded all user's skills");
        return result;
    }

    private void saveCoreTerms(List<Term> terms) {
        logger.debug("Start saving all terms from heap");
        List<String> list = new ArrayList<>();
        for (Term term : terms) {
            list.add(term.getName());
        }
        write(loaderConfig.getTermFileName(), list);
        logger.debug("Saving terms done");
    }

    private void saveCoreTags(List<Tag> tags) {
        logger.debug("Start saving all tags from heap");
        List<String> list = new ArrayList<>();
        for (Tag tag : tags) {
            list.add(tag.getName());
        }
        write(loaderConfig.getTagFileName(), list);
        logger.debug("Saving tags done");
    }

    private void saveCoreTermTags(List<Term> termTags) {
        logger.debug("Start saving all terms with tags from heap");
        List<String> list = new ArrayList<>();
        for (Term tt : termTags) {
            StringBuilder sb = new StringBuilder();
            for (String s : tt.getAllTagNames()) {
                sb.append(s + ",");
            }
            String out = tt.getName() + ":" + sb.toString().substring(0, sb.toString().length() - 1);
            list.add(out);
        }
        write(loaderConfig.getTermTagsFileName(), list);
        logger.debug("Saving terms with tags done");
    }

    private void saveUserSkills(List<UserSkills> userSkills) {
        logger.debug("Start saving all user's skills from heap");
        List<String> list = new ArrayList<>();
        for (UserSkills userSkill : userSkills) {
            for (Map.Entry<Term, Skill> pair : userSkill.getSkills()) {
                Term key = pair.getKey();
                Skill value = pair.getValue();
                list.add(userSkill.getUser().getName() + ":" + key.getName() + ":" + value.getValue());
            }
        }
        write(loaderConfig.getUserSkillsFileName(), list);
        logger.debug("Saving user's skills done");
    }

    private List<String> getTagNamesFromFile() {
        return readAllLinesFromFile(loaderConfig.getTagFileName());
    }

    private List<String> getTermNamesFromFile() {
        return readAllLinesFromFile(loaderConfig.getTermFileName());
    }

    private List<String> getTermTagsFromFile() {
        return readAllLinesFromFile(loaderConfig.getTermTagsFileName());
    }

    private List<String> getUserSkillsFromFile() {
        return readAllLinesFromFile(loaderConfig.getUserSkillsFileName());
    }

    private List<String> readAllLinesFromFile(String fileName) {
        List<String> result = new ArrayList<>();
        File file = new File(fileName);
        if (file.exists()) {
            try {
                try (BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()))) {
                    String s;
                    while ((s = in.readLine()) != null) {
                        result.add(s);
                    }
                }
            } catch (IOException e) {
                logger.error("Can't read data from file I/O Exception", e);
                System.exit(1);
            }
        } else {
            logger.error("File not found");
            System.exit(1);
        }
        return result;
    }

    private void write(String fileName, List<String> list) {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (FileWriter out = new FileWriter(file.getAbsoluteFile())) {
                for (String ss : list) {
                    out.write(ss);
                    out.write("\n");
                }
            }
        } catch (IOException e) {
            logger.error("Can't write to file", e);
            System.exit(1);
        }
    }

    private void saveAllData() {
        logger.debug("Start to save all data");
        saveCoreTerms(coreTerms);
        saveCoreTags(coreTags);
        saveCoreTermTags(coreTermTags);
        saveUserSkills(userSkills);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MINUTE, 10);
        nextTimeToSave = c.getTime();
        logger.debug("Saving all data done");
    }

    private boolean timeToSave() {
        return new Date().after(nextTimeToSave);
    }

    @Override
    public void init(IConfigLoader loaderConfig) {
        this.loaderConfig = loaderConfig;
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MINUTE, 10);
        nextTimeToSave = c.getTime();
    }

    @Override
    public void addTerm(Term term) throws Exception {
        logger.debug("add term: {}", term.getName());
        coreTerms.add(term);
        logger.debug("term added");
    }

    @Override
    public void addTag(Tag tag) throws Exception {
        logger.debug("add tag: {}", tag.getName());
        coreTags.add(tag);
        if (timeToSave()) {
            saveAllData();
        }
        logger.debug("tag added");
    }

    @Override
    public void addTagToTerm(Term term, Tag tag) throws Exception {
        for (Term resultTerm : coreTermTags) {
            if(resultTerm.getName().equals(term.getName()))
                resultTerm.addTag(tag);
        }
//        term.addTag(tag);
//        coreTermTags.add(term);
        logger.debug("tag to term added");
    }

    @Override
    public List<Tag> getTags() throws Exception {
        return coreTags;
    }

    @Override
    public List<Term> getTerms() throws Exception {
        return coreTerms;
    }

    @Override
    public boolean deleteTermSoft(Term term) throws Exception {
        if (!coreTerms.contains(term)) {
            return false;
        } else {
            coreTerms.remove(term);
        }
        return true;
    }

    @Override
    public void deleteTermHard(Term term) throws Exception {
        coreTerms.remove(term);
    }

    @Override
    public boolean deleteTagSoft(Tag tag) throws Exception {
        if (!coreTags.contains(tag)) {
            return false;
        }
        coreTags.remove(tag);
        return true;
    }

    @Override
    public void deleteTagHard(Tag tag) throws Exception {
        coreTags.remove(tag);
    }

    @Override
    public void deleteTermFromAllTermTags(Term term) throws Exception {
        if (!coreTerms.contains(term)) {
            return;
        } else {
            term.deleteAllTags();
        }
    }

    @Override
    public void deleteTagFromTerm(Term term, Tag tag) throws Exception {
        if (!coreTerms.contains(term)) {
            return;
        } else {
            for (Term coreTerm : coreTerms) {
                if (coreTerm.equals(term))
                    term.deleteTag(tag);
            }
        }
    }

    @Override
    public void deleteTagFromAllTerms(Tag tag) throws Exception {
        for (Term coreTermTag : coreTermTags) {
            coreTermTag.deleteTag(tag);
        }
    }

    @Override
    public void setUserSkill(User user, Term term, Skill skill) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("set user's skill for user: {}, term: {}, skill: {}", user, term.getName(), skill);
        }
        UserSkills us = new UserSkills(user);
        us.setSkill(term, skill);
        userSkills.add(us);
        logger.debug("user's skill set");
    }

    @Override
    public UserSkills getUserSkills(User user) throws Exception {
        logger.debug("get user's skill for user: {}", user);
        UserSkills us = new UserSkills(user);
        for (UserSkills userSkill : userSkills) {
            if (userSkill.getUser().getName().equals(user.getName()))
                for (Map.Entry<Term, Skill> pair : userSkill.getSkills()) {
                    Term key = pair.getKey();
                    Skill value = pair.getValue();
                    us.setSkill(key, value);
                }
        }
        logger.debug("user's skill got");
        return us;
    }

    @Override
    public void saveAll() {
        logger.info("save all data");
        saveAllData();
        logger.info("all data saved");
    }

    @Override
    public List<Term> getDependenceTermAndTag() throws Exception {
        return coreTermTags;
    }

}
