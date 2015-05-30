package userinterface;

import config.IConfig;
import core.Skill;
import core.StockKeeper;
import core.Tag;
import core.Term;
import warehouse.DataBaseLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class ConsoleUI implements IRunApplication {

    private BufferedReader br;
    private StockKeeper keeper;

    @Override
    public void run(IConfig config) throws Exception {
        br = new BufferedReader(new InputStreamReader(System.in));
        String userName = getUserString("Представьтесь, пожалуйста.");
        config.setUserName(userName.toLowerCase());
        keeper = new StockKeeper(config);

        while (true) {
            String userChooseWhatToDo = getUserString("\nУкажите что хотите сделать:\r\n"
                    + "1) Вывести на экран все термины \r\n"
                    + "2) Вывести на экран все теги \r\n"
                    + "3) Вывести на экран степень владения термином \r\n"
                    + "4) Вывести зависимости терминов и тегов \r\n"
                    + "5) Добавить термин \r\n"
                    + "6) Добавить тэг \r\n"
                    + "7) Добавить связь между термином и тэгом \r\n"
                    + "8) Установить степень владения термином \r\n"
                    + "9) Выход\r\n");
            switch (userChooseWhatToDo) {
                case "1":
                    printAllTerms();
                    break;
                case "2":
                    printAllTags();
                    break;
                case "3":
                    printUserSkills();
                    break;
                case "4":
                    printDependenceTermsAndTags();
                    break;
                case "5":
                    addTermFromUser();
                    break;
                case "6":
                    addTagFromUser();
                    break;
                case "7":
                    addLinkTermTag();
                    break;
                case "8":
                    setSkillFromUser();
                    break;
                case "9":
                    saveAll();
                    DataBaseLoader.HibernateUtil.shutdown();
                    System.exit(0);
                default:
                    System.out.println("Вы ввели недопустимое число!");
            }
        }
    }

    private void printDependenceTermsAndTags() throws Exception {
        for (Term term : keeper.getDependenceTermsAndTags()) {
            StringBuilder sb = new StringBuilder();
            for (String tagName : term.getAllTagNames()) {
                sb.append(tagName + ", ");
            }
            if (sb.length() > 3)
                System.out.println(term.getName() + " : " + sb.toString().substring(0, sb.toString().length() - 2));
        }
    }

    private void printAllTags() throws Exception {
        for (Tag tag : keeper.getTags()) {
            System.out.print(tag.getName() + ", ");
        }
    }

    private void saveAll() throws Exception {
        keeper.saveAll();
    }

    private void printAllTerms() throws Exception {
        for (Term term : keeper.getTerms()) {
            System.out.print(term.getName() + ", ");
        }
    }

    private void printUserSkills() throws Exception {
        for (Map.Entry<Term, Skill> pair : keeper.getUserSkills().getSkills()) {
            Term key = pair.getKey();
            Skill value = pair.getValue();
            System.out.println(key.getName() + " : " + value.getValue());
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
        printTerms(keeper.getTerms());
        String userTerm = getUserString("Выберите термин !");
        for (Term term : keeper.getTerms()) {
            if (userTerm.toLowerCase().equals(term.getName())) {
                String skill = getUserString("Укажите степень владения термином");
                keeper.setSkill(userTerm, new Skill(skill));
            }
        }
    }

    private void addTermFromUser() throws Exception {
        String termFromUser = getUserString("Укажите добавляемый термин ?");
        keeper.addTerm(termFromUser.toLowerCase());
    }

    private void addTagFromUser() throws Exception {
        String tagFromUser = getUserString("Укажите добавляемый тэг ?");
        keeper.addTag(tagFromUser.toLowerCase());
    }

    private void addLinkTermTag() throws Exception {
        printTerms(keeper.getTerms());
        String UserChooseOfTerm = getUserString("Выбирите термин из списка");
        printTags(keeper.getTags());
        String UserChooseOfTag = getUserString("Выбирите тэг из списка");
        try {
            keeper.addTagToTerm(UserChooseOfTag.toLowerCase(), UserChooseOfTerm.toLowerCase());
        } catch (Exception e) {
            System.out.println("Связь уже существует");
            System.out.println(e);
            return;
        }
        System.out.println("Связь добавлена между " + UserChooseOfTag + " и " + UserChooseOfTerm);

    }

    private void printTerms(List<Term> list) {
        for (Term term : list) {
            System.out.print(term.getName() + ", ");
        }
        System.out.println();
    }

    private void printTags(List<Tag> listTags) {
        for (Tag tag : listTags) {
            System.out.print(tag.getName() + ", ");
        }
        System.out.println();
    }
}
