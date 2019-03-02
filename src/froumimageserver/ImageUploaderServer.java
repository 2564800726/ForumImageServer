package froumimageserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ImageUploaderServer {
    public void startImageServer(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                new ImageServer(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
