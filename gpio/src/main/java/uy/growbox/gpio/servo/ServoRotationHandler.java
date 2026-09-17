package uy.growbox.gpio.servo;

import com.pi4j.component.servo.ServoDriver;

import java.util.logging.Logger;

public class ServoRotationHandler {
    public static final int MOVE_STEP = 1;
    private final Logger log = Logger.getLogger(ServoRotationHandler.class.getSimpleName());

    private final ServoDriver servoDriver;
    private final int initialPulseWidth;
    private final int finalPulseWidth;

    private int direction = -1;

    private int currentPulseWidth;

    public ServoRotationHandler(ServoDriver servoDriver, int initialPulseWidth, int finalPulseWidth) {
        this.servoDriver = servoDriver;
        this.initialPulseWidth = initialPulseWidth;
        this.finalPulseWidth = finalPulseWidth;

        // motion starts with flipping direction to -1
        this.currentPulseWidth = (finalPulseWidth - initialPulseWidth) / 2;
    }

    public int getCurrentPulseWidth() {
        return currentPulseWidth;
    }

    public void moveCamera() {
        int movement = MOVE_STEP * direction;
        int newValue = currentPulseWidth + movement;

        if (newValue >= finalPulseWidth || newValue <= initialPulseWidth) {
            log.info("flipping motion");
            direction = direction * -1;
            return;
        }

        int servoPulseWidth = newValue;
        incrementPulse(movement);
    }

    public synchronized void setPulseWidth(int servoPulseWidth) {
        log.info("setting servo pulse to " + servoPulseWidth);
        currentPulseWidth = servoPulseWidth;
        servoDriver.setServoPulseWidth(servoPulseWidth);
    }

    public void incrementPulse(int increment) {
        final int width = currentPulseWidth + increment;
        log.info("incrementingPulse by " + increment);
        setPulseWidth(width);
    }
}