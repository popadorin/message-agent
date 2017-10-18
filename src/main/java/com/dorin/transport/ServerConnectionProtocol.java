package com.dorin.transport;

import java.io.IOException;

public interface ServerConnectionProtocol {
    void send(String message);

    void close() throws IOException;
}
