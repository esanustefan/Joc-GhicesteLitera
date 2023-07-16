package org.example;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@ComponentScan({"org.example.databases", "org.example"})
@SpringBootApplication
public class StartRestServices {
    private static SessionFactory sessionFactory;

    static void initialize() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            var metadataSources = new MetadataSources( registry );
            var builtMetadata = metadataSources.buildMetadata();
            sessionFactory = builtMetadata.buildSessionFactory();
        }
        catch (Exception e) {
            //e.getCause();
            System.err.println("Exceptie "+e);
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }

    static void close() {
        if ( sessionFactory != null ) {
            sessionFactory.close();
        }
    }

    @Primary
    @Bean(name="properties")
    public Properties getBdProperties(){
        Properties props = new Properties();
        try {
            System.out.println("Searching bd.config in directory "+((new File(".")).getAbsolutePath()));
            props.load(new FileReader("bd.config"));
        } catch (IOException e) {
            System.err.println("Configuration file bd.cong not found" + e);

        }
        return props;
    }

    @Bean(name="sessionFactory")
    public SessionFactory getSessionFactory(){
        return sessionFactory;
    }

    public static void main(String[] args) {
        initialize();
        SpringApplication.run(StartRestServices.class, args);

    }

}
