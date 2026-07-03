package jobplatform.persistence;

import jobplatform.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;

public class DataStore {

    private static final SessionFactory sessionFactory =
            new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();

    private DataStore() {
    }

    public static boolean load() {
        if (!hasData()) {
            clearExtents();
            return false;
        }

        try (Session session = sessionFactory.openSession()) {
            Skill.setExtent(findAll(session, Skill.class));
            Company.setExtent(findAll(session, Company.class));
            PlatformUser.setExtent(findAll(session, PlatformUser.class));
            Employer.setExtent(findAll(session, Employer.class));
            JobSeeker.setExtent(findAll(session, JobSeeker.class));
            CandidateProfile.setExtent(findAll(session, CandidateProfile.class));
            CV.setExtent(findAll(session, CV.class));
            CandidateSkill.setExtent(findAll(session, CandidateSkill.class));
            RecruitmentProcess.setExtent(findAll(session, RecruitmentProcess.class));
            RecruitmentStage.setExtent(findAll(session, RecruitmentStage.class));
            JobOffer.setExtent(findAll(session, JobOffer.class));
            Application.setExtent(findAll(session, Application.class));
            SavedOffer.setExtent(findAll(session, SavedOffer.class));
            Notification.setExtent(findAll(session, Notification.class));
            Interview.setExtent(findAll(session, Interview.class));
        }

        return true;
    }

    public static void save() {
        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            saveList(session, Skill.getExtent());
            saveList(session, Company.getExtent());
            saveList(session, PlatformUser.getExtent());
            saveList(session, Employer.getExtent());
            saveList(session, JobSeeker.getExtent());
            saveList(session, CandidateProfile.getExtent());
            saveList(session, CV.getExtent());
            saveList(session, CandidateSkill.getExtent());
            saveList(session, RecruitmentProcess.getExtent());
            saveList(session, RecruitmentStage.getExtent());
            saveList(session, JobOffer.getExtent());
            saveList(session, Application.getExtent());
            saveList(session, SavedOffer.getExtent());
            saveList(session, Notification.getExtent());
            saveList(session, Interview.getExtent());

            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
    }

    public static void save(Object object) {
        if (object == null) {
            return;
        }

        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(object);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
    }

    public static void delete(Object object) {
        if (object == null) {
            return;
        }

        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.delete(session.contains(object) ? object : session.merge(object));
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
    }

    private static void saveList(Session session, List<?> objects) {
        for (Object object : objects) {
            if (object != null) {
                session.saveOrUpdate(object);
            }
        }
    }

    private static <T> List<T> findAll(Session session, Class<T> entityClass) {
        return session.createQuery("from " + entityClass.getSimpleName(), entityClass).list();
    }

    public static <T> List<T> findAll(Class<T> entityClass) {
        try (Session session = sessionFactory.openSession()) {
            return findAll(session, entityClass);
        }
    }

    public static long count(Class<?> entityClass) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "select count(e) from " + entityClass.getSimpleName() + " e",
                    Long.class
            ).getSingleResult();
        }
    }

    public static boolean hasData() {
        return count(PlatformUser.class) > 0;
    }

    private static void clearExtents() {
        Skill.setExtent(new ArrayList<>());
        Company.setExtent(new ArrayList<>());
        PlatformUser.setExtent(new ArrayList<>());
        Employer.setExtent(new ArrayList<>());
        JobSeeker.setExtent(new ArrayList<>());
        CandidateProfile.setExtent(new ArrayList<>());
        CV.setExtent(new ArrayList<>());
        CandidateSkill.setExtent(new ArrayList<>());
        RecruitmentProcess.setExtent(new ArrayList<>());
        RecruitmentStage.setExtent(new ArrayList<>());
        JobOffer.setExtent(new ArrayList<>());
        Application.setExtent(new ArrayList<>());
        SavedOffer.setExtent(new ArrayList<>());
        Notification.setExtent(new ArrayList<>());
        Interview.setExtent(new ArrayList<>());
    }

    public static void shutdown() {
        if (!sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}