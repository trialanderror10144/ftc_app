package com.trialanderror.controlhandlers;

import com.qualcomm.robotcore.util.Range;

/**
 * Created by Esposito's 9-16 on 1/13/2018.
 */

public class PIDControl {

    private double setpoint;

    private double Kp;
    private double Ki;
    private double Kd;
    //For tuning??
    private double Ku;

    public double errorCalc;
    private double integral;
    private double derivative;
    private double timePassed;
    private double pidValues;

    //Is this needed???
    private double proportional;

    public PIDControl(double aKp, double aKi, double aKd) {
        aKp = Kp;
        aKi = Ki;
        aKd = Kd;
        resetValues(0);
    }
    //What would the direction be? Left pos = Clockwise
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

        setpoint = 0;
    }

    public String returnErrorValues() {
        return "P: " + Kp*errorCalc + " I: " + Ki*integral + " D: " +Kd*derivative;
    }

    public void setSetpoint(double aValue) {
        setpoint = aValue;
    }

    public void updatePidValues(double aActual, double aTime) {
        errorCalc = setpoint - aActual;
        timePassed = aTime;
        if (errorCalc == 0) {
            derivative = 0;
        }
        if (errorCalc != 0) {
            derivative = errorCalc / timePassed ;
        }


        pidValues = Kp*errorCalc + Ki*integral + Kd*derivative;

    }
}