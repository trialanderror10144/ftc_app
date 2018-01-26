package com.trialanderror.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.trialanderror.controlhandlers.PIDControl;
import com.trialanderror.robothandlers.Drivetrain;
import com.trialanderror.robothandlers.GlyphLift;
import com.trialanderror.robothandlers.JewelKnocker;
import com.trialanderror.robothandlers.RelicGrabber;
import com.trialanderror.robothandlers.VuforiaCameraRegister;

import com.trialanderror.sensorhandlers.GyroTurnSensor;
import com.trialanderror.sensorhandlers.JewelColorSensor;


import com.trialanderror.sensorhandlers.PanelRangeSensor;
import com.trialanderror.viewhandlers.NumberCategory;
import com.trialanderror.viewhandlers.OptionMenu;
import com.trialanderror.viewhandlers.SingleSelectCategory;

import com.trialanderror.fieldhandlers.Alliances;
import com.trialanderror.fieldhandlers.PositionToWall;
import com.trialanderror.fieldhandlers.CryptoKeys;


import static com.trialanderror.fieldhandlers.Alliances.BLUE_ALLIANCE;
import static com.trialanderror.fieldhandlers.Alliances.RED_ALLIANCE;

import static com.trialanderror.fieldhandlers.CryptoKeys.CENTER;
import static com.trialanderror.fieldhandlers.CryptoKeys.LEFT;
import static com.trialanderror.fieldhandlers.CryptoKeys.RIGHT;
import static com.trialanderror.fieldhandlers.CryptoKeys.UNKNOWN;
import static com.trialanderror.fieldhandlers.JewelColors.RED_JEWEL;
import static com.trialanderror.fieldhandlers.JewelColors.BLUE_JEWEL;;
import static com.trialanderror.fieldhandlers.PositionToWall.LEFT_SQUARE;
import static com.trialanderror.fieldhandlers.PositionToWall.RIGHT_SQUARE;

@Autonomous(name = "Auto: Practice Main")
public class AutonomousMain extends OpMode {

    private Drivetrain drivetrain;
    private GyroTurnSensor gyroSensor;

    private int stateCurrent;

    //Variables for Runtime
    private ElapsedTime runtime = new ElapsedTime();
    private double lastReadRuntime;
    private int lastReadRunState;

    public void init() {
        drivetrain = new Drivetrain((hardwareMap));
        gyroSensor = new GyroTurnSensor(hardwareMap.gyroSensor.get("gyro"));
        drivetrain.resetEncoders();
        stateCurrent = 0;

    }



    public void loop() {
        switch (stateCurrent) {

            case 0:
                if (drivetrain.getEncodersMagnitude() >= 1000) {
                    drivetrain.setPowerWithoutAcceleration(0,0);
                    stateCurrent++;
                } else {
                    drivetrain.setPowerWithoutAcceleration(.08,.08);
                }
                break;
            case 1:
                gyroSensor.resetGyro();
                drivetrain.resetEncoders();
                if (getStateRuntime() > 5.1) {
                    stateCurrent++;
                }
                break;
            case 2:
                if (gyroSensor.headingGyro() <= 75) {
                    drivetrain.setPowerWithoutAcceleration(.65,-.65);
                }
                else {
                    drivetrain.setPowerWithoutAcceleration(0,0);
                    stateCurrent++;
                }
                break;

            default:
                drivetrain.stop();
        }
        telemetry.addData("Gyro Heading:", gyroSensor.headingGyro());
        telemetry.addData("Encoder:", drivetrain.getEncodersMagnitude());
        telemetry.addData("Encoder Left: ", drivetrain.getEncoderLeft());
        telemetry.addData("Encoder Right: ", drivetrain.getEncoderRight());
    }
        @Override
        public void stop () {
            drivetrain.stop();

    }

    private double getStateRuntime() {
        if(lastReadRunState != stateCurrent) {
            lastReadRuntime = runtime.seconds();
            lastReadRunState = stateCurrent;
        }
        return runtime.seconds() - lastReadRuntime;
    }
}