package cse535.brainet;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

// The tutorial can be found just here on the SSaurel's Blog :
// https://www.ssaurel.com/blog/create-a-simple-http-web-server-in-java
// Each Client Connection will be managed in a dedicated Thread
public class Server implements Runnable{
    // port to listen connection
    private static final int PORT = 8080;

    // verbose mode

    // Client Connection via Socket Class
    private Socket connect;
    private static BrainNetBayes brainNetBayes;

    private static String pathToModel;

    private Server(Socket c) {
        connect = c;
    }

    public static void main(String[] args) {
        /*  The body of the request should look something like this
            ---
            S001R01.csv
            1
            done

            ---
            The trailing newline is important. the first field is the file that we're testing, the second field is the index within
            that file that we're testing
        * */

        try {
            if (args.length == 1) {
                /*
                 * Two arguments are required for the server
                 * The first argument is the path to the training data to create the model
                 * The second argument is the base directory for the data that is to be loaded by users
                 * */
                pathToModel = args[0];
            } else {
//                pathToModel = "C:\\Users\\ryane\\AndroidStudioProjects\\CSE535-BraiNetProject2\\BraiNet_Android\\app\\src\\main\\java\\cse535\\brainet\\files\\Combined_Testing_data.csv";
                pathToModel = "/Users/jk/dev/CSE535-BraiNetProject/BraiNet_Android/app/src/main/assets/bayes.model";
                System.out.println("Server must be started with 1 argument: [path_to_model]");
                System.out.println("DEBUG - Using default value");
//                System.exit(0);
            }

            System.out.println("Creating model...");
            brainNetBayes = new BrainNetBayes(pathToModel);
            System.out.println("Model created");

            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Listening for connections on port: " + PORT + "...\n");

            // we listen until user halts server execution
            while (true) {
                Server myServer = new Server(serverConnect.accept());
                // create dedicated thread to manage the client connection
                Thread thread = new Thread(myServer);
                thread.start();
            }

        } catch (Exception e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        // we manage our particular client connection
        BufferedReader in = null; PrintWriter out = null; BufferedOutputStream dataOut = null;

        try {
            // we read characters from the client via input stream on the socket
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            // we get character output stream to client (for headers)
            out = new PrintWriter(connect.getOutputStream());
            // get binary output stream to client (for requested data)
            dataOut = new BufferedOutputStream(connect.getOutputStream());

            // get first line of the request from the client
            String input = in.readLine();
            // we parse the request with a string tokenizer
            StringTokenizer parse = new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client

            if (method.equals("POST")) {
                String response = "error during processing"; // default to error in case this fails for some reason
                String line;
                String lineBeforePrevious = "";
                String previousLine = "";
                while ((line = in.readLine()) != null) {
                    if (line.equals("done")) {
                        // TODO
//                        String pathToAssets = "C:\\Users\\ryane\\AndroidStudioProjects\\CSE535-BraiNetProject2\\BraiNet_Android\\app\\src\\main\\assets\\";
                        String pathToAssets = "/Users/jk/dev/CSE535-BraiNetProject/BraiNet_Android/app/src/main/assets/";
                        response = brainNetBayes.tryEntry(pathToAssets + lineBeforePrevious + "_test.csv", Integer.parseInt(previousLine));
                        break;
                    } else {
                        lineBeforePrevious = previousLine;
                        previousLine = line;
                    }
                }


                out.println("HTTP/1.1 200 OK");
                out.println("Date: " + new Date());
                out.println("Content-length: " + response.length());
                out.println(); // blank line between headers and content, very important !
                out.flush(); // flush character output stream buffer
                // file
                dataOut.write(response.getBytes(), 0, response.length());
                dataOut.flush();
            } else {
                String response = "Must use a POST method";

                out.println("HTTP/1.1 501 Not Implemented");
                out.println("Date: " + new Date());
                out.println("Content-length: " + response.length());
                out.println(); // blank line between headers and content, very important !
                out.flush(); // flush character output stream buffer
                // file
                dataOut.write(response.getBytes(), 0, response.length());
                dataOut.flush();
            }
        } catch (IOException ioe) {
            System.err.println("Server error : " + ioe);
        } finally {
            try {
                in.close();
                out.close();
                dataOut.close();
                connect.close(); // we close socket connection
            } catch (Exception e) {
                System.err.println("Error closing stream : " + e.getMessage());
            }
        }
    }
}