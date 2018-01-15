package com.trialanderror.robothandlers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.trialanderror.controlhandlers.AccelerationThread;
import com.trialanderror.hardwarehandlers.AccelerationMotor;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.*;


public class Drivetrain {

    private static final int ENCODER_PORT_1 = 1;
    private static final int ENCODER_PORT_2 = 2;
    private static final double DEFAULT_PID_TURNING_BASE_POWER = 0.23;
    private static final double WHEEL_ACCEL_SPEED_PER_SECOND_STRAIGHT = 0.8;
    private static final double WHEEL_DECEL_SPEED_PER_SECOND_STRAIGHT = 15;
    private static final double WHEEL_ACCEL_SPEED_PER_SECOND_TURNING = 15;
    private static final double WHEEL_DECEL_SPEED_PER_SECOND_TURNING = 15;
    private static final double WHEEL_MINIMUM_POWER = 0.3;
    private static final double WHEEL_MAXIMUM_POWER = 1.0;

    private int driveEncoderCorrectionPassings;
    private final int driveEncoderCorrectionThreshold = 3;
    private double measuredPidTurningBasePower;
    private int virtualEncoderZeroLeft;
    private int virtualEncoderZeroRight;

    private AccelerationThread wheelAccelerationThread;
    private DcMotorController wheelControllerLeft;
    private DcMotorController wheelControllerRight;

    private AccelerationMotor leftFront;
    private AccelerationMotor leftBack;
    private AccelerationMotor rightFront;
    private AccelerationMotor rightBack;


    public Drivetrain(HardwareMap aHardwareMap) {
        leftFront = new AccelerationMotor(aHardwareMap.dcMotor.get("leftFront") , WHEEL_ACCEL_SPEED_PER_SECOND_STRAIGHT, WHEEL_DECEL_SPEED_PER_SECOND_STRAIGHT, WHEEL_MINIMUM_POWER, WHEEL_MAXIMUM_POWER);
        leftBack = new AccelerationMotor(aHardwareMap.dcMotor.get("leftBack"), WHEEL_ACCEL_SPEED_PER_SECOND_STRAIGHT, WHEEL_DECEL_SPEED_PER_SECOND_STRAIGHT, WHEEL_MINIMUM_POWER, WHEEL_MAXIMUM_POWER);
        rightFront = new AccelerationMotor(aHardwareMap.dcMotor.get("rightFront"), WHEEL_ACCEL_SPEED_PER_SECOND_STRAIGHT, WHEEL_DECEL_SPEED_PER_SECOND_STRAIGHT, WHEEL_MINIMUM_POWER, WHEEL_MAXIMUM_POWER);
        rightBack = new AccelerationMotor(aHardwareMap.dcMotor.get("rightBack"), WHEEL_ACCEL_SPEED_PER_SECOND_STRAIGHT, WHEEL_DECEL_SPEED_PER_SECOND_STRAIGHT, WHEEL_MINIMUM_POWER, WHEEL_MAXIMUM_POWER);

        wheelControllerLeft = aHardwareMap.dcMotorController.get("Front Motors");
        wheelControllerRight = aHardwareMap.dcMotorController.get("Back Motors");
        rightFront.setDirection(REVERSE);
        rightBack.setDirection(REVERSE);
    //    runWithoutEncoderPid();

      //  driveEncoderCorrectionPassings = 0;
    //    measuredPidTurningBasePower = DEFAULT_PID_TURNING_BASE_POWER;

        wheelAccelerationThread = new AccelerationThread();
        wheelAccelerationThread.addMotor(leftFront);
        wheelAccelerationThread.addMotor(leftBack);
        wheelAccelerationThread.addMotor(rightFront);
        wheelAccelerationThread.addMotor(rightBack);
        wheelAccelerationThread.start();
    }

    public void brake() {
        leftFront.stopMotorHard();
        leftBack.stopMotorHard();
        rightFront.stopMotorHard();
        rightBack.stopMotorHard();
    }
    public int getEncoderLeft() {
        return wheelControllerLeft.getMotorCurrentPosition(ENCODER_PORT_1) - virtualEncoderZeroLeft;
    }

    public int getEncoderRight() {
        return wheelControllerRight.getMotorCurrentPosition(ENCODER_PORT_1) - virtualEncoderZeroRight;
    }

    public double getPowerLeft() {
        return leftFront.getCurrentPower();
    }

    public double getPowerRight() {
        return rightFront.getCurrentPower();
    }

    public int getEncodersMagnitude() {
        return (Math.abs(getEncoderLeft()) + Math.abs(getEncoderRight())) / 2;
    }

    public void setBrakeModeAuto() {
        leftFront.autoBrakeMode();
        leftBack.autoBrakeMode();
        rightFront.autoBrakeMode();
        rightBack.autoBrakeMode();
    }

    public void setBrakeModeTele() {
        leftFront.teleBrakeMode();
        leftBack.teleBrakeMode();
        rightFront.teleBrakeMode();
        rightBack.teleBrakeMode();
    }

    public boolean isDriveEncodersPast(int aDistance) {
        if(Math.abs(getEncodersMagnitude()) >= aDistance) driveEncoderCorrectionPassings++;
        else driveEncoderCorrectionPassings = 0;
        return driveEncoderCorrectionPassings >= driveEncoderCorrectionThreshold;
    }

    public boolean isEncoderLeftReset() {
        return Math.abs(getEncoderLeft()) <= 10;
    }

    public boolean isEncoderRightReset() {
        return Math.abs(getEncoderRight()) <= 10;
    }

    public boolean isEncodersReset() {
        return isEncoderLeftReset() && isEncoderRightReset();
    }

  /*  public boolean isStalling() {
        //TODO: Implement isStalling.
        return false;
    } */

    private void resetEncoderLeftVirtually() {
        virtualEncoderZeroLeft = wheelControllerLeft.getMotorCurrentPosition(ENCODER_PORT_1);
    }

    private void resetEncoderRightVirtually() {
        virtualEncoderZeroRight = wheelControllerRight.getMotorCurrentPosition(ENCODER_PORT_1);
    }

    public void resetEncoders() {
        driveEncoderCorrectionPassings = 0;

        resetEncoderLeftVirtually();
        resetEncoderRightVirtually();
    }

    public void resetEncodersPhysically() {
        driveEncoderCorrectionPassings = 0;
        wheelControllerLeft.setMotorMode(ENCODER_PORT_1, DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        wheelControllerRight.setMotorMode(ENCODER_PORT_1, DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void runWithoutEncoder() {
        wheelControllerLeft.setMotorMode(ENCODER_PORT_1, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheelControllerLeft.setMotorMode(ENCODER_PORT_2, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheelControllerRight.setMotorMode(ENCODER_PORT_1, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheelControllerRight.setMotorMode(ENCODER_PORT_2, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setPower(double aLeftPower, double aRightPower) {
        if((aLeftPower < 0) == (aRightPower < 0)) setAccelerationRate(WHEEL_ACCEL_SPEED_PER_SECOND_STRAIGHT, WHEEL_DECEL_SPEED_PER_SECOND_STRAIGHT);
        else setAccelerationRate(WHEEL_ACCEL_SPEED_PER_SECOND_TURNING, WHEEL_DECEL_SPEED_PER_SECOND_TURNING);

        leftFront.setTargetPower(aLeftPower);
        leftBack.setTargetPower(aLeftPower);
        rightFront.setTargetPower(aRightPower);
        rightBack.setTargetPower(aRightPower);
    }

    public void setPowerWithoutAcceleration(double aLeftPower, double aRightPower) {
        leftFront.setDirectPower(aLeftPower);
        leftBack.setDirectPower(aLeftPower);
        rightFront.setDirectPower(aRightPower);
        rightBack.setDirectPower(aRightPower);
    }

    public void setPowerPidCorrection(double aLeftPower, double aRightPower) {
        if(aLeftPower > 0) {
            leftFront.setDirectPower(aLeftPower + measuredPidTurningBasePower);
            leftBack.setDirectPower(aLeftPower + measuredPidTurningBasePower);
        }
        else if(aLeftPower < 0) {
            leftFront.setDirectPower(aLeftPower - measuredPidTurningBasePower);
            leftBack.setDirectPower(aLeftPower - measuredPidTurningBasePower);
        }
        else {
            leftFront.setDirectPower(0);
            leftBack.setDirectPower(0);
        }

        if(aRightPower > 0) {
            rightFront.setDirectPower(aRightPower + measuredPidTurningBasePower);
            rightBack.setDirectPower(aRightPower + measuredPidTurningBasePower);
        }
        else if(aRightPower < 0) {
            rightFront.setDirectPower(aRightPower - measuredPidTurningBasePower);
            rightBack.setDirectPower(aRightPower - measuredPidTurningBasePower);
        }
        else {
            rightFront.setDirectPower(0);
            rightBack.setDirectPower(0);
        }
    }

    private void setAccelerationRate(double anAcceleration, double aDeceleration){
        leftFront.setAccelerationRates(anAcceleration, aDeceleration);
        leftBack.setAccelerationRates(anAcceleration, aDeceleration);
        rightFront.setAccelerationRates(anAcceleration, aDeceleration);
        rightBack.setAccelerationRates(anAcceleration, aDeceleration);
    }

   /* public void setMeasuredPidTurningBasePower(double aTurningBasePower) {
        measuredPidTurningBasePower = aTurningBasePower;
    } */

    public void setMinimumMotorPower(double aMinimumPower) {
        leftFront.setMinPower(aMinimumPower);
        leftBack.setMinPower(aMinimumPower);
        rightFront.setMinPower(aMinimumPower);
        rightBack.setMinPower(aMinimumPower);
    }

    public void stop() {
        wheelAccelerationThread.stop();
    }

}