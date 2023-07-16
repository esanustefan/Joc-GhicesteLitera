package org.example.databases;

import org.example.Clasament;
import org.example.interfaces.ClasamentRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.sql.SQLException;

@Component
public class ClasamentHibernateDB implements ClasamentRepository {

    private final SessionFactory sessionFactory;
    public ClasamentHibernateDB(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Clasament findOne(Long aLong) {
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Clasament clasament = session.createQuery("from Clasament where id = :id", Clasament.class)
                        .setParameter("id", aLong)
                        .setMaxResults(1)
                        .uniqueResult();
                tx.commit();
                return clasament;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Iterable<Clasament> findAll() throws SQLException {
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Iterable<Clasament> clasament = session.createQuery("from Clasament", Clasament.class)
                        .list();
                tx.commit();
                return clasament;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Clasament save(Clasament entity) throws FileNotFoundException {
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(entity);
                tx.commit();
                return entity;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Clasament delete(Long aLong) {
        return null;
    }

    @Override
    public Clasament update(Clasament entity1, Clasament entity2) {
        return null;
    }
}
