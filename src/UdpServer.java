/**
 * author:      Xiang Li
 * function:    start UDP server thread
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpServer implements Runnable{

    int port;
    Database db;

    public UdpServer(int port, Database db) throws IOException {
        this.port = port;
        this.db = db;
    }

    public void run() {

        byte[] clientData;

        System.out.println("*** Started UDP Server, waiting for Client ***");

        try {
            // create
            DatagramPacket clientPacket;
            DatagramSocket socket = new DatagramSocket(port);

            clientData = new byte[1024];
            clientPacket = new DatagramPacket(clientData, clientData.length);

            // start a UDP thread
            new UdpServerThread(socket, clientPacket, port, db);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
