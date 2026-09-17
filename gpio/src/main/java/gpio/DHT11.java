package gpio;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;

//TODO this should replace the dht python script.

/**
 * pi@growbox6:/opt/growbox-control $ java -cp growbox-control-1.0.jar gpio.DHT11
 */
public class DHT11 {
    private static final int MAXTIMINGS = 85;
    public static final int TEMPERATURE_SENSOR_PIN = 22;
    private static final int MAX_READS = 40;
    private final int[] dht11_dat = { 0, 0, 0, 0, 0 };

    private final Context pi4j;
    private DigitalOutput digitalOutput;
    private DigitalInput digitalInput;

    public DHT11() {
        // Initialize Pi4J with auto context
        pi4j = Pi4J.newAutoContext();
        forceReleasePin();

        try {
            // Configure digital output
            digitalOutput = pi4j.create(DigitalOutput.newConfigBuilder(pi4j)
                    .id("DHT11_OUTPUT")
                    .name("DHT11 Sensor Output")
                    .address(TEMPERATURE_SENSOR_PIN)
                    .build());

            // Configure digital input
            digitalInput = pi4j.create(DigitalInput.newConfigBuilder(pi4j)
                    .id("DHT11_INPUT")
                    .name("DHT11 Sensor Input")
                    .address(TEMPERATURE_SENSOR_PIN)
                    .pull(PullResistance.PULL_UP)
                    .build());

        } catch (Exception e) {
            System.out.println(" ==>> GPIO SETUP FAILED: " + e.getMessage());
        }
    }

    public void getTemperature(final int pin) {
        var laststate = DigitalState.HIGH.getValue().intValue();
        int j = 0;
        dht11_dat[0] = dht11_dat[1] = dht11_dat[2] = dht11_dat[3] = dht11_dat[4] = 0;

        try {

            // Set as output and send start signal
            digitalOutput.low();
            Thread.sleep(18); // Wait 18ms

            digitalOutput.high();

            // Switch to input mode by using the digital input instance
            // Note: In Pi4J V2, we don't dynamically change pin modes like in V1
            // We use separate instances for input and output

            for (int i = 0; i < MAXTIMINGS; i++) {
                int counter = 0;
                while (digitalInput.state().getValue().intValue() == laststate) {
                    counter++;
                    // Using Thread.sleep for microsecond delays isn't precise,
                    // but it's the best we can do in pure Java
                    try { Thread.sleep(0, 1000); } catch (InterruptedException ie) {} // ~1μs
                    if (counter == 255) {
                        break;
                    }
                }

                laststate = digitalInput.state().getValue().intValue();

                if (counter == 255) {
                    break;
                }

                /* ignore first 3 transitions */
                if (i >= 4 && i % 2 == 0) {
                    /* shove each bit into the storage bytes */
                    dht11_dat[j / 8] <<= 1;
                    if (counter > 16) {
                        dht11_dat[j / 8] |= 1;
                    }
                    j++;
                }
            }

            // check we read 40 bits (8bit x 5 ) + verify checksum in the last byte
            if (j >= 40 && checkParity()) {
                float h = (float) ((dht11_dat[0] << 8) + dht11_dat[1]) / 10;
                if (h > 100) {
                    h = dht11_dat[0]; // for DHT11
                }
                float c = (float) (((dht11_dat[2] & 0x7F) << 8) + dht11_dat[3]) / 10;
                if (c > 125) {
                    c = dht11_dat[2]; // for DHT11
                }
                if ((dht11_dat[2] & 0x80) != 0) {
                    c = -c;
                }
                final float f = c * 1.8f + 32;
                System.out.println("Humidity = " + h + " Temperature = " + c + "(" + f + "f)");
            } else {
                System.out.println("Data not good, skip");
            }

        } catch (Exception e) {
            System.out.println("Error reading DHT11: " + e.getMessage());
        }
    }

    private boolean checkParity() {
        return dht11_dat[4] == (dht11_dat[0] + dht11_dat[1] + dht11_dat[2] + dht11_dat[3] & 0xFF);
    }

    public void shutdown() {
        if (pi4j != null) {
            pi4j.removeAllInitializedListeners();
            pi4j.shutdown();
        }
    }

    private void forceReleasePin() {
        var registry = pi4j.registry();

        // Remove existing I/O instances for this address
        registry.all().forEach((s, io) -> {
            try {
                registry.remove(io.id());
            } catch (Exception e) {
                System.err.println("Warning: failed to remove " + io.id());
            }
        });
//        registry.all().keySet().stream()
////                .filter(io -> io.address() == bcmPin)
//                .forEach(io -> {
//                    try {
//                        registry.remove(io.id());
//                    } catch (Exception e) {
//                        System.err.println("Warning: failed to remove " + io.id());
//                    }
//                });
    }

    public static void main(final String ars[]) throws Exception {
        final DHT11 dht = new DHT11();
        try {
            for (int i = 0; i < MAX_READS; i++) {
                Thread.sleep(2000);
                dht.getTemperature(TEMPERATURE_SENSOR_PIN);
            }
            System.out.println("Done!!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dht.shutdown();
        }
    }
}