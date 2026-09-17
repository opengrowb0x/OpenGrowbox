package gpio.implementations.cgpt;

import com.pi4j.Pi4J;
import com.pi4j.exception.ShutdownException;

public class DHTReader {


    /**
     * java -cp growbox-control-1.0.jar gpio.implementations.cgpt.DHTReader 11 22
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.out.println("Usage: java DHTReader [11|22|2302] <BCM GPIO>");
            System.exit(1);
        }

        int type = Integer.parseInt(args[0]);
        int pin = Integer.parseInt(args[1]);

        DHTSensor.Type sensorType;

        switch (type) {
            case 11: sensorType = DHTSensor.Type.DHT11; break;
            case 22: sensorType = DHTSensor.Type.DHT22; break;
            case 2302: sensorType = DHTSensor.Type.AM2302; break;
            default:
                System.out.println("Unknown sensor type.");
                return;
        }

        var pi4j = Pi4J.newAutoContext();
        try {
            var sensor = new DHTSensor(pi4j, sensorType, pin);

            DHTSensor.Result result = sensor.read();

            if (result != null) {
                System.out.printf("Temp=%.1f°  Humidity=%.1f%%\n",
                        result.temperature,
                        result.humidity);
            } else {
                System.out.println("Failed to get reading. Try again!");
                System.exit(1);
            }


        } catch (InterruptedException | ShutdownException e) {
            throw new RuntimeException(e);
        } finally {
            pi4j.shutdown();
        }
    }
}
