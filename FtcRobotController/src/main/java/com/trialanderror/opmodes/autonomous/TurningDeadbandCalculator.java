package com.trialanderror.opmodes.autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.trialanderror.robothandlers.Drivetrain;
import com.trialanderror.sensorhandlers.GyroTurnSensor;

@TeleOp(name="Util: Turning Deadband Calculator", group="Util")
public class TurningDeadbandCalculator extends OpMode {

    private static final double MOTOR_CHANGE_PER_ITERATION = 0.01;

    private double currentMotorPower;
    private int deadandPassings;
    private Drivetrain drivetrain;
    private GyroTurnSensor gyroscope;
    private boolean isDeadbandFound;
    private double measuredDeadband;
    private ElapsedTime runtime = new ElapsedTime();
    private int speedIncrementStateMachineFlow;

    @Override
    public void init() {
        currentMotorPower = 0.0;
        deadandPassings = 0;
        drivetrain = new Drivetrain(hardwareMap);
        gyroscope = new GyroTurnSensor(hardwareMap.gyroSensor.get("gyro"));
        isDeadbandFound = false;
        measuredDeadband = 0.0;
        speedIncrementStateMachineFlow = 0;

        drivetrain.setMinimumMotorPower(0.0);
    }

    @Override
    public void init_loop() {
        drivetrain.resetEncoders();
    }

    @Override
    public void loop() {

        if(!isDeadbandFound) {
            switch(speedIncrementStateMachineFlow) {
                case 0:
                    runtime.reset();
                    speedIncrementStateMachineFlow++;
                    break;
                case 1:
                    if(runtime.seconds() > 0.05) speedIncrementStateMachineFlow++;
                    break;
                case 2:
                    currentMotorPower += MOTOR_CHANGE_PER_ITERATION;
                    speedIncrementStateMachineFlow = 0;
                    break;
            }

            if(drivetrain.getEncodersMagnitude() > 12) {
                //measuredDeadband = currentMotorPower + 0.02;
                measuredDeadband = currentMotorPower;
                isDeadbandFound = true;
            }
            drivetrain.setPowerWithoutAcceleration(-currentMotorPower, currentMotorPower);
        }

        else {
            if(gamepad1.a) drivetrain.setPowerWithoutAcceleration(-measuredDeadband, measuredDeadband);
            else drivetrain.brake();
        }

        telemetry.addData("Current Power", currentMotorPower);
        telemetry.addData("Current Encoders", drivetrain.getEncodersMagnitude());
        telemetry.addData("Measured Deadband", measuredDeadband);

    }

    @Override
    public void stop() {
        drivetrain.stop();
    }

}