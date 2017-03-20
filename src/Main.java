/**
 * Modified from:   Projects2015/P2/biro_maxim/maxim_biro_andrew_binns/src/Main.java
 * function:        start server
 */

import java.io.IOException;

public class Main {

    public static void main(final String[] args) throws IOException, InterruptedException {


        String protocol = null;
        Integer port = null;
        String databaseFilePath = null;

        // command format
        final String usage = "Usage: -t protocol -p port -d database-file-path";

        int c;

        while((c = GetOpt.getopt(args, "t:p:d:")) != GetOpt.END){
            switch(c){
                case 't':
                    protocol = GetOpt.optarg;
                    break;
                case 'p':
                    port = Integer.parseInt(GetOpt.optarg);
                    break;
                case 'd':
                    databaseFilePath = GetOpt.optarg;
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

        if (port < 1 || port > 65535) {
            System.err.println("Please provide a valid port");
            return;
        }

        if (databaseFilePath == null) {
            System.out.println("Database path was not provided");
            System.out.println(usage);
            return;
        }


        Database db = new Database(databaseFilePath);

        if (protocol == null ) {
            System.err.println("Protocol was not provided");
            System.out.println(usage);
            return;
        } else if (protocol.equalsIgnoreCase("T")){

            // start a TCP server
            final Thread tcpThread = new Thread(new TcpServer(port, db));
            tcpThread.start();
            tcpThread.join();

        } else if (protocol.equalsIgnoreCase("U")){

            // // start a UDP server
            final Thread udpThread = new Thread(new UdpServer(port, db));
            udpThread.start();
            udpThread.join();
        } else {
            System.err.println("Protocol was not provided");
            System.out.println(usage);
            return;
        }

    }
}
