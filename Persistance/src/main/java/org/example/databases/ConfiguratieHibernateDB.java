package org.example.databases;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Configuratie;
import org.example.interfaces.ConfiguratieRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.sql.SQLException;

@Component
public class ConfiguratieHibernateDB implements ConfiguratieRepository {

    private final SessionFactory sessionFactory;

    private static final Logger logger = LogManager.getLogger();

    public ConfiguratieHibernateDB(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    @Override
    public Configuratie findOne(Long aLong) {
        return null;
    }

    @Override
    public Iterable<Configuratie> findAll() throws SQLException {
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Iterable<Configuratie> configuratii = session.createQuery("from Configuratie", Configuratie.class)
                        .list();
                tx.commit();
                return configuratii;
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
    public Configuratie save(Configuratie entity) throws FileNotFoundException {
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
        }
        return null;
    }

    @Override
    public Configuratie delete(Long aLong) {
        return null;
    }

    @Override
    public Configuratie update(Configuratie entity1, Configuratie entity2) {
        return null;
    }
}
