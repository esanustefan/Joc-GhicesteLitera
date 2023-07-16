package org.example.databases;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Jucator;
import org.example.interfaces.JucatorRepository;
import org.example.utils.JdbcUtils;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class JucatorDB implements JucatorRepository {
    private final JdbcUtils jdbcUtils;

    private static final Logger logger = LogManager.getLogger();

    public JucatorDB(Properties props) {
        logger.info("Initializing JucatorDB with properties: {} ", props);
        System.out.println("Initializing JucatorDB with properties: {} "+ props);
        jdbcUtils = new JdbcUtils(props);
    }

    @Override
    public Jucator findOne(Long aLong) {
        return null;
    }

    @Override
    public Iterable<Jucator> findAll() throws SQLException {
        return null;
    }

    @Override
    public Jucator save(Jucator entity) throws FileNotFoundException {
        return null;
    }

    @Override
    public Jucator delete(Long aLong) {
        return null;
    }

    @Override
    public Jucator update(Jucator entity1, Jucator entity2) {
        return null;
    }

    @Override
    public Jucator getAccount(String username) throws SQLException {
        logger.traceEntry("finding jucator with {}", username);
        Connection con = jdbcUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("select * from jucator where username = ?")) {
            preStmt.setString(1, username);
            try(ResultSet result = preStmt.executeQuery()) {
                if(result.next()) {
                    Long id = result.getLong("id");
                    Jucator jucator = new Jucator(username);
                    jucator.setId(id);
                    logger.traceExit(jucator);
                    return jucator;
                }
            } catch (SQLException e) {
                logger.error(e);
                System.err.println("Error DB " + e);
            }
        }
        catch (Exception e) {
            logger.error(e);
            System.err.println("Error DB 1: " + e);
        }
        return null;
    }
}
