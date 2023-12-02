package me.phastixtv.wirtschaftssystem.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class MySQLTabel {

    private final MySQLConnection connection;
    private final String name;
    private final HashMap<String, MySQLDataType> colums;

    public MySQLTabel(MySQLConnection connection, String name, HashMap<String, MySQLDataType> colums) {
        this.connection = connection;
        this.name = name;
        this.colums = colums;
        createTable();
    }

    public void createTable() {
        String sql = "CREATE TABEL IF NOT EXISTS " + name + "(";
        for (String colum : colums.keySet()) {
            sql += colum + " " + colums.get(colum) + ",";
        }
        sql = sql.substring(0, sql.length() -1);
        sql += ");";
        try {
            Statement statement = this.connection.getConnection().createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> getColumNames() {
        return colums.keySet();
    }

    public MySQLDataType getType(String columName) {
        return colums.get(columName);
    }

    public static class Condition {
        String value;
        String columName;

        public Condition(String columName, String value) {
            this.columName = columName;
            this.value = value;
        }
    }

    public void set(String columName, Objects objects, Condition condition) {
        if (objects == null) {
            remove(condition);
            return;
        }

        if (exists(condition)) {
            try {
                String sql = "update " + this.name + " set " + columName + " =? where " + condition.columName + "=?";

                PreparedStatement preparedStatement = connection.getConnection().prepareStatement(sql);
                preparedStatement.setObject(1, objects);
                preparedStatement.setString(2, condition.value);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                String sql = "insert into " + this.name + " (" + columName + ") values (?)";
                PreparedStatement preparedStatement = connection.getConnection().prepareStatement(sql);
                preparedStatement.setObject(1, objects);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void remove(Condition condition) {
        try {
            String sql = "delete from " + this.name + " where " + condition.columName + "=?";
            PreparedStatement preparedStatement = connection.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, condition.value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getString(String columName, Condition condition) {
        try {
            String sql = "select " + columName + " from " + this.name + " where " + condition.columName + "=?";
            PreparedStatement preparedStatement = connection.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, condition.value);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(columName);
            }
            return  null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getInt(String columName, Condition condition) {
        try {
            String sql = "select " + columName + " from " + this.name + " where " + condition.columName + "=?";
            PreparedStatement preparedStatement = connection.getConnection().prepareStatement(sql);;
            preparedStatement.setString(1, condition.value);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(columName);
            }
            return  0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getBoolean(String columName, Condition condition) {
        try {
            String sql = "select " + columName + " from " + this.name + " where " + condition.columName + "=?";
            PreparedStatement preparedStatement = connection.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, condition.value);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(columName);
            }
            return  false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(Condition condition) {
        try {
            String sql = "select " + condition.columName + " from " + this.name + " where " + condition.columName + "=?";
            PreparedStatement preparedStatement = connection.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, condition.value);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
