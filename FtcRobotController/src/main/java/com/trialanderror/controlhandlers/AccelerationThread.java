package com.trialanderror.controlhandlers;

import com.trialanderror.hardwarehandlers.AccelerationMotor;
/**
 * Created by Esposito's 9-16 on 11/7/2017.
 */

public class AccelerationThread implements Runnable {

    private static final int MAX_MOTORS = 8;
    private static final int UPDATES_PER_SECOND = 100;
    public static final int UPDATE_PERIOD_MS = 1000 / UPDATES_PER_SECOND;

    private AccelerationMotor[] acceleratedMotors = new AccelerationMotor[MAX_MOTORS];
    private boolean isAccelerationControlRunning = false;
    private int motorCount = 0;
    private Thread accelerationControlThread;

    public AccelerationThread() {

    }

    public void addMotor(AccelerationMotor aMotor) {
        acceleratedMotors[motorCount++] = aMotor;
    }

    @Override
    public synchronized void run() {
        while (isAccelerationControlRunning) {
            for (int i = 0; i < motorCount; i++) {
                acceleratedMotors[i].update();
            }

            try {
                Thread.sleep(UPDATE_PERIOD_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void start() {
        if(!isAccelerationControlRunning) {
            isAccelerationControlRunning = true;
            accelerationControlThread = new Thread(this);
            accelerationControlThread.start();
        }
    }

    public void stop() {
        isAccelerationControlRunning = false;

        for (int i = 0; i < motorCount; i++) {
            acceleratedMotors[i].stopMotorHard();
        }
    }

}
