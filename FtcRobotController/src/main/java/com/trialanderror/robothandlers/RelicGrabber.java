package com.trialanderror.robothandlers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class RelicGrabber {

    private DcMotor extendMotor;
    private Servo grabRelic;
    private Servo twistRelic;

    private static final double OPEN_CLAMP = 0.095;
    private static final double CLOSE_CLAMP = 0.557;
    //.51

    private static final double TWIST_UP = .042;
    private static final double TWIST_DOWN = 0.1117;

    private double twistPosition;
    private double clampPosition = CLOSE_CLAMP;

    public RelicGrabber(HardwareMap aHardwareMap) {

        extendMotor = aHardwareMap.dcMotor.get("extend");
        grabRelic = aHardwareMap.servo.get("grabr");
        twistRelic = aHardwareMap.servo.get("twistr");

        extendMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        grabRelic.setPosition(CLOSE_CLAMP);
        twistRelic.setPosition(TWIST_DOWN);
    }
    public void horizontalMove() {
        extendMotor.setPower(1.0);
    }
    public void horizontalRetract() {
        extendMotor.setPower(-1.0);
    }
    public void noHorizMove() {
        extendMotor.setPower(0.0);
    }
    public void slowHorizMove() { extendMotor.setPower(0.35);}
    public void slowHorixRetract() { extendMotor.setPower(-0.35);}
    public void openRelic() {
        clampPosition = OPEN_CLAMP;
        grabRelic.setPosition(clampPosition);
    }
    public void clampRelic(){
        clampPosition = CLOSE_CLAMP;
        grabRelic.setPosition(clampPosition);
    }
    public void twistRelicUp() {
        twistRelic.setPosition(TWIST_UP);
    }
    public void twistRelicDown() {
        twistRelic.setPosition(TWIST_DOWN);
    }
    public void twistDeltaRelic(double delta) {
        twistPosition += delta;
        twistPosition = Range.clip(twistPosition, TWIST_UP, TWIST_DOWN);
        twistRelic.setPosition(twistPosition);
    }
    public void clampSmallDelta() {
        clampPosition +=.01;
        clampPosition = Range.clip(clampPosition, OPEN_CLAMP, CLOSE_CLAMP);
        grabRelic.setPosition(clampPosition);
    }
    public void openSmallDelta() {
        clampPosition -=.01;
        clampPosition = Range.clip(clampPosition, OPEN_CLAMP, CLOSE_CLAMP);
        grabRelic.setPosition(clampPosition);
    }
}
