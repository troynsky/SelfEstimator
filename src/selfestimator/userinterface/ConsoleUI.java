package selfestimator.userinterface;

import selfestimator.config.IConfig;
import selfestimator.corelogic.logic.Skill;
import selfestimator.corelogic.logic.StockKeeper;
import selfestimator.corelogic.logic.Tag;
import selfestimator.corelogic.logic.Term;
import selfestimator.warehouse.FileLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class ConsoleUI implements IRunApplication {

    private BufferedReader br;
    private StockKeeper keeper;
    private boolean haveToExit = false;

    @Override
    public void run(IConfig config) throws Exception {
        br = new BufferedReader(new InputStreamReader(System.in));
        String userName = getUserString("Представтесь, пожалуйста.");
        config.setUserName(userName);
        keeper = new StockKeeper(config);

        while (true) {
            String UserChoiseWhatToDo = getUserString("\nУкажите что хотите сделать:\r\n"
                    + "1) Вывести на экран все термины и теги \r\n"
                    + "2) Вывести на экран степень владения термином \r\n"
                    + "3) Добавить термин \r\n"
                    + "4) Добавить тэг \r\n"
                    + "5) Добавить связь между термином и тэгом \r\n"
                    + "6) Установить степень владения термином \r\n"
                    + "7) Выход\r\n");
            switch (UserChoiseWhatToDo) {
                case "1":
                    printTermsAndTags();
                    break;
                case "2":
                    printUserSkills();
                    break;
                case "3":
                    addTermFromUser();
                    break;
                case "4":
                    addTagFromUser();
                    break;
                case "5":
                    addLinkTermTag();
                    break;
                case "6":
                    setSkillFromUser();
                    break;
                case "7":
                    saveAll();
                    System.exit(0);
                default:
                    System.out.println("Вы ввели недопустимое число!");
            }
        }
    }

    private void saveAll() throws Exception {
        keeper.saveAll();
    }

    private void printTermsAndTags() throws Exception {
        for (Term term : keeper.getTerms()) {
            System.out.print(term.getName() + " : ");
            for (String tagName : term.getAllTagNames()) {
                System.out.print(tagName + ", ");
            }
            System.out.println();
        }
        printTags(keeper.getTags());
    }

    private void printUserSkills() throws Exception {
        for (Map.Entry<String, Skill> pair : keeper.getUserSkills().getSkills()) {
            String key = pair.getKey();
            Skill value = pair.getValue();
            System.out.println(key + ";" + value.getValue());
        }
    }

    private String getUserString(String question) throws Exception {
        askUser(question);
        String answer = br.readLine();
        return answer;
    }

    private void askUser(String question) {
        System.out.println(question);
    }

    private int getUserInt(String question) throws Exception {
        return Integer.parseInt(getUserString(question));
    }

    private void setSkillFromUser() throws Exception {
        List<Term> list = keeper.getTerms();
        printTerms(list);
        String userTerm = getUserString("Выберите термин !");
        for (Term term : keeper.getTerms()) {
            if (userTerm.equals(term.getName())) {
                String skill = getUserString("Укажите степень владения термином");
                keeper.setSkill(userTerm, Skill.getSkillByNumber(skill));
            }
        }
    }

    private void addTermFromUser() throws Exception {
        String termFromUser = getUserString("Укажите добавляемый термин ?");
        keeper.addTerm(termFromUser);

    }

    private void addTagFromUser() throws Exception {
        String tagFromUser = getUserString("Укажите добавляемый тэг ?");
        keeper.addTag(tagFromUser);
    }

    private void addLinkTermTag() throws Exception {
        printTerms(keeper.getTerms());
        String UserChoiseOfTerm = getUserString("Выбирите термин из списка");
        printTags(keeper.getTags());
        String UserChoiseOfTag = getUserString("Выбирите тэг из списка");
        keeper.addTagToTerm(UserChoiseOfTag, UserChoiseOfTerm);
        System.out.println("Связь добавлена между " + UserChoiseOfTag + " и " + UserChoiseOfTerm);

    }

    private void printTerms(List<Term> list) {
        for (Term term : list) {
            System.out.println(term.getName());
        }
    }

    private void printTags(List<Tag> listTags) {
        for (Tag tag : listTags) {
            System.out.print(tag.getName() + ", ");
        }
        System.out.println();
    }
}
