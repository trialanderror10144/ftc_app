package com.trialanderror.robothandlers;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.Range;

public class JewelKnocker {

    Servo JewelKnocker;

    private static final double UP_POSITION = 0.098;
    private static final double DOWN_POSITION = 0.403;

    private double JewelPos;

    public JewelKnocker(HardwareMap aHardwareMap){

        JewelKnocker = aHardwareMap.servo.get("knocker");
        initServoPos();
    }
    public void initServoPos() {
        JewelKnocker.setPosition(UP_POSITION);
    }
    public void changeGoDown() {
        JewelKnocker.setPosition(DOWN_POSITION);
    }
    public void changeGoUp() {
        JewelKnocker.setPosition(UP_POSITION);
    }
}
