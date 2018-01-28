package com.trialanderror.controlhandlers;

import com.qualcomm.robotcore.util.Range;

public class PIDControl {

    private int readingsAtSetpoint;
    private int marginOfError;
    private static final int SETPOINT_REACHED_READINGS_THRESHOLD = 10;

    public double setpoint;

    private double Kp;
    private double Ki;
    private double Kd;

    public double errorCalc;
    private double integral;
    private double derivative;
    private double timePassed;
    private double pidValues;

    public PIDControl(double aKp, double aKi, double aKd) {
        setPidCoefficients(aKp, aKi, aKd);
        resetValues(0);
        marginOfError = 2;
    }
    // Left Positive = Clockwise
    public double getLeftNewPower(double aPower) {
        return Range.clip(aPower + pidValues, -1, 1);
    }

    public double getRightNewPower(double aPower) {
        return Range.clip(aPower - pidValues, -1, 1);
    }

    public void resetValues(double aTime) {
        timePassed = aTime;
        integral = 0;
        derivative = 0;

    }
    public void setPidCoefficients(double aKp, double aKi, double aKd) {
        Kp = aKp;
        Ki = aKi;
        Kd = aKd;
    }
    public String returnErrorValues() {
        return pidValues + ", " + "P: " + Kp*errorCalc + " I: " + Ki*integral + " D: " +Kd*derivative;
    }

    public void setSetpoint(double aValue) {
        setpoint = aValue;
    }

    public void updatePidValues(double aActual, double aTime) {
        errorCalc = setpoint - aActual;
        if (errorCalc == 0) {
            derivative = 0;
        }
        if (errorCalc != 0) {
            derivative = (errorCalc / (aTime - timePassed)) ;
        }

        pidValues = Kp*errorCalc + Ki*integral + Kd*derivative;

        if(aActual < (setpoint + marginOfError) && aActual > (setpoint - marginOfError)) readingsAtSetpoint++;
        else readingsAtSetpoint = 0;
    }
    public boolean isSetpointReached() {
        return readingsAtSetpoint >= SETPOINT_REACHED_READINGS_THRESHOLD;
    }
    public void setMarginOfError(int a) {
        marginOfError = a;
    }

}