package net.plsar;

import org.h2.tools.RunScript;

import javax.sql.DataSource;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Logger;

public class DatabaseEnvironmentManager {
    Logger Log = Logger.getLogger(DatabaseEnvironmentManager.class.getName());

    PersistenceConfig persistenceConfig;

    public void configure(){

        try {
            Path schemaFilePath = Paths.get("src", "main", "resources", persistenceConfig.getSchemaConfig().getSchema());
            File schemaConfigFile = new File(schemaFilePath.toAbsolutePath().toString());
            if (!schemaConfigFile.exists()) {
                Log.info("non-persistence mode");
                Log.info("schema.sql missing in src/main/resources/. project will be treated as a non persistent application.");
                Log.info("Database environment setup complete");
                return;
            }

            InputStream in = new FileInputStream(schemaConfigFile);

            if (in == null || in.available() == 0) {
                Log.info("src/main/resources/{schema}.sql contains no tables. project will be treated as a non persistent application.");
                Log.info("Database environment setup complete");
                return;
            }

            StringBuilder schemaSql = new StringBuilder();
            if (in.available() > 0){
                Scanner scanner = new Scanner(in);
                do {
                    schemaSql.append(scanner.nextLine() + "\n");
                } while(scanner.hasNext());
                in.close();
            }

            DataSource datasource = new ExecutableDatasource.Builder()
                    .url(persistenceConfig.getUrl())
                    .driver(persistenceConfig.getDriver())
                    .user(persistenceConfig.getUser())
                    .password(persistenceConfig.getPassword())
                    .create();

            Connection conn = datasource.getConnection();

            RunScript.execute(conn, new StringReader("drop all objects;"));

            if (!schemaSql.toString().equals("")) {
                RunScript.execute(conn, new StringReader(schemaSql.toString()));
            }

            conn.commit();
            conn.close();

            Log.info("Database environment setup complete");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void setPersistenceConfig(PersistenceConfig persistenceConfig) {
        this.persistenceConfig = persistenceConfig;
    }
}
