package gpio.implementations;

import gpio.GPIOApi;
import uy.growbox.gpio.exceptions.ExecutionException;

import java.io.IOException;
import java.util.Arrays;

public class DummyGPIOController implements GPIOApi {

    public DummyGPIOController() {
        System.out.println("init DummyGPIOController");
    }

    @Override
    public void initializeOutputPortMode(int port) {

    }

    @Override
    public void initializeInputPortMode(int port) {

    }

    @Override
    public Boolean readOutputPort(int port) {
        System.out.println("<-- readOutputPort " + port + " state " + true);
        return true;
    }

    @Override
    public void writeOutputPort(int port, boolean state) {
        System.out.println("--> writeOutputPort " + port + " state " + state);
    }

    private String[] getOutputWriteCommand(boolean state, int gpioPort) {
        return new String[]{"gpio", "-g", "write", String.valueOf(gpioPort), state ? "0" : "1"};
    }

    @Override
    public int readInputPort(int port) {
        System.out.println("<== readInputPort " + port + " value 20%");
        return 20;
    }

    @Override
    public void terminate() {

    }

    public final String executeCommand(String[] cmd) {
        System.out.println("execute cmd:" );
        Arrays.stream(cmd).forEach((i) -> System.out.print(i+ " "));
        return cmd[0];
    }

    @Override
    public String executeCommand(String[] cmd, boolean checkResult) throws ExecutionException, IOException, InterruptedException {
        return executeCommand(cmd);
    }
}
