package com.nedap.go.game;

import com.nedap.go.server.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {

    private Server server;

    @BeforeEach
    void setUp() throws UnknownHostException {
        server = new Server(0, InetAddress.getLocalHost());
    }

    @Test
    void testStartAndStopServer() {
        // after creating the server, it is still closed:
        assertFalse(server.isOpenForConnection());

        // start the server; now, it should be open for connections:
        server.start();
        assertTrue(server.isOpenForConnection());
        // also, the port number should be between 0 and 65535:
        assertTrue(server.getPort() > 0);
        assertTrue(server.getPort() <= 65535);

        // after closing the server, it should not be open for connections anymore:
        server.stop();
        assertFalse(server.isOpenForConnection());
    }
}

