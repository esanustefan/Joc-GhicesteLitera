package org.example.interfaces;

import org.example.Jucator;
import org.example.Repository;

import java.sql.SQLException;

public interface JucatorRepository extends Repository<Long, Jucator> {
    public Jucator getAccount(String username) throws SQLException;
}
