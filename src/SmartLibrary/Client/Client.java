package SmartLibrary.Client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        new Client().start();
    }

    public void start() {
        System.out.println("Connecting to SmartLibrary...");
        try (Socket socket = new Socket("localhost", 8080);
             Scanner serverIn = new Scanner(socket.getInputStream());
             PrintStream serverOut = new PrintStream(socket.getOutputStream());
             Scanner userInput = new Scanner(System.in))
        {
            System.out.println("Connected! loading menu...");
            Thread listener = new Thread(() -> {
                try {
                    while (serverIn.hasNextLine()) {
                        String message = serverIn.nextLine();
                        System.out.println(message);
                    }
                } catch (Exception e) {
                    System.out.println("Connection closed.");
                }
            });
            listener.start();
            while (true) {
                if (userInput.hasNextLine()) {
                    String command = userInput.nextLine();
                    serverOut.println(command);
                    if (command.equalsIgnoreCase("exit")) {
                        break;
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Could not connect to server.");
        }
    }
}
