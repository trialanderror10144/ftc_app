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
    private static final double CLOSE_CLAMP = 0.51;
    private static final double START_CLAMP = 0.54;

    private static final double TWIST_UP = .042;
    private static final double TWIST_DOWN = 0.1117;

    private static final int ENCODER_PORT_1 = 1;
    private double twistPosition;
    private double clampPosition;

    public RelicGrabber(HardwareMap aHardwareMap) {

        extendMotor = aHardwareMap.dcMotor.get("extend");
        grabRelic = aHardwareMap.servo.get("grabr");
        twistRelic = aHardwareMap.servo.get("twistr");

        extendMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extendMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        grabRelic.setPosition(START_CLAMP);
        twistRelic.setPosition(TWIST_DOWN);
    }
    public void horizontalMove() {
        extendMotor.setPower(0.7);
    }
    public void horizontalRetract() {
        extendMotor.setPower(-0.7);
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
