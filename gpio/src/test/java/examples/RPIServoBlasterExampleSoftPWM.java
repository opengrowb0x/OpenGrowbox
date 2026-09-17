package examples;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmType;

public class RPIServoBlasterExampleSoftPWM {

    public static void main(String[] args) throws Exception {
        String servoId = null;
        if (args.length > 0) {
            servoId = args[0];
        }

        Context pi4j = Pi4J.newAutoContext();

        try {
            // Using software PWM on any GPIO pin
            Pwm servo17 = pi4j.create(Pwm.newConfigBuilder(pi4j)
                    .id("SERVO_17")
                    .name("Servo GPIO 17")
                    .address(17) // Any GPIO pin
                    .pwmType(PwmType.SOFTWARE)
                    .frequency(50) // 50Hz for servos
                    .initial(0)
                    .shutdown(0)
                    .build());

            Pwm servo18 = pi4j.create(Pwm.newConfigBuilder(pi4j)
                    .id("SERVO_18")
                    .name("Servo GPIO 18")
                    .address(18) // Any GPIO pin
                    .pwmType(PwmType.SOFTWARE)
                    .frequency(50)
                    .initial(0)
                    .shutdown(0)
                    .build());

            Pwm servoDriver = servo17;
            if ("1".equalsIgnoreCase(servoId)) {
                servoDriver = servo18;
            }

            System.out.println("Using servo pin: " + servoDriver.config().address() +
                    "\nFrequency: " + servoDriver.getFrequency() + "Hz" +
                    "\nPWM Type: " + servoDriver.config().pwmType());

            long start = System.currentTimeMillis();
            int WAIT_TIME_BETWEEN_MOVES = 500;

            while (System.currentTimeMillis() - start < 30000) {
                for (int pulse = 100; pulse < 200; pulse += 1) {
                    double dutyCycle = pulseToDutyCycle(pulse);
                    System.out.println("Setting pulse to " + pulse + "us, duty cycle: " + dutyCycle + "%");
                    servoDriver.on(dutyCycle);
                    Thread.sleep(WAIT_TIME_BETWEEN_MOVES);
                }
                for (int pulse = 200; pulse > 100; pulse -= 1) {
                    double dutyCycle = pulseToDutyCycle(pulse);
                    System.out.println("Setting pulse to " + pulse + "us, duty cycle: " + dutyCycle + "%");
                    servoDriver.on(dutyCycle);
                    Thread.sleep(WAIT_TIME_BETWEEN_MOVES);
                }
            }

            servoDriver.off();
            System.out.println("Exiting RPIServoBlasterExample");

        } finally {
            pi4j.shutdown();
        }
    }

    private static double pulseToDutyCycle(int pulse) {
        double pulseWidthUs = pulse * 10.0;
        return (pulseWidthUs / 20000.0) * 100.0;
    }
}