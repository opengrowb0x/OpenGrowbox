package gpio;

public interface MinimalGPIOApi {

    // SEE DHT11.java as an example
    void initializeOutputPortMode(int port);

    void initializeInputPortMode(int port);

    Boolean readOutputPort(int port);

    void writeOutputPort(int port, boolean state) throws Exception;

    int readInputPort(int port) throws Exception;

    void terminate();
}
