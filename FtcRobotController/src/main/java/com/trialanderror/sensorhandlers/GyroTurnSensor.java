package com.trialanderror.sensorhandlers;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class GyroTurnSensor {

    private ModernRoboticsI2cGyro gyroSensor;

    private int virtualZeroGyro;
    private static final int CLOCKWISE = -1;

    public GyroTurnSensor(HardwareMap aHardwareMap) {
        gyroSensor = (ModernRoboticsI2cGyro) aHardwareMap.gyroSensor.get("gyro");

        gyroSensor.calibrate();
    }
    public void resetGyro() {
        virtualZeroGyro = gyroSensor.getIntegratedZValue();
    }
    public int headingGyro() {
        return CLOCKWISE*(gyroSensor.getIntegratedZValue() - virtualZeroGyro);
    }
}
