package com.cookos;

import java.io.IOException;
import java.net.*;

import org.apache.logging.log4j.*;

public class Server {
    
    private static final Logger logger = LogManager.getLogger(Server.class);

    public static void main(String[] args) throws IOException {
        try (var serverSocket = new ServerSocket(8080)) {

            while (true) {
                var socket = serverSocket.accept();

                logger.info("%s:%d connected".formatted(socket.getInetAddress(), socket.getPort()));

                var thread = new Thread(new ServerTask(socket));
                thread.start();
            }           

        }
    }
}
