package uy.growbox.gpio;

import gpio.MinimalGPIOApi;
import gpio.implementations.DummyGPIOController;
import gpio.implementations.Pi4jApi;

public class Pi4jClient {


    /**
     * java -cp growbox-control-1.0.jar uy.growbox.gpio.Pi4jClient get 10
     * @param args
     */
    public static void main(String[] args) throws Exception {
        boolean piMode = true;

        MinimalGPIOApi gpioApi = piMode ? new Pi4jApi() : new DummyGPIOController();

        if (args.length < 2) {
            System.err.println("Usage: java -jar {jar_name} command {inputOutputId} [sensorValue]");
            System.err.println("ex: java -cp growbox-control-1.0.jar uy.growbox.gpio.Pi4jClient get 10");
            System.err.println("ex: java -jar sensorsApi.jar set 10 1");
            return;
        }

        String cmd = args[0];
        String inputOutputId = args[1];
        String sensorValue = null;

        try {
            sensorValue = args[2];
        } catch (ArrayIndexOutOfBoundsException e) {
        }


        final int port = Integer.parseInt(inputOutputId);
        if ("set".equalsIgnoreCase(cmd)) {
            final boolean state = "on".equalsIgnoreCase(sensorValue);
            gpioApi.writeOutputPort(port, state);
            System.out.println("write " + state + " to port: " + port);
        } else if ("get".equalsIgnoreCase(cmd)) {
            Boolean result = gpioApi.readOutputPort(port);
            System.out.println(result);
        } else {
            System.out.println("Unknown command: " + cmd);
        }
        gpioApi.terminate();
    }
}
