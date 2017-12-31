package com.trialanderror.sensorhandlers;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;

public class PanelRangeSensor {

    private int lastReadDistance;
    private byte[] sensorCache;
    private I2cDevice rSensor;
    private I2cDeviceSynch sensorReader;

    public PanelRangeSensor(I2cDevice hardwareMapI2cDevice, int i2cAddress) {
        lastReadDistance = 0;
        rSensor = hardwareMapI2cDevice;
        sensorReader = new I2cDeviceSynchImpl(rSensor, I2cAddr.create8bit(i2cAddress), false);
        sensorReader.engage();
    }
    public double getUltrasonicReading() {
        sensorCache = sensorReader.read(0x04, 1);
        if((sensorCache[0] & 0xFF) != 255 && (sensorCache[0] & 0xFF) != 0) lastReadDistance = sensorCache[0] & 0xFF;
        return lastReadDistance;

    }
}
