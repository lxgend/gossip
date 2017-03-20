/**
 * author:      Xiang Li
 * function:    communicate with the client
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.List;

public class UdpServerThread implements Runnable{

    int port;
    DatagramSocket socket = null;
    DatagramPacket serverPacket, clientPacket;
    Database db;


    public UdpServerThread(DatagramSocket socket, DatagramPacket clientPacket, int port, Database db) {
        this.socket = socket;
        this.clientPacket = clientPacket;
        this.port = port;
        this.db = db;

        new Thread(this).start();

        System.out.println("Started a new UDP server thread!");
    }


    @Override
    public void run() {


        try {
            db.createDatabase();

            byte[] serverData;

            String recv;
            StringBuffer send = new StringBuffer();

            while(true){

                //receive from client
                socket.receive(clientPacket);

                recv = new String(clientPacket.getData(), 0, clientPacket.getLength(),"US-ASCII");
                System.out.println("Received: " + recv);

                //  disconnect to client
                if (recv.equalsIgnoreCase("q")){
                    System.out.println("End thread");
                    break;

                } else if (recv.equalsIgnoreCase("PEERS?")) {

                    // query
                    List<TableValue> existedRecords = db.returnRecords("peers","peername", "port", "ip");

                    if(existedRecords.isEmpty() || existedRecords.size() == 0) {
                        serverData = "NO records".getBytes();
                        serverPacket = new DatagramPacket(serverData, serverData.length, clientPacket.getAddress(), clientPacket.getPort());
                        socket.send(serverPacket);
                    } else {

                        send.append(existedRecords.get(0).getTableName().toUpperCase());
                        send.append("|");
                        send.append(existedRecords.get(0).getRowCount());
                        send.append("|");

                        for(int i = 0; i < existedRecords.size(); i ++){
                            send.append(existedRecords.get(i).getData1());
                            send.append("|");
                            send.append(existedRecords.get(i).getData2());
                            send.append("|");
                            send.append(existedRecords.get(i).getData3());
                            send.append("|");

                            if(i == existedRecords.size()-1){
                                send.append("%");
                            }
                        }

                        System.out.println("Below is the existed List: \n"+send);

                        serverData = String.valueOf(send).getBytes();
                        serverPacket = new DatagramPacket(serverData, serverData.length, clientPacket.getAddress(), clientPacket.getPort());
                        socket.send(serverPacket);
                    }

                } else if(recv.endsWith("%") && recv.contains(":")){

                    // store the message from client to an array
                    recv = recv.substring(0, recv.length() - 1);
                    String[] splitInput = recv.split(":");

                    if(Arrays.asList(splitInput).contains(null) || splitInput.length != 4 ) {
                        serverData = "Invalid input".getBytes();
                        serverPacket = new DatagramPacket(serverData, serverData.length, clientPacket.getAddress(), clientPacket.getPort());
                        socket.send(serverPacket);

                    } else if(splitInput[0].equalsIgnoreCase("GOSSIP") ||
                            splitInput[0].equalsIgnoreCase("PEER")){

                        // store the message to database
                        db.insertData(splitInput[0], splitInput[1], splitInput[2], splitInput[3]);

                        serverData = "Store now".getBytes();
                        serverPacket = new DatagramPacket(serverData, serverData.length, clientPacket.getAddress(), clientPacket.getPort());
                        socket.send(serverPacket);

                    } else {
                        serverData = "Invalid input".getBytes();
                        serverPacket = new DatagramPacket(serverData, serverData.length, clientPacket.getAddress(), clientPacket.getPort());
                        socket.send(serverPacket);
                    }
                } else {
                    serverData = "Invalid input".getBytes();
                    serverPacket = new DatagramPacket(serverData, serverData.length, clientPacket.getAddress(), clientPacket.getPort());
                    socket.send(serverPacket);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket != null){
                socket.close();
            }
        }
    }
}
