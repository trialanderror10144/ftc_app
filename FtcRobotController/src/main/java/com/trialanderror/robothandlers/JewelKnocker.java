package com.trialanderror.robothandlers;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

public class JewelKnocker {

    private Servo jewelDropper;
    private Servo jewelKnocker;

    private static final double UP_POSITION = 0.217;
    private static final double MID_POS = .385;
    private static final double DOWN_POSITION = 0.55;

    private static final double MID_POSITION = .32157;
    private static final double LEFT_POSITION = .07843;
    private static final double RIGHT_POSITION = .56471;

    public JewelKnocker(HardwareMap aHardwareMap){

        jewelDropper = aHardwareMap.servo.get("dropper");
        jewelKnocker = aHardwareMap.servo.get("knocker");

        initServoPos();
    }
    public void initServoPos() {
        jewelDropper.setPosition(UP_POSITION);
        jewelKnocker.setPosition(MID_POSITION);
    }
    public void changeGoDown() {
        jewelDropper.setPosition(DOWN_POSITION);
    }
    public void changeGoUp() {
        jewelDropper.setPosition(UP_POSITION);
    }
    public void midTest() { jewelDropper.setPosition(MID_POS);
    }
    public void hitLeft(){
        jewelKnocker.setPosition(LEFT_POSITION);
    }
    public void hitRight() {
        jewelKnocker.setPosition(RIGHT_POSITION);
    }
    public void resetPos() {
        jewelKnocker.setPosition(MID_POSITION);
    }
}
