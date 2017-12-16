package com.trialanderror.robothandlers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RelicGrabber {

    private DcMotor extendMotor;
    private DcMotor angleMotor;

    private int grabberPos;


    public RelicGrabber(HardwareMap aHardwareMap) {

        extendMotor = aHardwareMap.dcMotor.get("extend");
        angleMotor = aHardwareMap.dcMotor.get("angle");

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
