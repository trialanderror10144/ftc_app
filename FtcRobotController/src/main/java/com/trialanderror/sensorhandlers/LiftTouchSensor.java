package com.trialanderror.sensorhandlers;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Created by Esposito's 9-16 on 12/14/2017.
 */

public class LiftTouchSensor {

    private TouchSensor myNameIsJeff;

    public LiftTouchSensor(HardwareMap aHardwareMap) {

        myNameIsJeff = aHardwareMap.touchSensor.get("tsensor");
    }
    public boolean isLowered() {
       return myNameIsJeff.isPressed();
    }
}
