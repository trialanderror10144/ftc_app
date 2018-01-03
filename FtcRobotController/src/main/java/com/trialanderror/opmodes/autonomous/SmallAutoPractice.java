package com.trialanderror.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.trialanderror.sensorhandlers.PanelRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous(name = "RangeDistanceTest")
public class SmallAutoPractice extends OpMode{

    private PanelRangeSensor backUltra;
    private PanelRangeSensor frontUltra;

    public void init() {
        frontUltra = new PanelRangeSensor((hardwareMap.i2cDevice.get("rsensorback")), 0x28);
        backUltra = new PanelRangeSensor((hardwareMap.i2cDevice.get("rsensorfront")), 0x10);
    }
    public void loop() {
        telemetry.addData("Front Range (CM):", frontUltra.getUltrasonicReading());
        telemetry.addData("Back Range (CM):", backUltra.getUltrasonicReading());
    }
}
