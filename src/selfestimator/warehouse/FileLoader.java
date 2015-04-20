package selfestimator.warehouse;

import selfestimator.config.IConfigLoader;
import selfestimator.corelogic.logic.Skill;
import selfestimator.corelogic.logic.Tag;
import selfestimator.corelogic.logic.Term;
import selfestimator.corelogic.logic.UserSkills;

import java.io.*;
import java.util.*;

public class FileLoader implements ILoadData {

    private IConfigLoader loaderConfig;
    private Map<String, Term> coreTerms;
    private Map<String, Tag> coreTags;
    private UserSkills userSkills;
    private Date nextTimeToSave;

    public FileLoader(IConfigLoader loaderConfig) {
        this.loaderConfig = loaderConfig;
        coreTerms = new HashMap<>();
        coreTags = new HashMap<>();
        userSkills = new UserSkills(loaderConfig.getUserName());
    }

    private Map<String, Tag> loadCoreTags() throws Exception {
        HashMap<String, Tag> result = new HashMap<>();
        try {
            List<String> tagNames = getTagNamesFromFile();
            for (String ss : tagNames) {
                result.put(ss, new Tag(ss));
            }
        } catch (FileNotFoundException ex) {
            throw new Exception("unable to read tags: " + ex.getMessage());
        }
        return result;
    }

    /*
     * Метод имеет множество допущений: формат файла, целостность данных
     * и так далее. Мы верим, что всё будет хорошо. ^_^
     * А когда всё неизбежно станет плохо - мы убежим к работе с БД.
     */
    private Map<String, Term> loadCoreTerms() throws Exception {
        HashMap<String, Term> terms = new HashMap<>();
        try {
            List<String> termNames = getTermNamesFromFile();
            for (String ss : termNames) {
                terms.put(ss, new Term(ss));
            }
        } catch (FileNotFoundException ex) {
            throw new Exception("unable to read terms: " + ex.getMessage());
        }

        try {
            List<String> termTags = getTermTagsFromFile();
            for (String termTag : termTags) {
                String[] splitted = termTag.split(";");
                Term term = terms.get(splitted[0]);
                term.addTag(new Tag(splitted[1]));
            }
        } catch (FileNotFoundException ex) {
            throw new Exception("unable to read term tags: " + ex.getMessage());
        } catch (NullPointerException e) {
            throw e;
        }

        return terms;
    }

    private void saveCoreTags(Map<String, Tag> tags) throws Exception {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Tag> pair : tags.entrySet()) {
            String key = pair.getKey();
            list.add(key);
        }
        write(loaderConfig.getTagFileName(), list);
    }

    private void saveCoreTerms(Map<String, Term> terms) throws Exception {
        List<String> termNames = new ArrayList<>();
        List<String> termTags = new ArrayList<>();
        for (Map.Entry<String, Term> pair : terms.entrySet()) {
            String key = pair.getKey();
            termNames.add(key);
            Term term = pair.getValue();
            for (String tagName : term.getAllTagNames()) {
                termTags.add(term.getName() + ";" + tagName);
            }
        }
        write(loaderConfig.getTermFileName(), termNames);
        write(loaderConfig.getTermTagsFileName(), termTags);

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
            try (PrintWriter out = new PrintWriter(file.getAbsoluteFile())) {
                for (String ss : list) {
                    out.println(ss);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveUserSkills(UserSkills userSkills) {
        List<String> list = new ArrayList<>();
        list.add(userSkills.getUserName());
        for (Map.Entry<String, Skill> pair : userSkills.getSkills()) {
            String key = pair.getKey();
            Skill value = pair.getValue();
            list.add(key + ";" + value.getValue());
        }
        write(loaderConfig.getUserSkillsFileName(), list);
    }

    private UserSkills writeUserSkills(String userName) throws FileNotFoundException {
        List<String> lines = readAllLinesFromFile(loaderConfig.getUserSkillsFileName());
        UserSkills userSkills = new UserSkills(lines.get(0));
        for (String ss : lines) {  //        for (String ss : lines.subList( 1, lines.size())){
            if (ss == lines.get(0)) {
                continue;
            }
            String[] splitted = ss.split(";");
            Skill skill = Skill.getSkillByNumber(splitted[1]);
            userSkills.setSkill(splitted[0], skill);
        }
        return userSkills;
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
    public void addTag(Tag tag) throws Exception {
        coreTags.put(tag.getName(), tag);
        if (timeToSave()) {
            saveAllData();
        }
    }

    @Override
    public boolean deleteTagSoft(Tag tag) throws Exception {
        if (!coreTags.containsKey(tag.getName())) {
            return false;
        }
        coreTags.remove(tag.getName());
        return true;

    }

    @Override
    public void deleteTagHard(Tag tag) throws Exception {
        coreTags.remove(tag.getName());

    }

    @Override
    public Map<String, Tag> getTags() throws Exception {
        return coreTags;

    }

    @Override
    public void addTerm(Term term) throws Exception {
        coreTerms.put(term.getName(), term);
    }

    @Override
    public boolean deleteTermSoft(Term term) throws Exception {
        if (!coreTerms.containsKey(term.getName())) {
            return false;
        } else {
            coreTerms.remove(term.getName());
        }
        return true;
    }

    @Override
    public void deleteTermHard(Term term) throws Exception {
        coreTerms.remove(term.getName());
    }

    @Override
    public void deleteTermFromAllTermTags(Term term) throws Exception {
        if (!coreTerms.containsKey(term.getName())) {
            return;
        } else {
            term.deleteAllTags();
        }
    }

    @Override
    public Map<String, Term> getTerms() throws Exception {
       // if(userSkills.getUserName().equals())
        return coreTerms;
    }

    @Override
    public void addTagToTerm(Term term, Tag tag) throws Exception {
        term.addTag(tag);
    }

    @Override
    public void deleteTagFromTerm(Term term, Tag tag) throws Exception {
        term.deleteTag(tag);
    }

    @Override
    public void deleteTagFromAllTerms(Tag tag) throws Exception {
        for (Map.Entry<String, Term> pair : coreTerms.entrySet()) {
            Term term = pair.getValue();
            term.deleteTag(tag);
        }
    }

    @Override
    public void setUserSkill(String userName, Term term, int skill) throws Exception {
        userSkills.setSkill(term.getName(), Skill.getSkillByNumber(skill));
    }

    @Override
    public UserSkills getUserSkills(String userName) throws Exception {
        return userSkills;
    }

    @Override
    public void saveAll() throws Exception {
        saveAllData();
    }

    @Override
    public List<Term> getDependenceTermAndTag() throws Exception {
        return null;
    }

    private void saveAllData() throws Exception {
        //TODO: save all data
        saveCoreTags(coreTags);
        saveCoreTerms(coreTerms);
        saveUserSkills(userSkills);
        Calendar c = Calendar.getInstance().getInstance();
        c.setTime(new Date());
        c.add(Calendar.MINUTE, 10);
        nextTimeToSave = c.getTime();
    }

    private boolean timeToSave() {
        return new Date().after(nextTimeToSave);
    }
}
