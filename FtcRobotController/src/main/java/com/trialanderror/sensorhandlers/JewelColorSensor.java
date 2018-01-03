package com.trialanderror.sensorhandlers;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.trialanderror.fieldhandlers.JewelColors;

import static com.trialanderror.fieldhandlers.JewelColors.*;

public class JewelColorSensor {

    private ColorSensor colorSensor;
    private int virtualZeroRed;
    private int virtualZeroBlue;

    public JewelColorSensor(ColorSensor aColorSensor, int i2cAddress) {
        colorSensor = aColorSensor;
        colorSensor.setI2cAddress(I2cAddr.create8bit(i2cAddress));
        colorSensor.enableLed(true);

        virtualZeroRed = 0;
        virtualZeroBlue = 0;
    }
    public int getRed() {
        return colorSensor.red() - virtualZeroRed;
    }
    public int getBlue() {
        return colorSensor.blue() - virtualZeroBlue;
    }
    public void zeroSensorValues() {
        virtualZeroRed = colorSensor.red();
        virtualZeroBlue =  colorSensor.blue();
    }
    public String returnColorValues() {
        String whitespace = " ";
        return colorSensor.red() + whitespace + colorSensor.blue() + whitespace + colorSensor.green();
    }
    public JewelColors getJewelColor() {
        if (getRed() > getBlue()) {
            return RED_JEWEL;
        }
        if (getBlue() > getRed()) {
            return BLUE_JEWEL;
        }
        else {
            return UNKNOWN;
        }
    }

}
