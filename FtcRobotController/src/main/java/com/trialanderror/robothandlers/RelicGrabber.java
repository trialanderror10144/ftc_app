package com.trialanderror.robothandlers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class RelicGrabber {

    private DcMotor extendMotor;
    private Servo leftGrab;
    private Servo rightGrab;

    private int grabberPos;

    public RelicGrabber(HardwareMap aHardwareMap) {

        extendMotor = aHardwareMap.dcMotor.get("extend");
        leftGrab = aHardwareMap.servo.get("leftgrab");
        rightGrab = aHardwareMap.servo.get("rightgrab");

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
}
