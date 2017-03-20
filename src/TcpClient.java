/**
 * author:      Xiang Li
 * function:    start TCP client
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpClient {

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

        if (port == null) {
            System.err.println("Port was not provided");
            System.out.println(usage);
            return;
        }

        try {
            // bind host and port
            Socket socket = new Socket("localhost", port);


            // read from keyboard input
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in, "US-ASCII"));

            // writer to socket
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // read from socket
            BufferedReader sReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"US-ASCII"));

            System.out.println("This is TCP Client");

            // read from server, print
            System.out.println(sReader.readLine());

            String input;

            while((input = inputReader.readLine())!= null ){

                // send to server
                out.println(input);

                //  disconnect to server
                if (input.equalsIgnoreCase("q")){
                    System.out.println(sReader.readLine());
                    break;
                }

                // read from server, print
                System.out.println(sReader.readLine());

            }

            inputReader.close();
            out.close();
            sReader.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
