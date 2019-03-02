package database;

import com.mysql.cj.jdbc.Driver;
import loadconf.Loader;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseManager {
    private static String JDBC_URL;
    private static String USER_NAME;
    private static String PASSWORD;
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    public DatabaseManager() throws Exception {
        init();
        connect();
    }

    /**
     * 判断数据是否已经存在
     * @param tableName  表名
     * @param column  列名
     * @param value  需要判断的数据
     * @return  标识是否存在
     */
    public boolean isExist(String tableName, String column, String value) {
        try {
            resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE " + column + "=" + value + ";");
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据约束条件判断数据是否存在
     * @param tableName  表名
     * @param column  列名
     * @param value  需要判断的数据
     * @param constraint  约束条件
     * @return  标识是否存在
     */
    public boolean isExist(String tableName, String column, String value, String constraint) {
        try {
            resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE " + column + "=" + value + " AND " + constraint + ";");
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询数据
     * @param tableName  表名
     * @param column  列名
     * @param constraint  约束条件
     * @return  查询到的数据
     */
    public String query(String tableName, String column, String constraint) {
        try {
            resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE " + constraint + ";");
            String result = null;
            while (resultSet.next()) {
                result = resultSet.getString(column);
            }
            resultSet.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 插入数据到数据库（指定字段）
     * @param tableName  表名
     * @param columns  列名
     * @param values  值
     * @return  标识插入数据的结果
     */
    public boolean insert(String tableName, String[] columns, String[] values) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName).append("(");
        for (int i = 0; i < columns.length; i++) {
            if (i == columns.length - 1) {
                sql.append(columns[i]).append(") VALUES(");
            } else {
                sql.append(columns[i]).append(", ");
            }
        }
        for (int i = 0; i < values.length; i++) {
            if (i == values.length - 1) {
                sql.append("'").append(values[i]).append("');");
            } else {
                sql.append("'").append(values[i]).append("', ");
            }
        }
        try {
            statement.execute(sql.toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 插入数据到数据库（所有字段）
     * @param tableName  表名
     * @param values  值
     * @return  标识数据插入的结果
     */
    public boolean insert(String tableName, String[] values) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName).append(" ").append("VALUES(");
        for (int i = 0; i < values.length; i++) {
            if (i == values.length - 1) {
                sql.append("'").append(values[i]).append("');");
            } else {
                sql.append("'").append(values[i]).append("', ");
            }
        }
        try {
            statement.execute(sql.toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除一条记录
     * @param tableName  表名
     * @param constraint  约束条件
     * @return  标识删除数据的结果
     */
    public boolean delete(String tableName, String constraint) {
        try {
            String sql = "DELETE FROM " + tableName + " WHERE " + constraint + ";";
            System.out.println(sql);
            statement.execute(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新数据库中的数据
     * @param tableName  表名
     * @param column  列名
     * @param newValue  更新后的数据
     * @param constraint  约束条件
     * @return  标识更新数据的结果
     */
    public boolean update(String tableName, String column, String newValue, String constraint) {
        try {
            statement.execute(
                    "UPDATE " + tableName + " SET " + column + "='" + newValue + "' WHERE " + constraint + ";");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查询满足条件的所有结果，以字符串数组的形式返回
     * @param tableName  表名
     * @param column  列名
     * @param constraint  约束条件
     * @return  查询结果
     */
    public ArrayList<String> queryAll(String tableName, String column, String constraint) {
        try {
            resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE " + constraint + ";");
            ArrayList<String> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(resultSet.getString(column));
            }
            resultSet.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询一张表里面的所有记录的指定列
     * @param tableName  表名
     * @param columns  列名
     * @return  把结果作为一个集合返回
     */
    public ArrayList<HashMap<String, String>> gets(String tableName, String[] columns) {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        try {
            resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            while (resultSet.next()) {
                HashMap<String, String> hashMap = new HashMap<>();
                for (String column : columns) {
                    hashMap.put(column, resultSet.getString(column));
                }
                result.add(hashMap);
            }
            resultSet.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询某一条记录的指定列
     * @param tableName  表名
     * @param columns  列名
     * @param constraint  约束条件
     * @return  把结果作为一个集合返回
     */
    public HashMap<String, String> gets(String tableName, String[] columns, String constraint) {
        HashMap<String, String> result = new HashMap<>();
        try {
            resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE " + constraint + ";");
            while (resultSet.next()) {
                for (String column : columns) {
                    result.put(column, resultSet.getString(column));
                }
            }
            resultSet.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getResultSet(String sql) {
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void connect() throws Exception {
        DriverManager.registerDriver(new Driver());
        Connection connection = DriverManager.getConnection(JDBC_URL, USER_NAME, PASSWORD);
        statement = connection.createStatement();
    }

    public void disConnect() throws Exception {
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    private void init() {
        JDBC_URL = Loader.getInstance().getConf("JDBC_URL");
        USER_NAME = Loader.getInstance().getConf("USER_NAME");
        PASSWORD = Loader.getInstance().getConf("PASSWORD");
    }
}
