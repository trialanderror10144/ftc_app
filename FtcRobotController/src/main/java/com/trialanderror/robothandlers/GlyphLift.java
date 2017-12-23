package com.trialanderror.robothandlers;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class GlyphLift {

    DcMotor  liftG;
    CRServo leftSide;
    CRServo rightSide;

    private static final double LEFT_OPEN = 0.055;
    private static final double LEFT_CLOSED = 0.44;


    private static final double RIGHT_OPEN = 0.025;
    private static final double RIGHT_CLOSED =  -0.44;


    private static final double LEFT_MID = .537;
    private static final double LEFT_BARELY = .551;
    private static final double RIGHT_MID = -0.498;
    private static final double RIGHT_BARELY = -.486;

    private double leftPosition;
    private double rightPosition;

    public GlyphLift(HardwareMap aHardwareMap) {

        leftSide = aHardwareMap.crservo.get("leftGrip");
        rightSide = aHardwareMap.crservo.get("rightGrip");
        liftG = aHardwareMap.dcMotor.get("glyphLift") ;

        liftG.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        initServoPos();
    }

    public void initServoPos() {
        leftSide.setPower(LEFT_OPEN);
        rightSide.setPower(RIGHT_OPEN);
    }

    public void leftChangePos(double delta) {
        leftPosition += delta;
        leftPosition = Range.clip(leftPosition, LEFT_OPEN, LEFT_CLOSED);
       // leftSide.setPosition(leftPosition);
    }
    public void rightChangePos(double delta) {
        rightPosition += delta;
        rightPosition = Range.clip(rightPosition, RIGHT_CLOSED, RIGHT_OPEN);
       // rightSide.setPosition(rightPosition);
    }
    public void closeAuto() {
        leftSide.setPower(LEFT_CLOSED);
        rightSide.setPower(RIGHT_CLOSED);
    }
    public void openAuto() {
        leftSide.setPower(LEFT_OPEN);
        rightSide.setPower(RIGHT_OPEN);
    }
    public void midAuto() {
        leftSide.setPower(LEFT_MID);
        rightSide.setPower(RIGHT_MID);
    }
    public void bOpenAuto() {
        leftSide.setPower(LEFT_BARELY);
        rightSide.setPower(RIGHT_BARELY);
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