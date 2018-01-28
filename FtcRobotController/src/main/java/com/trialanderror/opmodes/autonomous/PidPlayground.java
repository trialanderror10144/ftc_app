package com.trialanderror.opmodes.autonomous;

import android.content.Context;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.trialanderror.controlhandlers.PIDControl;
import com.trialanderror.hardwarehandlers.AndroidPhoneUtil;
import com.trialanderror.robothandlers.Drivetrain;
import com.trialanderror.sensorhandlers.GyroTurnSensor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.trialanderror.fieldhandlers.Alliances.BLUE_ALLIANCE;
import static com.trialanderror.fieldhandlers.Alliances.RED_ALLIANCE;

@TeleOp(name="PID Value Testing")
public class PidPlayground extends OpMode {

    private static final boolean LOGGING_ENABLED =  false;

    private double currentDeadband; //Item 0
    private double currentP; //Item 1
    private double currentI; //Item 2
    private double currentD; //Item 3
    private Drivetrain drivetrain;
    private GyroTurnSensor gyroscope;
    private PIDControl pidCalculator;
    private boolean previousDpadDown;
    private boolean previousDpadUp;
    private ElapsedTime runtime = new ElapsedTime();
    private int selectedParameter;
    private Writer stream;
    private String streamCurrentLine;

    @Override
    public void init() {
        currentDeadband = 0.0;
        currentP = 0.00;
        currentI = 0.0;
        //currentD = 0.15 * currentP;
        currentD = 0.000;
        selectedParameter = 0;

        drivetrain = new Drivetrain(hardwareMap);
        gyroscope = new GyroTurnSensor(hardwareMap.gyroSensor.get("gyro"));
        pidCalculator = new PIDControl(currentP, currentI, currentD);

        if(LOGGING_ENABLED) {
            FileWriter writer = null;
            String fileName = AndroidPhoneUtil.getDCIMDirectory() + File.separator + "pid_" + System.nanoTime() + ".csv";
            File emptyFile = new File(fileName);
            try {
                emptyFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Cannot create file " + fileName, e);
            }
            try {
                writer = new FileWriter(fileName, true);
            } catch (IOException e) {
                throw new RuntimeException("Cannot write file " + fileName, e);
            }
            stream = new BufferedWriter(writer);
        }
    }

    @Override
    public void loop() {
        if(gamepad1.b) gyroscope.resetGyro();

        if(gamepad1.dpad_up && !previousDpadUp) selectedParameter--;
        if(gamepad1.dpad_down && !previousDpadDown) selectedParameter++;

        if(gamepad1.dpad_right) {
            if(selectedParameter == 0) currentDeadband += 0.0001;
            if(selectedParameter == 1) currentP += 0.00001;
            if(selectedParameter == 2) currentI += 0.00001;
            if(selectedParameter == 3) currentD += 0.00001;
        }
        if(gamepad1.dpad_left) {
            if(selectedParameter == 0) currentDeadband -= 0.0001;
            if(selectedParameter == 1) currentP -= 0.00001;
            if(selectedParameter == 2) currentI -= 0.00001;
            if(selectedParameter == 3) currentD -= 0.00001;
        }
        if(gamepad1.b) {
            if(selectedParameter == 0) currentDeadband = 0;
            if(selectedParameter == 1) currentP = 0;
            if(selectedParameter == 2) currentI = 0;
            if(selectedParameter == 3) currentD = 0;
        }

        if(!gamepad1.a) {
            drivetrain.brake();
            drivetrain.setMeasuredPidTurningBasePower(currentDeadband);

            pidCalculator.resetValues(runtime.seconds());
            pidCalculator.setMarginOfError(1);
            pidCalculator.setPidCoefficients(currentP, currentI, currentD);
            pidCalculator.setSetpoint(0);
        }
        else {
            if(!pidCalculator.isSetpointReached()) drivetrain.setPowerPidCorrection(pidCalculator.getLeftNewPower(0),
                    pidCalculator.getRightNewPower(0));
            else drivetrain.brake();
            pidCalculator.updatePidValues(gyroscope.headingGyro(), getRuntime());

            if (LOGGING_ENABLED) {
                try {
                    streamCurrentLine = ((int) runtime.milliseconds()) + "," + gyroscope.headingGyro() +
                            "," + drivetrain.getPowerLeft() + "," + drivetrain.getPowerRight() +
                            "," + pidCalculator.returnErrorValues() + "\n";
                    stream.write(streamCurrentLine);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        telemetry.addData("Gyro Heading", gyroscope.headingGyro());
        if(selectedParameter == 0) telemetry.addData("Deadband", currentDeadband + "***");
        else telemetry.addData("Deadband", currentDeadband + "");
        if(selectedParameter == 1) telemetry.addData("P", currentP + "***");
        else telemetry.addData("P", currentP + "");
        if(selectedParameter == 2) telemetry.addData("I", currentI + "***");
        else telemetry.addData("I", currentI + "");
        if(selectedParameter == 3) telemetry.addData("D", currentD + "***");
        else telemetry.addData("D", currentD + "");

        previousDpadDown = gamepad1.dpad_down;
        previousDpadUp = gamepad1.dpad_up;
    }

    @Override
    public void stop() {
        drivetrain.stop();
        if(LOGGING_ENABLED) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}