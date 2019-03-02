package froumimageserver;

import database.DatabaseConnectionPool;
import loadconf.Loader;

public class StartForumImageUploadServer {
    private void doShutdownWork() {
        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatabaseConnectionPool.shutDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("程序结束");
            }
        }));
    }
    public static void main(String[] args) {
        new StartForumImageUploadServer().doShutdownWork();
        int port;
        try {
            new ImageUploaderServer().startImageServer(23333);
        } catch (NumberFormatException numberFormatException) {
            System.out.println();
        }
    }
}
