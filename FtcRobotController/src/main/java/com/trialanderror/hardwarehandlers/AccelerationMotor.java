package com.trialanderror.hardwarehandlers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.trialanderror.controlhandlers.AccelerationThread;

public class AccelerationMotor {

    private static final double MAX_MOTOR_POWER = 1;
    private static final double MIN_MOTOR_POWER = -1;

    private DcMotor acceleratedMotor;
    private double accelerationRateSpeedPerHardwareTick;
    private double currentPower;
    private double decelerationRateSpeedPerHardwareTick;
    private double maximumPower;
    private double minimumPower;
    private double targetPower;

    public AccelerationMotor(DcMotor aMotor, double anAccelerationRateMotorSpeedPerSecond, double aDecelerationRateMotorSpeedPerSecond,
                             double aMinimumPower, double aMaximumPower) {

        acceleratedMotor = aMotor;
        setAccelerationRates(anAccelerationRateMotorSpeedPerSecond, aDecelerationRateMotorSpeedPerSecond);
        setMinPower(aMinimumPower);
        setMaxPower(aMaximumPower);
    }

    public double getCurrentPower() {
        return acceleratedMotor.getPower();
    }

    public synchronized double getTargetPower() {
        return targetPower;
    }
    public synchronized void setAccelerationRates(double anAccelerationRateMotorSpeedPerSecond, double aDecelerationRateMotorSpeedPerSecond) {
        accelerationRateSpeedPerHardwareTick = anAccelerationRateMotorSpeedPerSecond / (1000 / AccelerationThread.UPDATE_PERIOD_MS);
        decelerationRateSpeedPerHardwareTick = aDecelerationRateMotorSpeedPerSecond / (1000 / AccelerationThread.UPDATE_PERIOD_MS);
    }

    public synchronized void setDirectPower(double aPower) {
        currentPower = aPower;
        setTargetPower(currentPower);

        currentPower = Math.min(currentPower, MAX_MOTOR_POWER);
        currentPower = Math.max(currentPower, MIN_MOTOR_POWER);
        acceleratedMotor.setPower(currentPower);
    }

    public synchronized void setMaxPower (double aMaxPower) {
        maximumPower = aMaxPower;
    }

    public synchronized void setMinPower(double aMinPower) {
        minimumPower = aMinPower;
    }

    public synchronized void setTargetPower(double aTargetPower) {
        targetPower = aTargetPower;
    }

    public void stopMotorHard() {
        acceleratedMotor.setPower(0.0);
        setTargetPower(0.0);
    }

    public synchronized void update() {
        //Robot driving forward.
        if(currentPower > 0) {
            //Accelerating.
            if(currentPower < targetPower) {
                currentPower += accelerationRateSpeedPerHardwareTick;
                currentPower = Math.min(currentPower, Math.min(targetPower, maximumPower));
            }
            //Decelerating.
            else if(currentPower > targetPower) {
                currentPower -= decelerationRateSpeedPerHardwareTick;
                currentPower = Math.max(currentPower, 0);
            }
            //No acceleration; at target power.
            else {
                //Do nothing.
            }
        }
        //Robot driving backward.
        else if(currentPower < 0){
            //Decelerating.
            if(currentPower < targetPower) {
                currentPower += decelerationRateSpeedPerHardwareTick;
                currentPower = Math.min(currentPower, 0);
            }
            //Accelerating.
            else if(currentPower > targetPower) {
                currentPower -= accelerationRateSpeedPerHardwareTick;
                currentPower = Math.max(currentPower, Math.max(targetPower, -maximumPower));
            }
            //No acceleration; at target power.
            else {
                //Do nothing.
            }
        }
        //Robot currently stopped.
        else {
            //Start moving forward.
            if(targetPower > minimumPower) {
                currentPower += Math.max(minimumPower, accelerationRateSpeedPerHardwareTick);
            }
            //Start moving backward.
            else if(targetPower < -minimumPower) {
                currentPower -= Math.max(minimumPower, accelerationRateSpeedPerHardwareTick);
            }
            //No movement; at target power.
            else {
                //Don't move.
            }
        }

        currentPower = Math.min(currentPower, MAX_MOTOR_POWER);
        currentPower = Math.max(currentPower, MIN_MOTOR_POWER);
        acceleratedMotor.setPower(currentPower);
    }

    //All below methods wrap the DcMotor class...
    public Direction getDirection() {
        return acceleratedMotor.getDirection();
    }

    public void setDirection(Direction aDirection) {
        acceleratedMotor.setDirection(aDirection);
    }
}