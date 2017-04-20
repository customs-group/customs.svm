/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A helper class with useful static database management functions.
 * <p>
 *
 * @author edwardlol
 *         Created by edwardlol on 2017/4/18.
 */
public final class DBManager {
    //~ Static fields/initializers ---------------------------------------------

    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static final String config_file = "./custom_proj.conf";

    private static String DB_CONNECTION;
    private static String DB_USER;
    private static String DB_PASSWORD;

    //~ Constructors -----------------------------------------------------------

    /**
     * Private constructor to prevent instantiating.
     */
    private DBManager() {
    }

    //~ Methods ----------------------------------------------------------------

    public static Connection get_DB_connection() {
        Connection connection = null;
        // read config file
        try (FileReader fr = new FileReader(config_file);
             BufferedReader br = new BufferedReader(fr)) {

            String config = br.readLine();
            while (config != null) {
                String[] result = config.split(": ");
                switch (result[0]) {
                    case "DB_CONNECTION":
                        DB_CONNECTION = result[1];
                        break;
                    case "DB_USER":
                        DB_USER = result[1];
                        break;
                    case "DB_PASSWORD":
                        DB_PASSWORD = result[1];
                        break;
                    default:
                        break;
                }
                config = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("read config file failed!");
            e.printStackTrace();
        }

        // get db connection
        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("get db connection failed!");
            e.printStackTrace();
        }
        return connection;
    }

    public static void return_DB_connection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("closing db connection failed!");
                e.printStackTrace();
            }
        }
    }
}

// End DBManager.java
