/**
 * author:      Xiang Li
 * function:    start UDP client
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;


public class UdpClient {

    static DatagramPacket clientPacket, serverPacket;
    static DatagramSocket socket;

    public static void main(String[] args) {

        Integer port = null;
        final String usage = "Usage: -p port";
        int c;

        while((c = GetOpt.getopt(args, "p:")) != GetOpt.END){
            switch(c) {
                case 'p':
                    port = Integer.parseInt(GetOpt.optarg);
                    break;
                default:
                    System.err.println("Error: " + GetOpt.optopt);
                    System.out.println(usage);
                    return;
            }
        }

        try {

            System.out.println("This is UDP Client. Enter 'peers?' for query, 'q' for quit");


            // read from keyboard input
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in, "US-ASCII"));
            String input;

            // define server address, data
            InetAddress address = InetAddress.getByName("localhost");
            byte[] serverData = new byte[1024];
            serverPacket = new DatagramPacket(serverData, serverData.length);
            socket = new DatagramSocket();

            while((input = inputReader.readLine())!= null ){

                // transfer string to byte[]
                byte[] clientData = input.getBytes();
                clientPacket = new DatagramPacket(clientData, clientData.length, address, port);

                //  disconnect to server
                if (input.equalsIgnoreCase("q")){
                    socket.send(clientPacket);
                    System.out.println("Ended send");
                    break;
                }

                // send to server
                socket.send(clientPacket);
                // receive from server
                socket.receive(serverPacket);
                System.out.println(new String(serverPacket.getData(), 0, serverPacket.getLength()));

            }

            inputReader.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}