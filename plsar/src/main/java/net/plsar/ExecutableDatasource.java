package net.plsar;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

public class ExecutableDatasource implements DataSource{

    Logger Log = Logger.getLogger("ExecutableDatasource");

    int c;

    Properties props;
    Queue<Connection> queue;

    public ExecutableDatasource(Builder config){
        this.c = config.c;
        this.props = config.props;
        this.queue = new LinkedBlockingDeque<>();
        this.create();
    }

    public void create() {
        try {
            Executable executable = null;
            for(int qzo = 0; qzo < c; qzo++){
                executable = new Executable(this);
                executable.run();
            }
            executable.join();
            setupShutdown();
        }catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void setupShutdown(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Log.info("ExecutableDatasource clean!");
                for(Connection connection : queue){
                    connection.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }));
    }

    protected void addConnection() throws InterruptedException {
        Connection connection = createConnection();
        if(connection != null) {
            queue.add(connection);
        }
    }

    public Connection getConnection() {
        if(queue.peek() != null) {
            try {
                Executable executable = new Executable(this);
                executable.run();
                executable.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return queue.poll();
        }
        return null;
    }

    protected Connection createConnection(){
        Connection connection;
        try{
            connection = DriverManager.getConnection(
                    props.getProperty("url"),
                    props);
            connection.setAutoCommit(false);
        } catch (SQLException ex) {
            throw new RuntimeException("Problem connecting to the database", ex);
        }
        return connection;
    }

    public static class Executable extends Thread {

        public ExecutableDatasource executableDatasource;

        public Executable(ExecutableDatasource executableDatasource){
            this.executableDatasource = executableDatasource;
        }

        @Override
        public void run() {
            try {
                executableDatasource.addConnection();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Builder {
        int c = 1;
        String ur;
        String u;
        String p;
        String d;

        Properties props;

        public Builder url(String ur){
            this.ur = ur;
            return this;
        }
        public Builder connections(int c){
            this.c = c;
            return this;
        }
        public Builder user(String u){
            this.u = u;
            return this;
        }
        public Builder password(String p){
            this.p = p;
            return this;
        }
        public Builder driver(String d){
            this.d = d;
            return this;
        }
        public ExecutableDatasource create(){
            try {
                Class.forName(d);

                props = new Properties();
                props.setProperty("user", u);
                props.setProperty("password", p);
                props.setProperty("url", ur);


            }catch (Exception ex){
                ex.printStackTrace();
            }

            return new ExecutableDatasource(this);
        }
    }

    public static class PapiException extends SQLException {
        public PapiException(String message){
            super(message);
        }
    }

    public Connection getConnection(String username, String password) throws PapiException {
        throw new PapiException("this is a simple implementation, use get connection() no parameters");
    }


    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new PapiException("no log writer here... just executableDatasource");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new PapiException("no log writer here... just executableDatasource");
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException { }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new PapiException("no login timeout... just executableDatasource");
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("parent logger, what? just executableDatasource");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws PapiException {
        throw new PapiException("no wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws PapiException {
        throw new PapiException("no wrapper.");
    }

}