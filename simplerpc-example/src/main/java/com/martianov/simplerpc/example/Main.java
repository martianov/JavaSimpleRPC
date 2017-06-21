package com.martianov.simplerpc.example;

/**
 * Starts example application.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class Main {
    public static void main(String[] args) {
        if (args.length != 7 || (args.length % 2 != 1)) {
            System.out.println("Not enough arguments.\n" +
                    "Usage: host port clientsCount workersCount callsCount serviceName methodName [serviceName methodName]\n" +
                    "Please see README.md for more details.\n");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        int clientsCount = Integer.parseInt(args[2]);
        int workersCount = Integer.parseInt(args[3]);
        int callsCount = Integer.parseInt(args[4]);

        int serviceNamesCount = (args.length - 5) / 2;
        String[] serviceNames = new String[serviceNamesCount];
        String[] methodNames = new String[serviceNamesCount];

        for (int i = 0; i < serviceNamesCount; i++) {
            serviceNames[i] = args[5 + i];
            methodNames[i] = args[6 + i];
        }

        ExampleApplication app = new ExampleApplication(host, port, clientsCount, workersCount, callsCount, serviceNames, methodNames);

        try {
            int rc = app.start();
            System.exit(rc);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
