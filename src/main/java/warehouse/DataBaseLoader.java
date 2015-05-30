package warehouse;

import config.IConfigLoader;
import core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;


public class DataBaseLoader implements ILoadData {

    private static final Logger logger = LogManager.getLogger(DataBaseLoader.class);
    private IConfigLoader loaderConfig;

    public DataBaseLoader(IConfigLoader loaderConfig) {
        this.loaderConfig = loaderConfig;
    }

    @Override
    public void init(IConfigLoader loaderConfig) throws Exception {
        saveUser(loaderConfig.getUserName());
    }

    @Override
    public List<Tag> getTags() throws Exception {
        logger.debug("start to get all tags from DB");
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            logger.debug("all tags have been gotten");
            return session.createCriteria(Tag.class).list();
        } catch (HibernateException e) {
            logger.error("exception on getting all tags from DB, {}", e);
            System.exit(1);
        } finally {
            if (session != null)
                session.close();
        }
        return null;
    }

    @Override
    public List<Term> getTerms() throws Exception {
        logger.debug("start to get all terms from DB");
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            logger.debug("all terms have been gotten");
            return session.createCriteria(Term.class).list();
        } catch (HibernateException e) {
            logger.error("exception on getting all terms from DB, {}", e);
            System.exit(1);
        } finally {
            if (session != null)
                session.close();
        }
        return null;
    }

    @Override
    public UserSkills getUserSkills(User user) throws Exception {
        logger.debug("get userSkills for user: {}", user);
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            List<UserSkills> list = session.createQuery("FROM UserSkills WHERE user_id = ?").setParameter(0, getUserId(user)).list();
            UserSkills us = new UserSkills(user);
            for (UserSkills userSkills : list) {
                us.getTermSkills().putAll(userSkills.getTermSkills());
            }
            logger.debug("all userSkills have been gotten");
            return us;
        } catch (HibernateException e) {
            logger.error("exception on getting all userSkills from DB, {}", e);
            System.exit(1);
        } finally {
            if (session != null)
                session.close();
        }
        return null;
    }

    @Override
    public void saveAll() {
        /*NOP*/
    }

    @Override
    public List<Term> getDependenceTermAndTag() throws Exception {
        logger.debug("start to get dependenceTermAndTag from DB");
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            logger.debug("dependencies have been gotten");
            return session.createCriteria(Term.class).list();
        } catch (HibernateException e) {
            logger.error("exception on getting all dependenceTermAndTag from DB, {}", e);
            System.exit(1);
        } finally {
            if (session != null)
                session.close();
        }
        return null;
    }

    @Override
    public void addTag(Tag tag) throws Exception {
        logger.debug("start to add new tag into DB");
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            List<Tag> list = session.createCriteria(Tag.class).list();
            for (Tag result : list) {
                if (result.getName().equals(tag.getName()))
                    return;
            }
            session.beginTransaction();
            session.save(tag);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("exception on adding new tag or check for existing, {}", e);
            System.exit(1);
        } finally {
            if (session != null)
                session.close();
        }
        logger.debug("new tag has been added");
    }

    @Override
    public void addTerm(Term term) throws Exception {
        logger.debug("start to add new term into DB");
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            // find solution to do it (SELECT EXISTS(SELECT 1 FROM terms WHERE name = :termName).setString("termName", "'"+term.getName+"'"));
            List<Term> list = session.createCriteria(Term.class).list();
            for (Term result : list) {
                if (result.getName().equals(term.getName()))
                    return;
            }
            session.beginTransaction();
            session.save(term);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("exception on adding new term or check for existing, {}", e);
            System.exit(1);
        } finally {
            if (session != null)
                session.close();
        }
        logger.debug("new term has been added");
    }

    @Override
    public void setUserSkill(User user, Term term, Skill skill) throws Exception {
        logger.debug("Start to set userSkills for user {}", user.getName());
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            User resultUser = (User) session.get(User.class, getUserId(user));
            UserSkills us = new UserSkills(resultUser);
            session.save(us);
            session.save(skill);
            session.getTransaction().commit();

            session.beginTransaction();
            Term resultTerm = (Term) session.get(Term.class, getTermId(term));
            Skill resultSkill = (Skill) session.get(Skill.class, getSkillId(skill));
            us.setSkill(resultTerm, resultSkill);
            session.save(us);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("exception on adding new userSkills or check for existing, {}", e);
            System.exit(1);
        } finally {
            if (session != null)
                session.close();
        }
        logger.debug("userSkills has been set");
    }

    @Override
    public void addTagToTerm(Term term, Tag tag) throws Exception {
        logger.debug("start to add tag({}) to term({})", tag.getName(), term.getName());
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Tag resultTag = (Tag) session.get(Tag.class, getTagId(tag));
            Term resultTerm = (Term) session.get(Term.class, getTermId(term));
            resultTerm.addTag(resultTag);
            session.saveOrUpdate(resultTerm);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("exception on adding tag to term, {}", e);
            System.exit(1);
        } finally {
            if (session != null)
                session.close();
        }
        logger.debug("tag has been added to term");
    }

    private void saveUser(String userName) throws Exception {
        logger.debug("start to save new user: {}", userName);
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            List<User> list = session.createCriteria(User.class).list();
            for (User user : list) {
                if (userName.equals(user.getName())) {
                    logger.debug("this user: {} already exists", userName);
                    return;
                }
            }
            session.save(new User(userName));
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("exception on adding new user or check for existing");
            System.exit(1);
        } finally {
            if (session != null)
                session.close();
        }
        logger.debug("new user has been added");
    }

    private Long getTermId(Term term) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Long res = (Long) session.createQuery("SELECT id FROM Term WHERE name= ?").setParameter(0, term.getName()).uniqueResult();
            session.getTransaction().commit();
            return res;
        } catch (HibernateException e) {
            logger.error("exception on get term id from DB, {}", e);
            System.exit(1);
        } finally {
            if (session != null)
                session.close();
        }
        return -1L;
    }

    private Long getTagId(Tag tag) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Long res = (Long) session.createQuery("SELECT id FROM Tag WHERE name= ?").setParameter(0, tag.getName()).uniqueResult();
            session.getTransaction().commit();
            return res;
        } catch (HibernateException e) {
            logger.error("exception on get skill id from DB, {}", e);
            System.exit(1);
        } finally {
            if (session != null)
                session.close();
        }
        return -1L;
    }

    private Long getSkillId(Skill skill) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Long res = (Long) session.createQuery("SELECT id FROM Skill WHERE skill_value= ?").setParameter(0, skill.getValue()).list().get(0);
            session.getTransaction().commit();
            return res;
        } catch (HibernateException e) {
            logger.error("exception on get skill id from DB, {}", e);
            System.exit(1);
        } finally {
            if (session != null)
                session.close();
        }
        return -1L;
    }

    private Long getUserId(User user) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Long res = (Long) session.createQuery("SELECT id FROM User WHERE name = ? ").setString(0, user.getName()).uniqueResult();
            session.getTransaction().commit();
            return res;
        } catch (HibernateException e) {
            logger.error("exception on get user id from DB, {}", e);
            System.exit(1);
        } finally {
            if (session != null)
                session.close();
        }
        return -1L;
    }

    @Override
    public boolean deleteTagSoft(Tag tag) throws Exception {
        logger.debug("start to delete tag({}) soft", tag.getName());
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Tag deletedTag = (Tag) session.get(Tag.class, getTagId(tag));
            session.delete(deletedTag);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("exception on removing tag soft from DB, {}", e);
            System.exit(1);
        } finally {
            if (session != null)
                session.close();
        }
        logger.debug("tag has been deleted");
        return true;
    }

    @Override
    public void deleteTagHard(Tag tag) throws Exception {

    }

    @Override
    public boolean deleteTermSoft(Term term) throws Exception {
        logger.debug("start to delete term({}) soft", term.getName());
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Term deletedTerm = (Term) session.get(Term.class, getTermId(term));
            session.delete(deletedTerm);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("exception on removing term soft from DB, {}", e);
            System.exit(1);
        } finally {
            if (session != null)
                session.close();
        }
        logger.debug("term has been deleted");
        return true;
    }

    @Override
    public void deleteTermHard(Term term) throws Exception {

    }

    @Override
    public void deleteTagFromAllTerms(Tag tag) throws Exception {

    }

    @Override
    public void deleteTermFromAllTermTags(Term term) throws Exception {

    }

    @Override
    public void deleteTagFromTerm(Term term, Tag tag) throws Exception {

    }

    public static class HibernateUtil {

        private static final SessionFactory sessionFactory = buildSessionFactory();

        private HibernateUtil() {
        }

        private static SessionFactory buildSessionFactory() {
            try {
                return new Configuration().configure().buildSessionFactory();
            } catch (Throwable ex) {
                // Make sure you log the exception, as it might be swallowed
                System.err.println("Initial SessionFactory creation failed." + ex);
                throw new ExceptionInInitializerError(ex);
            }
        }

        public static SessionFactory getSessionFactory() {
            return sessionFactory;
        }

        public static void shutdown() {
            getSessionFactory().close();
        }
    }
}
