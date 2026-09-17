package gpio.implementations;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import gpio.MinimalGPIOApi;

public class Pi4jApi implements MinimalGPIOApi {

    private Context pi4j;

    public Pi4jApi() {
        init();
    }

    void init() {
        this.pi4j = Pi4J.newAutoContext();

    }

    @Override
    public void initializeOutputPortMode(int port) {
        DigitalOutput portOutput = pi4j.digitalOutput().create(port);
    }

    @Override
    public void initializeInputPortMode(int port) {
        DigitalInput portOutput = pi4j.digitalInput().create(port);
    }

    @Override
    public Boolean readOutputPort(int port) {
        DigitalOutput portOutput = pi4j.digitalOutput().create(port);
        var currentState = portOutput.state();
        System.out.println("readOutputPort " + currentState.value().intValue());
        return currentState.isLow();
    }

    @Override
    public void writeOutputPort(int port, boolean state) throws Exception {
//        DigitalOutputConfig config = DigitalOutput.newConfigBuilder(pi4j)
//                .address(port)
//                .shutdown(DigitalState.HIGH)
//                .initial(DigitalState.HIGH)
//                .build();
//        DigitalOutput portOutput = pi4j.digitalOutput().create(config);
        var portOutput = pi4j.dout().create(port);
        //portOutput.config().shutdownState(DigitalState.HIGH);

//        if (state) {
//            portOutput.low();
//        } else  {
//            portOutput.high();
//        }
//or
        portOutput.setState(state);
    }

    @Override
    public int readInputPort(int port) throws Exception {
        DigitalInput portOutput = pi4j.digitalInput().create(port);
        return portOutput.state().value().intValue();
    }

    @Override
    public void terminate() {
        pi4j.shutdown();
    }
}
