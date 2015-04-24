package warehouse;

import config.IConfigLoader;
import corelogic.logic.Skill;
import corelogic.logic.Tag;
import corelogic.logic.Term;
import corelogic.logic.UserSkills;

import java.io.*;
import java.util.*;

public class FileLoader implements ILoadData {

    private IConfigLoader loaderConfig;
    private List<Term> coreTerms;
    private List<Tag> coreTags;
    private List<Term> coreTermTags;
    private List<UserSkills> userSkills;
    private Date nextTimeToSave;

    public FileLoader(IConfigLoader loaderConfig) throws Exception {
        this.loaderConfig = loaderConfig;
        coreTerms = loadCoreTerms();
        coreTags = loadCoreTags();
        coreTermTags = loadCoreTermTags();
        userSkills = loadUserSkills();
    }

    private List<Term> loadCoreTerms() throws Exception {
        List<Term> result = new ArrayList<>();
        try {
            List<String> termNames = getTermNamesFromFile();
            for (String ss : termNames) {
                result.add(new Term(ss));
            }
        } catch (FileNotFoundException ex) {
            throw new Exception("unable to read terms: " + ex.getMessage());
        }
        return result;
    }

    private List<Tag> loadCoreTags() throws Exception {
        List<Tag> result = new ArrayList<>();
        try {
            List<String> tagNames = getTagNamesFromFile();
            for (String ss : tagNames) {
                result.add(new Tag(ss));
            }
        } catch (FileNotFoundException ex) {
            throw new Exception("unable to read tags: " + ex.getMessage());
        }
        return result;
    }

    private List<Term> loadCoreTermTags() throws Exception {
        List<Term> result = new ArrayList<>();
        try {
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
        } catch (FileNotFoundException e) {
            throw new Exception("unable to read termTags: " + e.getMessage());
        }
        return result;
    }

    private List<UserSkills> loadUserSkills() throws Exception {
        List<UserSkills> result = new ArrayList<>();
        try {
            List<String> list = getUserSkillsFromFile();
            for (String s : list) {
                String[] lines = s.split(":");
                UserSkills us = new UserSkills(lines[0]);
                us.setSkill(lines[1], Skill.getSkillByNumber(lines[2]));
                result.add(us);
            }
        } catch (FileNotFoundException e) {
            throw new Exception("unable to read termTags: " + e.getMessage());
        }
        return result;
    }

    private void saveCoreTerms(List<Term> terms) throws Exception {
        List<String> list = new ArrayList<>();
        for (Term term : terms) {
            list.add(term.getName());
        }
        write(loaderConfig.getTermFileName(), list);
    }

    private void saveCoreTags(List<Tag> tags) throws Exception {
        List<String> list = new ArrayList<>();
        for (Tag tag : tags) {
            list.add(tag.getName());
        }
        write(loaderConfig.getTagFileName(), list);
    }

    private void saveCoreTermTags(List<Term> termTags) throws Exception {
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
    }

    private void saveUserSkills(List<UserSkills> userSkills) {
        List<String> list = new ArrayList<>();
        for (UserSkills userSkill : userSkills) {
            for (Map.Entry<String, Skill> pair : userSkill.getSkills()) {
                String key = pair.getKey();
                Skill value = pair.getValue();
                list.add(userSkill.getUserName() + ":" + key + ":" + value.getValue());
            }
        }
        write(loaderConfig.getUserSkillsFileName(), list);
    }

    private List<String> getTagNamesFromFile() throws FileNotFoundException {
        return readAllLinesFromFile(loaderConfig.getTagFileName());
    }

    private List<String> getTermNamesFromFile() throws FileNotFoundException {
        return readAllLinesFromFile(loaderConfig.getTermFileName());
    }

    private List<String> getTermTagsFromFile() throws FileNotFoundException {
        return readAllLinesFromFile(loaderConfig.getTermTagsFileName());
    }

    private List<String> getUserSkillsFromFile() throws FileNotFoundException {
        return readAllLinesFromFile(loaderConfig.getUserSkillsFileName());
    }

    private List<String> readAllLinesFromFile(String fileName) throws FileNotFoundException {
        List<String> result = new ArrayList<>();
        File file = new File(fileName);
        makeSureFileExists(fileName);
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()))) {
                String s;
                while ((s = in.readLine()) != null) {
                    result.add(s);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private void makeSureFileExists(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }
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
            throw new RuntimeException(e);
        }
    }

    private void saveAllData() throws Exception {
        saveCoreTerms(coreTerms);
        saveCoreTags(coreTags);
        saveCoreTermTags(coreTermTags);
        saveUserSkills(userSkills);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MINUTE, 10);
        nextTimeToSave = c.getTime();
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
        coreTerms.add(term);
    }

    @Override
    public void addTag(Tag tag) throws Exception {
        coreTags.add(tag);
        if (timeToSave()) {
            saveAllData();
        }
    }

    @Override
    public void addTagToTerm(Term term, Tag tag) throws Exception {
        term.addTag(tag);
        coreTermTags.add(term);
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
    public void setUserSkill(String userName, Term term, int skill) throws Exception {
        UserSkills us = new UserSkills(userName);
        us.setSkill(term.getName(), Skill.getSkillByNumber(skill));
        userSkills.add(us);
    }

    @Override
    public UserSkills getUserSkills(String userName) throws Exception {
        UserSkills us = new UserSkills(userName);
        for (UserSkills userSkill : userSkills) {
            if (userSkill.getUserName().equals(userName))
                for (Map.Entry<String, Skill> pair : userSkill.getSkills()) {
                    String key = pair.getKey();
                    Skill value = pair.getValue();
                    us.setSkill(key, value);
                }
        }
        return us;
    }

    @Override
    public void saveAll() throws Exception {
        saveAllData();
    }

    @Override
    public List<Term> getDependenceTermAndTag() throws Exception {
        return coreTermTags;
    }

}
