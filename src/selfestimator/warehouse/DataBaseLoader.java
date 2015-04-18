package selfestimator.warehouse;

import selfestimator.config.IConfigLoader;
import selfestimator.corelogic.logic.Skill;
import selfestimator.corelogic.logic.Tag;
import selfestimator.corelogic.logic.Term;
import selfestimator.corelogic.logic.UserSkills;

import java.sql.*;
import java.util.Map;
import java.util.TreeMap;

public class DataBaseLoader implements ILoadData {

    private IConfigLoader loaderConfig;
    private DataBaseQueryWrapper queryWrapper;

    public DataBaseLoader(IConfigLoader loaderConfig) throws Exception {
        queryWrapper = new DataBaseQueryWrapper(loaderConfig.getDBConnectionString());
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw e;
        }
        this.loaderConfig = loaderConfig;
    }

    @Override
    public void init(IConfigLoader loaderConfig) throws Exception {
        getOrInsertUserIdByName(loaderConfig.getUserName());
    }

    @Override
    public Map<String, Tag> getTags() throws Exception {
        Map<String, Tag> resultMap = new TreeMap<>();
        queryWrapper.openConnection();
        ResultSet tags = queryWrapper.getResultSet("SELECT * FROM public.\"Tags\";");
        while (tags.next()) {
            Tag tag = new Tag(tags.getString("name"));
            resultMap.put(tag.getName(), tag);
        }
        queryWrapper.closeConnection();

        return resultMap;
    }

    @Override
    public Map<String, Term> getTerms() throws Exception {
        Map<String, Term> resultMap = new TreeMap<>();
        queryWrapper.openConnection();
        ResultSet terms = queryWrapper.getResultSet("SELECT \n"
                + "\"Terms\".\"Name\" as \"TermName\", \n"
                + "\"Tags\".\"Name\" as \"TagName\"\n"
                + "  FROM \"Terms\" \n"
                + "  INNER JOIN \"TermTags\" ON \"Terms\".\"Id\" = \"TermTags\".\"TermId\"\n"
                + "  INNER JOIN \"Tags\" ON \"Tags\".\"Id\" = \"TermTags\".\"TagId\";");
        while (terms.next()) {
            String newTermName = terms.getString("TermName");
            Term term;
            boolean exists = false;
            for (String key : resultMap.keySet()) {
                if (key.equals(newTermName)) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                term = resultMap.get(newTermName);
            } else {
                term = new Term(newTermName);
                resultMap.put(newTermName, term);
            }
            Tag tag = new Tag(terms.getString("TagName"));
            term.addTag(tag);
        }
        queryWrapper.closeConnection();
        return resultMap;
    }

    @Override
    public UserSkills getUserSkills(String userName) throws Exception {
        UserSkills userSkills = new UserSkills(userName);
        queryWrapper.openConnection();
        ResultSet rs = queryWrapper.getResultSet("SELECT \"Terms\".\"Name\", \"SkillValue\" FROM \"UserSkills\"\n"
                + "INNER JOIN \"Terms\" ON \"Terms\".\"Id\" = \"TermId\"\n"
                + "INNER JOIN \"Users\" ON \"Users\".\"Id\" = \"UserId\"\n"
                + "WHERE \"Users\".\"Name\" = '" + userName + "';");
        while (rs.next()) {
            userSkills.setSkill(rs.getString("Name"), Skill.getSkillByNumber(rs.getString("SkillValue")));
        }
        return userSkills;
    }

    @Override
    public void saveAll() throws Exception {
        /*NOP*/
    }

    @Override
    public void addTag(Tag tag) throws Exception {
        queryWrapper.openConnection();
        String tagName = tag.getName();
        queryWrapper.executeNonQuery("INSERT INTO public.\"Tags\" (\"Name\") VALUES ('" + tagName + "');");
        queryWrapper.closeConnection();
    }

    @Override
    public void addTerm(Term term) throws Exception {
        queryWrapper.openConnection();
        String termName = term.getName();
        queryWrapper.executeNonQuery("INSERT INTO public.\"Terms\" (\"Name\") VALUES ('" + termName + "');");
        queryWrapper.closeConnection();
    }

    @Override
    public void setUserSkill(String userName, Term term, int skill) throws Exception {
        int userId = getOrInsertUserIdByName(userName);
        queryWrapper.openConnection();
        String termName = term.getName();
        ResultSet resultSet = queryWrapper.getResultSet("SELECT \"Id\" FROM public.\"Terms\" WHERE \"Name\" = '" + termName + "';");
        int termId = -1;
        if (resultSet.next()) {
            termId = resultSet.getInt("id");
        }
        queryWrapper.executeNonQuery("DELETE FROM public.\"UserSkills\" "
                + "WHERE \"UserId\"=" + userId + " "
                + "AND \"TermId\" = " + termId + ";");
        queryWrapper.executeNonQuery("INSERT INTO public.\"UserSkills\" (\"UserId\", \"TermId\", \"SkillValue\") "
                + "VALUES (" + userId + ", " + termId + ", " + skill + ")");


    }


    @Override
    public boolean deleteTagSoft(Tag tag) throws Exception {
        queryWrapper.openConnection();
        String tagName = tag.getName();
        boolean result = queryWrapper.executeNonQueryNoException("DELETE FROM public.\"Tags\" WHERE \"Name\" = '" + tagName + "');");
        queryWrapper.closeConnection();
        return result;
    }

    @Override
    public void deleteTagHard(Tag tag) throws Exception {
        deleteTagFromAllTerms(tag);
        deleteTagSoft(tag);
    }

    @Override
    public boolean deleteTermSoft(Term term) throws Exception {
        queryWrapper.openConnection();
        String termName = term.getName();
        boolean result = queryWrapper.executeNonQueryNoException("DELETE FROM public.\"Terms\" WHERE (\"Name\" = '" + termName + "');");
        queryWrapper.closeConnection();
        return result;
    }

    @Override
    public void deleteTermHard(Term term) throws Exception {
        deleteTermFromAllTermTags(term);
        deleteTermSoft(term);
    }

    @Override
    public void addTagToTerm(Term term, Tag tag) throws Exception {
        int idTerm = getTermId(term);
        int idTag = getTagId(tag);
        queryWrapper.openConnection();
        queryWrapper.executeNonQuery("INSERT INTO \"TermTags\" (\"TermId\",\"TagId\") VALUES ('" + idTerm + "','" + idTag + "')");
        queryWrapper.closeConnection();
    }

    @Override
    public void deleteTagFromAllTerms(Tag tag) throws Exception {
        int id = getTagId(tag);
        queryWrapper.openConnection();
        queryWrapper.executeNonQuery("DELETE FROM \"TermTags\" WHERE \"TagId\"= '" + id + "' ");
        queryWrapper.closeConnection();
    }

    @Override
    public void deleteTermFromAllTermTags(Term term) throws Exception {
        int id = getTermId(term);
        queryWrapper.openConnection();
        queryWrapper.executeNonQuery("DELETE FROM \"TermTags\" WHERE \"TermId\"= '" + id + "' ");
        queryWrapper.closeConnection();
    }

    @Override
    public void deleteTagFromTerm(Term term, Tag tag) throws Exception {
        int idTerm = getTermId(term);
        int idTag = getTagId(tag);
        queryWrapper.openConnection();
        queryWrapper.executeNonQuery("DELETE FROM \"TermTags\" WHERE (\"TermId\" = '" + idTerm + "' AND \"TagId\" = '" + idTag + "') ");
        queryWrapper.closeConnection();

    }

    private class DataBaseQueryWrapper {

        String url;
        Connection c = null;
        Statement st = null;

        public DataBaseQueryWrapper(String url) {
            this.url = url;
        }

        public void openConnection() throws Exception {
            if (c != null || st != null) {
                throw new ConnectionNotClosedException();
            }
            c = DriverManager.getConnection(url);
            st = c.createStatement();
        }

        public void closeConnection() throws Exception {
            st.close();
            c.close();
            st = null;
            c = null;
        }

        public ResultSet getResultSet(String query) throws Exception {
            return st.executeQuery(query);
        }

        public void executeNonQuery(String query) throws Exception {
            st.execute(query);
        }

        public boolean executeNonQueryNoException(String query) throws Exception {
            st.execute(query);
            return true;
        }

        private class ConnectionNotClosedException extends Exception {
        }
    }

    private int getOrInsertUserIdByName(String userName) throws Exception {
        queryWrapper.openConnection();
        ResultSet resultSet = queryWrapper.getResultSet("SELECT \"Id\" FROM \"Users\" WHERE \"Name\" = '" + userName + "'");
        int id = 0;
        try {
            if (resultSet.next()) {
                id = resultSet.getInt("Id");
            } else {
                queryWrapper.executeNonQuery("INSERT INTO \"Users\" (\"Name\") VALUES ('" + userName + "');");
                resultSet = queryWrapper.getResultSet("SELECT MAX(\"Id\") FROM \"Users\";");
                if (resultSet.next()) {
                    id = resultSet.getInt(1);
                } else {
                    throw new SQLException("Щайтанама слущилься -  MAX(Id) ни хрена не вернул, вай-уляй!");
                }
            }
        } finally {
            queryWrapper.closeConnection();
            return id;
        }
    }

    private int getTermId(Term term) throws Exception {
        queryWrapper.openConnection();
        String termName = term.getName();
        ResultSet id = queryWrapper.getResultSet("SELECT \"Id\" FROM \"Terms\" WHERE \"Name\"='" + termName + "'");
        int num = 0;
        try {
            if (id.next()) {
                num = id.getInt("Id");
            }
        } finally {
            queryWrapper.closeConnection();
            return num;
        }
    }

    private int getTagId(Tag tag) throws Exception {
        queryWrapper.openConnection();
        String tagName = tag.getName();
        ResultSet id = queryWrapper.getResultSet("SELECT \"Id\" FROM \"Terms\" WHERE \"Name\"='" + tagName + "'");
        int num = 0;
        try {
            if (id.next()) {
                num = id.getInt("Id");
            }
        } finally {
            queryWrapper.closeConnection();
            return num;
        }
    }
}
