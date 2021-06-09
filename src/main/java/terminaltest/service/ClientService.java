package terminaltest.service;

import java.io.IOException;

public interface ClientService {
    void stop() throws IOException;

    void start();

    void init(String host, int port);

    void close() throws Exception;
}
