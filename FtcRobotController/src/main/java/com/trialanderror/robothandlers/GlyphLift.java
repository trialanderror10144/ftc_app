package com.trialanderror.robothandlers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class GlyphLift {

    DcMotor  liftG;
    Servo leftSide;
    Servo rightSide;

    private static final double LEFT_OPEN = .518;
    private static final double LEFT_CLOSED = 0.56;
    private static final double LEFT_MID = .537;
    private static final double LEFT_BARELY = .551;


    private static final double RIGHT_OPEN = .515;
    private static final double RIGHT_CLOSED =  0.478;
    private static final double RIGHT_MID = 0.498;
    private static final double RIGHT_BARELY = .486;

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
        leftSide.setPosition(LEFT_OPEN);
        rightSide.setPosition(RIGHT_OPEN);
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