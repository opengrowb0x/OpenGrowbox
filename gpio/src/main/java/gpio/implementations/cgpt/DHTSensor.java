package gpio.implementations.cgpt;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;

public class DHTSensor {

    public enum Type { DHT11, DHT22, AM2302 }

    private final Context pi4j;
    private final Type type;
    private final int bcmPin;

    public DHTSensor(Context pi4j, Type type, int bcmPin) {
        this.pi4j = pi4j;
        this.type = type;
        this.bcmPin = bcmPin;
    }

    public static class Result {
        public final float temperature;
        public final float humidity;

        public Result(float temperature, float humidity) {
            this.temperature = temperature;
            this.humidity  = humidity;
        }
    }

    public Result read() throws InterruptedException {
        forceReleasePin();

        int[] data = new int[5];
        int maxTimings = 85;

        // === 1. Config as OUTPUT ===
        var output = pi4j.create(DigitalOutput.newConfigBuilder(pi4j)
                .id("dht-out")
                .address(bcmPin)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .build());

        // Start signal
        output.low();
        Thread.sleep(20);    // 20ms
        output.high();
        Thread.sleep(1);

        forceReleasePin();
//        pi4j.remove(output);

        // === 2. Reconfigure pin as INPUT ===
        var input = pi4j.create(DigitalInput.newConfigBuilder(pi4j)
                .id("dht-in")
                .address(bcmPin)
                .pull(PullResistance.PULL_UP)
                .build());

        int lastState = 1;
        int j = 0;

        for (int i = 0; i < maxTimings; i++) {

            int counter = 0;
            while (input.state().value().intValue() == lastState) {
                counter++;
                if (counter >= 255) break;
            }

            lastState = input.state().value().intValue();
            if (counter >= 255) break;

            if (i >= 4 && i % 2 == 0) {
                data[j / 8] <<= 1;
                if (counter > 50) {
                    data[j / 8] |= 1;
                }
                j++;
            }
        }

        //pi4j.remove(input);
        forceReleasePin();

        if (j >= 40) {
            int checksum = (data[0] + data[1] + data[2] + data[3]) & 0xFF;
            if (data[4] == checksum) {

                float humidity, temperature;

                if (type == Type.DHT11) {
                    humidity = data[0];
                    temperature = data[2];
                } else {
                    humidity = ((data[0] << 8) | data[1]) / 10f;
                    temperature = (((data[2] & 0x7F) << 8) | data[3]) / 10f;
                    if ((data[2] & 0x80) != 0) temperature = -temperature;
                }

                if (humidity > 100.0f) return null;

                return new Result(temperature, humidity);
            }
        }

        return null;
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
//        registry.all().stream()
//                .filter(io -> io.address() == bcmPin)
//                .forEach(io -> {
//                    try {
//                        registry.remove(io.id());
//                    } catch (Exception e) {
//                        System.err.println("Warning: failed to remove " + io.id());
//                    }
//                });
    }

}
