package com.trialanderror.sensorhandlers;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class MotorTouchSensor {

    private TouchSensor touchSensor;

    public MotorTouchSensor(TouchSensor aTouchSensor) {
        touchSensor = aTouchSensor;
    }
    public boolean isLowered() {
       return touchSensor.isPressed();
    }
}
