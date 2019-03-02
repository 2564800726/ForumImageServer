package database;

import java.util.ArrayList;
import java.util.List;

public class DatabaseConnectionPool {
    private static final List<DatabaseManager> connections = new ArrayList<>();
    private static volatile int currentConnections = 0;

    public synchronized static DatabaseManager getConnection() {
        while (true) {
            if (currentConnections > 0) {
                DatabaseManager connection = connections.remove(connections.size() - 1);
                connections.remove(connection);
                currentConnections--;
                System.out.println("===================== " + currentConnections + " ============GET==========");
                return connection;
            }
        }
    }

    public static synchronized boolean close(DatabaseManager connection) {
        currentConnections++;
        System.out.println("===================== " + currentConnections + " ============PUT==========");
        return connections.add(connection);
    }

    static {
        for (int i = 0; i < 6; i++) {
            try {
                connections.add(new DatabaseManager());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentConnections = connections.size();
    }

    public static void shutDown() throws Exception {
        for (DatabaseManager connection : connections) {
            connection.disConnect();
        }
    }
}
