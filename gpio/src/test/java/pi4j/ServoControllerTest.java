package pi4j;

import org.junit.Test;
import uy.growbox.gpio.servo.ServoController;

import java.io.IOException;

public class ServoControllerTest {

    @Test
    public void verifyMove() throws IOException {
        ServoController servoController = new ServoController(null, null, 100, 120, 180, 190);

        for (int i = 0; i < 50; i++) {
            servoController.moveCameraVertically();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
