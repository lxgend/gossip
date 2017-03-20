/**
 * author:      Xiang Li
 * function:    listen to client and start TCP server thread
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer implements Runnable{

    int port;
    Database db;

    public TcpServer(int port, Database db){
        this.port = port;
        this.db = db;
    }

    public void run() {

        try {

            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket;
            System.out.println("*** Started TCP Server, waiting for Client ***");

            while(true){  // listening

                socket = serverSocket.accept();

                new TcpServerThread(socket, db); // create new thread
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
