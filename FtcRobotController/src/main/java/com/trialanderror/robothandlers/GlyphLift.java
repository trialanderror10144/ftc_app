package com.trialanderror.robothandlers;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class GlyphLift {

    private DcMotor  liftG;
    private Servo leftSide;
    private Servo rightSide;


    private static final double LEFT_START = .17;
    private static final double RIGHT_START = .81;

    private static final double LEFT_OPEN = 0.20;
    private static final double LEFT_CLOSED = 0.79;
    private static final double LEFT_MID = .47;
    private static final double LEFT_BARELY = .69;


    private static final double RIGHT_OPEN = 0.77;
    private static final double RIGHT_CLOSED = 0.21;
    private static final double RIGHT_MID = 0.5015;
    private static final double RIGHT_BARELY = .27;


    private double leftPosition;
    private double rightPosition;

    public GlyphLift(HardwareMap aHardwareMap) {

        leftSide = aHardwareMap.servo.get("leftGrip");
        rightSide = aHardwareMap.servo.get("rightGrip");
        liftG = aHardwareMap.dcMotor.get("glyphLift") ;

        liftG.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        initServoPos();
    }

    public void initServoPos() {
        leftSide.setPosition(LEFT_START);
        rightSide.setPosition(RIGHT_START);
    }

    public void leftChangePos(double delta) {
        leftPosition += delta;
        leftPosition = Range.clip(leftPosition, LEFT_OPEN, LEFT_CLOSED);
        leftSide.setPosition(leftPosition);
    }
    public void rightChangePos(double delta) {
        rightPosition += delta;
        rightPosition = Range.clip(rightPosition, RIGHT_CLOSED, RIGHT_OPEN);
        rightSide.setPosition(rightPosition);
    }
    public void closeAuto() {
        leftSide.setPosition(LEFT_CLOSED);
        rightSide.setPosition(RIGHT_CLOSED);
    }
    public void openAuto() {
        leftSide.setPosition(LEFT_OPEN);
        rightSide.setPosition(RIGHT_OPEN);
    }
    public void midAuto() {
        leftSide.setPosition(LEFT_MID);
        rightSide.setPosition(RIGHT_MID);
    }
    public void bOpenAuto() {
        leftSide.setPosition(LEFT_BARELY);
        rightSide.setPosition(RIGHT_BARELY);
    }
    public void raiseLiftPowerUp() {
        liftG.setPower(-0.5);
    }
    public void lowerLiftPowerDown() {
        liftG.setPower(0.5);
    }
    public void stop() {
        liftG.setPower(0.0);
    }
}