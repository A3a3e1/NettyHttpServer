package com.home.sample.nettyhttpserver;

import java.util.Scanner;
import java.util.logging.Logger;

public final class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        new Thread(server).start();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter '0' or 'exit' for shutdown server.");

            switch (scanner.nextLine().toLowerCase()) {
                case "0":
                case "exit":
                    server.stop();
                    break;
            }
        }
    }
}
