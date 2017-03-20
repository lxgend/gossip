/**
 * author:      Xiang Li
 * function:    communicate with the client
 */

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class TcpServerThread implements Runnable{

    Socket socket = null;
    Database db;


    public TcpServerThread(Socket socket, Database db) {
        this.socket = socket;
        this.db = db;

        new Thread(this).start();
        System.out.println("Started a new TCP server thread!");
    }

    @Override
    public void run() {

        try {
            db.createDatabase();

            // read from socket
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "US-ASCII"));

            // writer to socket
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Connection succeeded! Enter 'peers?' for query, 'q' for quit");

            String recv;
            StringBuffer send = new StringBuffer();

            while((recv = reader.readLine())!= null){  // listen to client

                System.out.println("Received: " + recv);

                //  disconnect to client
                if(recv.equalsIgnoreCase("q")){
                    out.println("Disconnected");
                    socket.close();
                    break;

                } else if (recv.equalsIgnoreCase("PEERS?")) {

                    // query
                    List<TableValue> existedRecords = db.returnRecords("peers","peername", "port", "ip");

                    if(existedRecords.isEmpty() || existedRecords.size() == 0) {
                        out.println("NO records");
                    } else {

                        send.append(existedRecords.get(0).getTableName().toUpperCase());
                        send.append("|");
                        send.append(existedRecords.get(0).getRowCount());
                        send.append("|");

                        for (int i = 0; i < existedRecords.size(); i++) {
                            send.append(existedRecords.get(i).getData1());
                            send.append("|");
                            send.append(existedRecords.get(i).getData2());
                            send.append("|");
                            send.append(existedRecords.get(i).getData3());
                            send.append("|");

                            if (i == existedRecords.size() - 1) {
                                send.append("%");
                            }
                        }

                        System.out.println("Below is the existed List: \n" + send);
                        out.println(send);
                    }

                } else if(recv.endsWith("%") && recv.contains(":")){

                    // store the message from client to an array
                    recv = recv.substring(0, recv.length() - 1);
                    String[] splitInput = recv.split(":");

                    if(Arrays.asList(splitInput).contains(null) || splitInput.length != 4 ) {
                        out.println("Invalid input");

                    } else if(splitInput[0].equalsIgnoreCase("GOSSIP") ||
                            splitInput[0].equalsIgnoreCase("PEER")){

                        // store the message to database
                        db.insertData(splitInput[0], splitInput[1], splitInput[2], splitInput[3]);

                        out.println("Store now");

                    } else {
                        out.println("Invalid input");
                    }
                } else {
                    out.println("Invalid input");
                }
            }

            System.out.println("End thread");

        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            try {
                if(socket != null){
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
