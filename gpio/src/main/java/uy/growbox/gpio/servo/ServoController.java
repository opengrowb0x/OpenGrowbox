package uy.growbox.gpio.servo;

// Requires https://github.com/richardghirst/PiBits/tree/master/ServoBlaster
// Makefile  gcc -Wall -g -O2 -L/opt/vc/lib -I/opt/vc/include -I/opt/vc/include/interface/vmcs_host/linux -I/opt/vc/include/interface/vcos/pthreads -o servod servod.c mailbox.c -lm -lbcm_host
// #include <sys/sysmacros.h> in mailbox.c

// cd ServoBlaster/user
// FOR SG90 servos (1ms 1.5ms and 2ms)
// sudo ./growbox2/ServoBlaster/user/servod --min=1000us --max=2000us
// pi@growbox1 /opt/vc/include $ find . -name vchost_config.h
// GPIO 17 and 18
// echo 1=+10 > /dev/servoblaster
// echo 2=-5 > /dev/servoblaster
// to preview
// raspivid -p -f -t 60000

import com.pi4j.Pi4J;
import com.pi4j.component.servo.ServoDriver;
import com.pi4j.context.Context;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmType;

import java.io.IOException;
import java.util.logging.Logger;

public final class ServoController {
    private final PositionUpdatedListener listener;
    private Logger log = Logger.getLogger(ServoController.class.getSimpleName());

    private ServoRotationHandler verticalRotationServo;
    private ServoRotationHandler horizontalRotationServo;
    private Context pi4j;

    public interface PositionUpdatedListener {
        void onPositionUpdate(int x, int y);
    }

    public ServoController(Integer latestX, Integer latestY, int horizontalMin, int horizontalMax, int verticalMin, int verticalMax) throws IOException {
        this(latestX, latestY, horizontalMin, horizontalMax, verticalMin, verticalMax, null);
    }

    public ServoController(Integer latestX, Integer latestY, int horizontalMin, int horizontalMax, int verticalMin, int verticalMax, PositionUpdatedListener listener) throws IOException {
        this.listener = listener;
        try {
            // Initialize Pi4J context
            pi4j = Pi4J.newAutoContext();
            initializeServoPins(horizontalMin, horizontalMax, verticalMin, verticalMax);
            setCurrentPosition(latestX, latestY);
        } catch (IOException e) {
            log.severe("servo initialization failed: " + e.getMessage());
            if (pi4j != null) {
                pi4j.shutdown();
            }
            throw e;
        } catch (Exception e) {
            log.severe("unexpected error during servo initialization: " + e.getMessage());
            if (pi4j != null) {
                pi4j.shutdown();
            }
            throw new IOException(e);
        }
    }

    private void initializeServoPins(int horizontalMin, int horizontalMax, int verticalMin, int verticalMax) throws IOException {
        try {
            // Create PWM instances for servo control
            // Using GPIO 17 and 18 with software PWM
            Pwm horizontalPwm = pi4j.create(Pwm.newConfigBuilder(pi4j)
                    .id("HORIZONTAL_SERVO")
                    .name("Horizontal Rotation Servo")
                    .address(17) // GPIO 17
                    .pwmType(PwmType.SOFTWARE)
                    .frequency(50) // 50Hz standard for servos
                    .initial(0)    // Initial duty cycle (0%)
                    .shutdown(0)   // Duty cycle on shutdown (0%)
                    .build());

            Pwm verticalPwm = pi4j.create(Pwm.newConfigBuilder(pi4j)
                    .id("VERTICAL_SERVO")
                    .name("Vertical Rotation Servo")
                    .address(18) // GPIO 18
                    .pwmType(PwmType.SOFTWARE)
                    .frequency(50) // 50Hz standard for servos
                    .initial(0)    // Initial duty cycle (0%)
                    .shutdown(0)   // Duty cycle on shutdown (0%)
                    .build());

            log.info("Initialized horizontal servo on GPIO 17");
            log.info("Initialized vertical servo on GPIO 18");

            horizontalRotationServo = new ServoRotationHandler((ServoDriver) horizontalPwm, horizontalMin, horizontalMax);
            verticalRotationServo = new ServoRotationHandler((ServoDriver) verticalPwm, verticalMin, verticalMax);

        } catch (Exception e) {
            log.severe("Failed to initialize servo pins: " + e.getMessage());
            throw new IOException("Servo pin initialization failed", e);
        }
    }

    private void setCurrentPosition(Integer latestX, Integer latestY) {
        log.info("setting servos to last known position " + latestX + ", " + latestY);
        if (latestX != null) {
            horizontalRotationServo.setPulseWidth(latestX);
        }
        if (latestY != null) {
            verticalRotationServo.setPulseWidth(latestY);
        }
    }

    public final void moveCameraVertically() {
        try {
            verticalRotationServo.moveCamera();
            updateListener();
        } catch (Exception e) {
            log.warning("error moving camera vertically: " + e.getMessage());
        }
    }

    private void updateListener() {
        if (listener != null) {
            listener.onPositionUpdate(horizontalRotationServo.getCurrentPulseWidth(), verticalRotationServo.getCurrentPulseWidth());
        }
    }

    public final void moveCameraHorizontally() {
        try {
            horizontalRotationServo.moveCamera();
            updateListener();
        } catch (Exception e) {
            log.warning("error moving camera horizontally: " + e.getMessage());
        }
    }

    public final void incrementHorizontally(int value) {
        try {
            log.warning("setting pulse increment " + value);
            horizontalRotationServo.incrementPulse(value);
            updateListener();
        } catch (Exception e) {
            log.warning("error moving camera horizontally: " + e.getMessage());
        }
    }

    public final void incrementVertically(int value) {
        try {
            verticalRotationServo.incrementPulse(value);
            updateListener();
        } catch (Exception e) {
            log.warning("error moving camera vertically: " + e.getMessage());
        }
    }

    public void shutdown() {
        if (pi4j != null) {
            try {
                // Turn off servos before shutdown
                if (horizontalRotationServo != null) {
                    //horizontalRotationServo.turnOff();
                }
                if (verticalRotationServo != null) {
                    //verticalRotationServo.turnOff();
                }
                pi4j.shutdown();
                log.info("ServoController shutdown completed");
            } catch (Exception e) {
                log.warning("Error during shutdown: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        ServoController servoController = null;
        try {
            servoController = new ServoController(100, 175, 100, 120, 175, 185);
            for (int i = 0; i < 50; i++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //servoController.moveCameraVertically();
                servoController.moveCameraHorizontally();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (servoController != null) {
                servoController.shutdown();
            }
        }
    }
}