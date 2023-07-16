package org.example;

import org.example.databases.ClasamentHibernateDB;
import org.example.databases.ConfiguratieHibernateDB;
import org.example.databases.JucatorDB;
import org.example.interfaces.ClasamentRepository;
import org.example.interfaces.ConfiguratieRepository;
import org.example.interfaces.JucatorRepository;
import org.example.utils.AbstractServer;
import org.example.utils.RpcConcurrentServer;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.Properties;

public class StartRpcServer {

    static SessionFactory sessionFactory;

    static void initialize() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Exception " + e);
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    static void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
    private static final int defaultPort = 55555;
    public static void main(String[] args) throws ServerException {
        initialize();

        Properties serverProps = new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch(IOException e){
            System.err.println("Cannot find server.properties " + e);
            return;
        }
        JucatorRepository jucatorDB = new JucatorDB(serverProps);
        ConfiguratieRepository configuratieDB = new ConfiguratieHibernateDB(sessionFactory);
        ClasamentRepository clasamentDB = new ClasamentHibernateDB(sessionFactory);

        ServiceInterface service = new Service(jucatorDB, configuratieDB, clasamentDB);
        int serverPort = defaultPort;
        try {
            serverPort = Integer.parseInt(serverProps.getProperty("port"));
        } catch (NumberFormatException nef) {
            System.err.println("Wrong Port Number " + nef.getMessage());
            System.err.println("Using default port " + defaultPort);
        }
        System.out.println("Starting server on port: " + serverPort);
        AbstractServer server = new RpcConcurrentServer(serverPort, service);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }
        finally {
            try{
                close();
                server.stop();
            } catch (ServerException e) {
                System.err.println("Error stopping server " + e.getMessage());
            }
        }
    }
}
