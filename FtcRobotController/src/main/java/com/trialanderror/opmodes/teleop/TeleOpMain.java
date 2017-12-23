package com.trialanderror.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.trialanderror.robothandlers.Drivetrain;
import com.trialanderror.robothandlers.GlyphLift;
import com.trialanderror.robothandlers.JewelKnocker;
import com.trialanderror.robothandlers.RelicGrabber;


@TeleOp(name="TeleOp Main")
public class TeleOpMain extends OpMode {

    /*
    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftBack;
    private DcMotor rightBack;
    */

    private Drivetrain drivetrain;
    private JewelKnocker jewelKnocker;
    private GlyphLift glyphLift;
    private RelicGrabber relicGrabber;

    private static final double STICK_DIGITAL_THRESHOLD = 0.25;
    private static final double DELTA_SERVO = 0.04;
    private static final double TURNING_SCALAR = 0.875;
    private static final double SLOW_DRIVE_SCALAR = .15;
    private static final double MIN_POWER_REG = 0.3;
    private static final double MIN_POWER_SLOW = 0.05;

    //Am I actually gonna use these??? Like seriously, the pro
    private double finalLeft;
    private double finalRight;

    @Override
    public void init() {
        drivetrain = new Drivetrain((hardwareMap));
        jewelKnocker = new JewelKnocker((hardwareMap));
        glyphLift = new GlyphLift((hardwareMap));

    /*  leftFront = hardwareMap.dcMotor.get("leftFront");
        leftBack = hardwareMap.dcMotor.get("leftBack");
        rightFront = hardwareMap.dcMotor.get("rightFront");
        rightBack = hardwareMap.dcMotor.get("rightBack");
        */

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

        if (gamepad1.dpad_down) {
            glyphLift.raiseLiftPowerUp();
        } else if (gamepad1.dpad_up) {
            glyphLift.lowerLiftPowerDown();
        } else {
            glyphLift.stop();
        }

      /*  float leftY = -gamepad1.left_stick_y;
        float rightY = -gamepad1.right_stick_y;
        leftFront.setPower(leftY);
        leftBack.setPower(leftY);
        rightFront.setPower(-rightY);
        rightBack.setPower(-rightY); */

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