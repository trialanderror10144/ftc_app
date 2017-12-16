package com.trialanderror.sensorhandlers;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cDevice;

/**
 * Created by Esposito's 9-16 on 11/9/2017.
 */

public class PanelRangeSensor {

    private ModernRoboticsI2cRangeSensor rSensor;

    public PanelRangeSensor(HardwareMap aHardwareMap) {
        rSensor = aHardwareMap.get(ModernRoboticsI2cRangeSensor.class, "rsensor");

    }
    public double getUltrasonicReading() {
        return  rSensor.rawUltrasonic();

    }

}
