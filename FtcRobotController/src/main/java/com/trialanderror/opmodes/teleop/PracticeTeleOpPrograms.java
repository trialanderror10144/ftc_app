package com.trialanderror.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.trialanderror.robothandlers.Drivetrain;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.trialanderror.robothandlers.GlyphLift;
import com.trialanderror.robothandlers.JewelKnocker;
import com.trialanderror.sensorhandlers.MotorTouchSensor;

/**
 * Created by Esposito's 9-16 on 11/22/2017.
 */

@Disabled
@TeleOp(name="TeleOp Practice")
public class PracticeTeleOpPrograms extends OpMode {

    //Defines Motors and Servos
    private Drivetrain drivetrain;
    private JewelKnocker jewelKnocker;
    private GlyphLift glyphLift;

    //Defines Sensors
    private MotorTouchSensor motorTouchSensor;

    //Defines Values For Power and Threshold
    private static final double STICK_DIGITAL_THRESHOLD = 0.25;
    private static final double DELTA_SERVO = 0.04;
    private static final double TURNING_SCALAR = 0.875;
    private static final double SLOW_DRIVE_SCALAR = .15;
    private static final double MIN_POWER_REG = 0.35;
    //private static final double MIN_POWER_SLOW = 0.05;

    @Override
    public void init() {
        drivetrain = new Drivetrain((hardwareMap));
        jewelKnocker = new JewelKnocker((hardwareMap));
        glyphLift = new GlyphLift((hardwareMap));

        //motorTouchSensor = new MotorTouchSensor((hardwareMap));

        jewelKnocker.initServoPos();
    }
    @Override
    public void loop() {

        if (gamepad1.a) {
            glyphLift.closeAuto();
            // glyphLift.leftChangePos(DELTA_SERVO);
            // glyphLift.rightChangePos(-DELTA_SERVO);
        }
        if (gamepad1.b) {
            glyphLift.openAuto();
            //glyphLift.leftChangePos(-DELTA_SERVO);
            //glyphLift.rightChangePos(DELTA_SERVO);
        }
        if (gamepad1.x) {
            glyphLift.midAuto();
        }
        if (gamepad1.y) {
            glyphLift.bOpenAuto();
        }

        //Motor Acts in Reverse of Program, JUST DEAL WITH IT
        if (gamepad1.dpad_down && motorTouchSensor.isLowered()) {
            glyphLift.stop();
        } else if (gamepad1.dpad_up || (gamepad1.dpad_up && motorTouchSensor.isLowered())) {
            glyphLift.raiseLiftPowerUp();
        } else if (gamepad1.dpad_down) {
            glyphLift.lowerLiftPowerDown();
        } else {
            glyphLift.stop();
        }


        if (slowDrive(gamepad1)) {
            drivetrain.setMinimumMotorPower(0.10);

            if ((-gamepad1.left_stick_y < 0) == (-gamepad1.right_stick_y) < 0)
                drivetrain.setPower(-gamepad1.left_stick_y * SLOW_DRIVE_SCALAR, -gamepad1.right_stick_y * SLOW_DRIVE_SCALAR);
            else
                drivetrain.setPower(-gamepad1.left_stick_y * SLOW_DRIVE_SCALAR * TURNING_SCALAR, -gamepad1.right_stick_y * SLOW_DRIVE_SCALAR * TURNING_SCALAR);
        }
        else {
                drivetrain.setMinimumMotorPower(MIN_POWER_REG);

        if ((-gamepad1.left_stick_y < 0) == (-gamepad1.right_stick_y) < 0)
            drivetrain.setPower(-gamepad1.left_stick_y, -gamepad1.right_stick_y);
        else
            drivetrain.setPower(-gamepad1.left_stick_y * TURNING_SCALAR, -gamepad1.right_stick_y * TURNING_SCALAR);
    }

    }
    @Override
    public void stop(){
        drivetrain.stop();

    }

    private boolean slowDrive(Gamepad aGamepad) {
        return (aGamepad.left_trigger > STICK_DIGITAL_THRESHOLD || aGamepad.right_trigger > STICK_DIGITAL_THRESHOLD);
    }

}