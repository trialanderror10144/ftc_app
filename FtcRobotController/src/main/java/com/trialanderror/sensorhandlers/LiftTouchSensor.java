package com.trialanderror.sensorhandlers;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class LiftTouchSensor {

    private TouchSensor liftTouchSesnor;

    public LiftTouchSensor(HardwareMap aHardwareMap) {
        liftTouchSesnor = aHardwareMap.touchSensor.get("tsensor");
    }
    public boolean isLowered() {
       return liftTouchSesnor.isPressed();
    }
}
