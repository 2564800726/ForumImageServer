package froumimageserver;

import database.DatabaseConnectionPool;
import database.DatabaseManager;

import java.io.*;
import java.net.Socket;

public class ImageServer extends Thread {
    private InputStream inputStream = null;
    private Socket socket = null;
    private FileOutputStream fileOutputStream = null;
    private DatabaseManager connection = null;

    public ImageServer(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = socket.getInputStream();
    }

    @Override
    public void run() {
        try {
            // 读取头部
            byte[] accountByte = new byte[11];
            inputStream.read(accountByte);
            byte[] passwordByte = new byte[18];
            inputStream.read(passwordByte);
            byte[] keyByte = new byte[3];
            inputStream.read(keyByte);
            byte[] md5Byte = new byte[32];
            inputStream.read(md5Byte);

            String account = new String(accountByte);
            String password = new String(passwordByte);
            String key = new String(keyByte);
            String md5 = new String(md5Byte);

            connection = DatabaseConnectionPool.getConnection();
            String correctPassword = connection.query("user", "password", "account=" + account);
            byte[] bytes = new byte[18];
            System.arraycopy(correctPassword.getBytes(), 0, bytes, 0, correctPassword.length());
            correctPassword = new String(bytes);
            System.out.println(correctPassword.equals(password));
            if (password.equals(correctPassword)) {
                downloadImage(md5);
                if ("BAC".equals(key)) {
                    String oldImage = connection.query("user", "background", "account=" + account);
                    if (oldImage != null && !"".equals(oldImage)) {
                        oldImage = oldImage.split("129.204.3.245")[1];
                    }
                    File file = new File("/var/www/html" + oldImage);
                    if (file.exists()) {
                        file.delete();
                    }
                    connection.update("user", "background", "http://129.204.3.245/" + md5 + ".png", "account=" + account);
                } else if ("HEA".equals(key)) {
                    connection.update("user", "head", "http://129.204.3.245/" + md5 + ".png", "account=" + account);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            DatabaseConnectionPool.close(connection);
        }
    }

    private void downloadImage(String md5) throws IOException {
        fileOutputStream = new FileOutputStream(new File("/var/www/html/" + md5 + ".png"));
        byte[] buffer = new byte[1024];
        int length = -1;
        while ((length = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, length);
        }
        fileOutputStream.flush();
    }
}
