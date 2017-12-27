package com.trialanderror.robothandlers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class RelicGrabber {

    private DcMotor extendMotor;
    private Servo grabRelic;
    private Servo twistRelic;

    private static final double OPEN_CLAMP = 0.0;
    private static final double CLOSE_CLAMP = .5;
    private int grabberPos;

    public RelicGrabber(HardwareMap aHardwareMap) {

        extendMotor = aHardwareMap.dcMotor.get("extend");
        grabRelic = aHardwareMap.servo.get("grabr");
        twistRelic = aHardwareMap.servo.get("twistr");

    }
    public void horizontalMove() {
        extendMotor.setPower(0.5);
    }
    public void horizontalRetract() {
        extendMotor.setPower(-0.5);
    }
    public void noHorizMove() {
        extendMotor.setPower(0.0);
    }
    public void openRelic() {
        grabRelic.setPosition(OPEN_CLAMP);
    }
    public void clampRelic(){
        grabRelic.setPosition(CLOSE_CLAMP);
    }
}
