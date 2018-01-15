package com.trialanderror.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.trialanderror.robothandlers.Drivetrain;
import com.trialanderror.robothandlers.GlyphLift;
import com.trialanderror.robothandlers.JewelKnocker;
import com.trialanderror.robothandlers.RelicGrabber;
import com.trialanderror.sensorhandlers.LiftTouchSensor;

@TeleOp(name="TeleOp Main")
public class TeleOpMain extends OpMode {

    //Defines Motors and Servos
    private Drivetrain drivetrain;
    private JewelKnocker jewelKnocker;
    private GlyphLift glyphLift;
    private RelicGrabber relicGrabber;

    //Defines Sensors
    private LiftTouchSensor liftTouchSensor;

    //Defines Values For Power and Threshold
    private static final double STICK_DIGITAL_THRESHOLD = 0.25;
    private static final double DELTA_SERVO = 0.004;
    private static final double TURNING_SCALAR = 0.875; //Originally .875
    private static final double SLOW_DRIVE_SCALAR = .2;
    private static final double SLOW_DRIVE_TURNING_SCALAR = 4.0;
    private static final double MIN_POWER_REG = 0.44;
    private static final double MIN_POWER_SLOW = 0.18;

    @Override
    public void init() {

        drivetrain = new Drivetrain((hardwareMap));
        drivetrain.setBrakeModeAuto();
        jewelKnocker = new JewelKnocker((hardwareMap));
        glyphLift = new GlyphLift((hardwareMap));
        relicGrabber = new RelicGrabber((hardwareMap));
        liftTouchSensor = new LiftTouchSensor((hardwareMap));
    }

    @Override
    public void loop() {

        //Glyph Grabbers Code/Control
        if (gamepad1.a) {
            glyphLift.closeAuto();
        }
        if (gamepad1.b) {
            glyphLift.openAuto();
        }
        if (gamepad1.x) {
            glyphLift.midAuto();
        }
        if (gamepad1.y) {
            glyphLift.bOpenAuto();
        }


        //Lift Control, Acts in Correct and NOT OPPOSITE Direction
        if (gamepad1.dpad_down && liftTouchSensor.isLowered()) {
            glyphLift.stop();
        } else if (gamepad1.dpad_up || (gamepad1.dpad_up && liftTouchSensor.isLowered())) {
            glyphLift.raiseLiftPowerUp();
        } else if (gamepad1.dpad_down) {
            glyphLift.lowerLiftPowerDown();
        } else {
            glyphLift.stop();
        }


        //Relic Extender/Motor Control
        if (gamepad2.dpad_up) {
            relicGrabber.horizontalMove();
        } else if (gamepad2.dpad_down) {
            relicGrabber.horizontalRetract();
        } else {
            relicGrabber.noHorizMove();
        }


        //Relic Grabber (Servos) Control
        if (gamepad2.a) {
            relicGrabber.openRelic();
        }
        if (gamepad2.b) {
            relicGrabber.clampRelic();
        }


        //Precision for Clamp
        if (gamepad2.left_bumper) {
            relicGrabber.clampSmallDelta();
        }
        if (gamepad2.right_bumper) {
            relicGrabber.openSmallDelta();
        }


        //x is down, y is up
        if (gamepad2.x) {
            relicGrabber.twistDeltaRelic(DELTA_SERVO);
        }
        if (gamepad2.y) {
            relicGrabber.twistDeltaRelic(-DELTA_SERVO);
        }


        if (slowDrive(gamepad1)) {
            drivetrain.setMinimumMotorPower(0.12);

            if ((-gamepad1.left_stick_y < 0) == (-gamepad1.right_stick_y) < 0)
                drivetrain.setPower(-gamepad1.left_stick_y * SLOW_DRIVE_SCALAR, -gamepad1.right_stick_y * SLOW_DRIVE_SCALAR);
            else
                drivetrain.setPower(-gamepad1.left_stick_y * SLOW_DRIVE_SCALAR * SLOW_DRIVE_TURNING_SCALAR, -gamepad1.right_stick_y * SLOW_DRIVE_SCALAR * SLOW_DRIVE_TURNING_SCALAR);
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
        glyphLift.stop();
        relicGrabber.noHorizMove();
        glyphLift.openAuto();
    }

    private boolean slowDrive(Gamepad aGamepad) {
        return (aGamepad.left_trigger > STICK_DIGITAL_THRESHOLD || aGamepad.right_trigger > STICK_DIGITAL_THRESHOLD);
    }
}