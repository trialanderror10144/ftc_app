package com.trialanderror.sensorhandlers;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class GyroTurnSensor {

    private ModernRoboticsI2cGyro gyroSensor;

    private int virtualZeroGyro;
    private static final int CLOCKWISE = -1;

    public GyroTurnSensor(GyroSensor aGyroscope) {
        gyroSensor = (ModernRoboticsI2cGyro) aGyroscope;
        virtualZeroGyro = 0;
        calibrateGyro();
    }
    public void resetGyro() {
        virtualZeroGyro = gyroSensor.getIntegratedZValue();
    }
    public void calibrateGyro() {
        gyroSensor.calibrate();
    }
    public int headingGyro() {
        return CLOCKWISE*(gyroSensor.getIntegratedZValue() - virtualZeroGyro);
    }
}
