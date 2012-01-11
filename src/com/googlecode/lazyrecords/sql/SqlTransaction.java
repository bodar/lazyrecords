package com.googlecode.lazyrecords.sql;

import com.googlecode.totallylazy.LazyException;
import com.googlecode.lazyrecords.Transaction;

import java.sql.Connection;
import java.sql.SQLException;

public class SqlTransaction implements Transaction{
    private final Connection connection;

    public SqlTransaction(Connection connection) {
        this.connection = connection;
        try {
            // Start transaction
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw LazyException.lazyException(e);
        }
    }

    public void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw LazyException.lazyException(e);
        }
    }

    public void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw LazyException.lazyException(e);
        }
    }
}

