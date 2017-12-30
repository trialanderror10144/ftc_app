package com.trialanderror.robothandlers;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

public class JewelKnocker {

    private Servo jewelKnocker;

    private static final double UP_POSITION = 0.098;
    private static final double DOWN_POSITION = 0.403;

    public JewelKnocker(HardwareMap aHardwareMap){
        jewelKnocker = aHardwareMap.servo.get("knocker");
        initServoPos();
    }
    public void initServoPos() {
        jewelKnocker.setPosition(UP_POSITION);
    }
    public void changeGoDown() {
        jewelKnocker.setPosition(DOWN_POSITION);
    }
    public void changeGoUp() {
        jewelKnocker.setPosition(UP_POSITION);
    }
}
