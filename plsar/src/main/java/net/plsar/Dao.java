package net.plsar;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Dao {

    Logger Log = Logger.getLogger("Dao");

    DataSource datasource;
    PersistenceConfig config;

    public Dao(PersistenceConfig config){
        this.config = config;
        this.datasource = new ExecutableDatasource.Builder()
                .driver(config.getDriver())
                .url(config.getUrl())
                .user(config.getUser())
                .password(config.getPassword())
                .connections(130)
                .create();
    }

    public <T> T get(String preSql, Object[] sqlparams, Class<?> klass){
        Object result = null;
        String sql = "";
        try {
            sql = getPopulatedSqlStatement(preSql, sqlparams);
            Connection connection = datasource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            if(resultSet.next()){
                result = getResult(resultSet, klass);
            }
            if(result == null){
                throw new Exception(klass + " not found using '" + sql + "'");
            }

            connection.commit();
            connection.close();

        } catch (SQLException ex) {
            if(config.isDebug()) {
                Log.info("bad sql:" + sql);
            }
        } catch (Exception ex) {
            Log.info(ex.getMessage());
        }

        return (T) klass.cast(result);
    }

    public Integer getInt(String preSql, Object[] sqlparams){
        Integer result = null;
        String sql = "";
        try {
            sql = getPopulatedSqlStatement(preSql, sqlparams);
            Connection connection = datasource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            if(resultSet.next()){
                result = Integer.parseInt(resultSet.getObject(1).toString());
            }

            if(result == null){
                throw new Exception("no results using '" + sql + "'");
            }

            connection.commit();
            connection.close();

        } catch (SQLException ex) {
            if(config.isDebug()) {
                Log.info("bad sql:" + sql);
            }
        } catch (Exception ex) {
            Log.info(ex.getMessage());
        }

        return result;
    }

    public Long getLong(String preSql, Object[] sqlparams){
        Long result = null;
        String sql = "";
        try {
            sql = getPopulatedSqlStatement(preSql, sqlparams);
            Connection connection = datasource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            if(resultSet.next()){
                result = Long.parseLong(resultSet.getObject(1).toString());
            }

            if(result == null){
                throw new Exception("no results using '" + sql + "'");
            }

            connection.commit();
            connection.close();
        } catch (SQLException ex) {
            if(config.isDebug()) {
                Log.info("bad sql:" + sql);
            }
        } catch (Exception ex) {
            Log.info(ex.getMessage());
        }

        return result;
    }

    public int save(String preSql, Object[] sqlparams){
        try {
            String sql = getPopulatedSqlStatement(preSql, sqlparams);
            Connection connection = datasource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.execute();

            ResultSet rs = statement.getGeneratedKeys();
            int id = 0;
            if (rs.next()) {
                id = rs.getInt(1);
            }

            connection.commit();
            connection.close();

            return id;
        }catch(Exception ex){
            if(config.isDebug()) {
                Log.info(ex.getMessage());
            }
        }
        return 0;
    }

    public <T> List<T> getList(String preSql, Object[] sqlparams, Class<?> klass){
        List<T> results = new ArrayList<>();
        try {
            String sql = getPopulatedSqlStatement(preSql, sqlparams);
            Connection connection = datasource.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                T obj = getResult(rs, klass);
                results.add(obj);
            }
            connection.commit();
            connection.close();
        }catch(ClassCastException ccex){
            if(config.isDebug()) {
                Log.info(ccex.getMessage());
            }
        }catch (Exception ex){
            Log.info(ex.getMessage());
        }
        return results;
    }

    public boolean update(String preSql, Object[] sqlparams){
        try {
            String sql = getPopulatedSqlStatement(preSql, sqlparams);
            Connection connection = datasource.getConnection();
            Statement statement = connection.createStatement();
            statement.execute(sql);
            connection.commit();
            connection.close();
        }catch(Exception ex){
            if(config.isDebug()) {
                Log.info(ex.getMessage());
            }
            return false;
        }
        return true;
    }

    public boolean delete(String preSql, Object[] sqlparams){
        try {
            String sql = getPopulatedSqlStatement(preSql, sqlparams);
            Connection connection = datasource.getConnection();
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
            connection.commit();
            connection.close();
        }catch(Exception ex){
            if(config.isDebug()) {
                Log.info(ex.getMessage());
            }
            return false;
        }
        return true;
    }


    protected String getPopulatedSqlStatement(String sql, Object[] sqlparams){
        for(Object object : sqlparams){
            if(object != null) {
                String parameter = object.toString();
                if (object.getClass().getTypeName().equals("java.lang.String")) {
                    parameter = parameter.replace("'", "''")
                            .replace("$", "\\$")
                            .replace("#", "\\#")
                            .replace("@", "\\@");
                }
                sql = sql.replaceFirst("\\[\\+\\]", parameter);
            }else{
                sql = sql.replaceFirst("\\[\\+\\]", "null");
            }
        }
        return sql;
    }

    protected <T> T getResult(ResultSet resultSet, Class<?> klass) throws Exception{
        Object object = new Object();
        Constructor[] constructors = klass.getConstructors();
        for(Constructor constructor: constructors){
            if(constructor.getParameterCount() == 0){
                object = constructor.newInstance();
            }
        }

        Field[] fields = object.getClass().getDeclaredFields();
        for(Field field: fields){
            field.setAccessible(true);
            String originalName = field.getName();
            String regex = "([a-z])([A-Z]+)";
            String replacement = "$1_$2";
            String name = originalName.replaceAll(regex, replacement).toLowerCase();
            Type type = field.getType();
            if (hasColumn(resultSet, name)) {
                if (type.getTypeName().equals("int") || type.getTypeName().equals("java.lang.Integer")) {
                    field.set(object, resultSet.getInt(name));
                } else if (type.getTypeName().equals("double") || type.getTypeName().equals("java.lang.Double")) {
                    field.set(object, resultSet.getDouble(name));
                } else if (type.getTypeName().equals("float") || type.getTypeName().equals("java.lang.Float")) {
                    field.set(object, resultSet.getFloat(name));
                } else if (type.getTypeName().equals("long") || type.getTypeName().equals("java.lang.Long")) {
                    field.set(object, resultSet.getLong(name));
                } else if (type.getTypeName().equals("boolean") || type.getTypeName().equals("java.lang.Boolean")) {
                    field.set(object, resultSet.getBoolean(name));
                } else if (type.getTypeName().equals("java.math.BigDecimal")) {
                    field.set(object, resultSet.getBigDecimal(name));
                } else if (type.getTypeName().equals("java.lang.String")) {
                    if (resultSet.getString(name) != null &&
                            !resultSet.getString(name).equals("null")) {
                        field.set(object, resultSet.getString(name));
                    }else{
                        field.set(object, "");
                    }
                }
            }
        }
        return (T) klass.cast(object);
    }

    public static boolean hasColumn(ResultSet resultSet, String columnName) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        for (int foo = 1; foo <= rsmd.getColumnCount(); foo++) {
            if (columnName.equals(rsmd.getColumnName(foo).toLowerCase())) {
                return true;
            }
        }
        return false;
    }

}
