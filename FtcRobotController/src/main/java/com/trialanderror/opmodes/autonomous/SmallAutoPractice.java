package com.trialanderror.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.trialanderror.sensorhandlers.PanelRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

/**
 * Created by Esposito's 9-16 on 12/14/2017.
 */

@Autonomous(name = "RangeDistanceTest")
public class SmallAutoPractice extends OpMode{

    private PanelRangeSensor pRSensor;


    public void init() {
        pRSensor = new PanelRangeSensor(hardwareMap);

    }
    public void loop() {
        pRSensor.getUltrasonicReading();


        telemetry.addData("Ultrasonic Distance (CM):", pRSensor.getUltrasonicReading());
    }

}
