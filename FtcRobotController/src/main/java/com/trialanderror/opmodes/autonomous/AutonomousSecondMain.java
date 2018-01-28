package com.trialanderror.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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
@Autonomous(name = "Auto: Main")
public class AutonomousSecondMain extends OpMode {

    //Creates dropdown menu for Robot Controller
    private OptionMenu autonomousParamsMenu;

    private GlyphLift glyphLift;
    private Drivetrain drivetrain;
    private RelicGrabber relicGrabber;
    private JewelKnocker jewelKnocker;
    private VuforiaCameraRegister camera;
    private JewelColorSensor jCSensor;
    private PanelRangeSensor backUltra;
    private PanelRangeSensor frontUltra;
    private GyroTurnSensor gyroSensor;
    //private GyroTurnSensor otherGyroSensor;
    private PIDControl gyroPID;
    //private GoodPIDControlTBD goodGyroPID;
    private final static double PID_GYRO_TURN_VALUES[] = new double[]{0.0018, 0.0, 0.0005};
    //.0018, 0.0, 0.0005

    private Alliances allianceColor;
    private PositionToWall position;
    private int jewelOption;
    private CryptoKeys glyphOption;

    //Main Variables for Switch Statements and Runtime
    private int delayTime;
    private int stateCurrent;

    //Variables for Runtime
    private ElapsedTime runtime = new ElapsedTime();
    private double lastReadRuntime;
    private int lastReadRunState;


    //List of Values for Optical Distance Sensor (Ultrasonic)

    //100 Use Back/Top Sensor
    public final static double RED_LEFTS_LEFT = 128.0;
    public final static double RED_LEFTS_CENTER = 109.0;
    public final static double RED_LEFTS_RIGHT = 90.0;


    //200
    public final static double RED_RIGHTS_LEFT = 35.0;
    public final static double RED_RIGHTS_CENTER = 54.0;
    public final static double RED_RIGHTS_RIGHT = 73.0;

    //300
    public final static double BLUE_LEFTS_LEFT = 37.0;
    public final static double BLUE_LEFTS_CENTER = 56.0;
    public final static double BLUE_LEFTS_RIGHT = 75.0;

    //400
    public final static double BLUE_RIGHTS_LEFT = 120.0;
    public final static double BLUE_RIGHTS_CENTER = 133.0;
    public final static double BLUE_RIGHTS_RIGHT = 147.0;


    private static final double TURN_MAX_DURATION = 6;

    public final static double PROPORTIONAL_GYRO_SCALAR = 0.0025;
    private double driveGyroCorrection;

    public void init() {
        try {
            camera = new VuforiaCameraRegister();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        glyphLift = new GlyphLift((hardwareMap));
        jewelKnocker = new JewelKnocker((hardwareMap));
        drivetrain = new Drivetrain((hardwareMap));
        drivetrain.setBrakeModeAuto();
        jCSensor = new JewelColorSensor(hardwareMap.colorSensor.get("csensor"), 0x3c);
        frontUltra = new PanelRangeSensor((hardwareMap.i2cDevice.get("rsensorback")), 0x28);
        backUltra = new PanelRangeSensor((hardwareMap.i2cDevice.get("rsensorfront")), 0x10);
        relicGrabber = new RelicGrabber((hardwareMap));
        gyroSensor = new GyroTurnSensor(hardwareMap.gyroSensor.get("gyro"));
        gyroPID = new PIDControl(PID_GYRO_TURN_VALUES[0], PID_GYRO_TURN_VALUES[1], PID_GYRO_TURN_VALUES[2]);

        //Creates Select Menu on Robot Controller
        OptionMenu.Builder ParamsBuilder = new OptionMenu.Builder(hardwareMap.appContext);
        //NumberCategory delaySelectMenu = new NumberCategory("Delay"); //ALWAYS IN SECONDS
        SingleSelectCategory teamAllianceColor = new SingleSelectCategory("Alliance Color");
        SingleSelectCategory alliancePosition = new SingleSelectCategory("Position");
        teamAllianceColor.addOption("Red");
        teamAllianceColor.addOption("Blue");
        alliancePosition.addOption("Left");
        alliancePosition.addOption("Right");
        ParamsBuilder.addCategory(teamAllianceColor);
        ParamsBuilder.addCategory(alliancePosition);
        //ParamsBuilder.addCategory(delaySelectMenu);
        autonomousParamsMenu = ParamsBuilder.create();
        autonomousParamsMenu.show();

        stateCurrent = 0;

    }
    public void loop() {
        switch (stateCurrent) {
            case 0:
                gyroSensor.resetGyro();
                glyphLift.closeAuto();
                drivetrain.resetEncoders();
                if (getStateRuntime() > .75) { stateCurrent++;}
                break;

            case 1:
                jCSensor.zeroSensorValues();
                readMenuParameters();
                if (allianceColor == RED_ALLIANCE && position == LEFT_SQUARE) {
                    stateCurrent = 100;
                }
                if (allianceColor == RED_ALLIANCE && position == RIGHT_SQUARE) {
                    stateCurrent = 200;
                }
                if (allianceColor == BLUE_ALLIANCE && position == LEFT_SQUARE) {
                    stateCurrent = 300;
                }
                if (allianceColor == BLUE_ALLIANCE && position == RIGHT_SQUARE) {
                    stateCurrent = 400;
                }
                break;

            //If we have stateCurrent values of 100, 200, use values 1 and 3
            //For 300 and 400, use values 2 and 4, and FLIP MOTOR DIRECTIONS
            //100, 200, and 300 all use the backUltra Sensor, 400 uses frontUltra


            case 100:
                camera.takeSnapshot();
                if (getStateRuntime() > 1.5) {stateCurrent++;}
                break;

            case 101:
                if (camera.getCryptoKey() == LEFT) {
                    glyphOption = LEFT;
                }
                if (camera.getCryptoKey() == CENTER || camera.getCryptoKey() == UNKNOWN) {
                    glyphOption = CENTER;
                }
                if (camera.getCryptoKey() == RIGHT) {
                    glyphOption = RIGHT;
                }
                stateCurrent++;
                break;

            case 102:
                if (getStateRuntime() > 0.2) {
                    glyphLift.stop();
                }
                else {
                    glyphLift.raiseLiftPowerUp();
                }
                if (getStateRuntime() > .5) stateCurrent++;
                break;

            case 103:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > .9) stateCurrent++;
                break;

            case 104:
                jCSensor.getJewelColor();
                if (getStateRuntime() > .8) stateCurrent++;
                break;

            case 105:
                CompareAllianceJewel();
                if (jewelOption == 1) {
                    jewelKnocker.hitLeft();
                    stateCurrent++;
                }
                else if (jewelOption == 3) {
                    jewelKnocker.hitRight();
                    stateCurrent++;
                }
                else {
                    stateCurrent++;
                }
                break;

            case 106:
                jewelKnocker.resetPos();
                if (getStateRuntime() > .5) {
                    jewelKnocker.changeGoUp();
                }
                if (getStateRuntime() > 1) {
                    stateCurrent++;
                }
                break;

            case 107:
                if (drivetrain.getEncodersMagnitude() >= 610) {
                    drivetrain.setPowerWithoutAcceleration(0,0);
                    gyroSensor.resetGyro();
                    //otherGyroSensor.resetGyro();
                    stateCurrent++;
                } else {
                    drivetrain.setPowerWithoutAcceleration(.08,.08);
                }
                break;

            case 108:
                if (glyphOption == LEFT) {
                    if (backUltra.getUltrasonicReading() >= RED_LEFTS_LEFT) {
                        drivetrain.setPowerWithoutAcceleration(0, 0);
                        gyroSensor.resetGyro();
                        stateCurrent++;
                    } else {
                        driveGyroCorrection = PROPORTIONAL_GYRO_SCALAR * gyroSensor.headingGyro();
                        drivetrain.setPowerWithoutAcceleration(.08 - driveGyroCorrection, .08 + driveGyroCorrection);
                    }
                }

                if (glyphOption == CENTER) {
                    if (backUltra.getUltrasonicReading() >= RED_LEFTS_CENTER) {
                        drivetrain.setPowerWithoutAcceleration(0, 0);
                        gyroSensor.resetGyro();
                        stateCurrent++;
                    } else {
                        driveGyroCorrection = PROPORTIONAL_GYRO_SCALAR * gyroSensor.headingGyro();
                        drivetrain.setPowerWithoutAcceleration(.08 - driveGyroCorrection, .08 + driveGyroCorrection);
                    }
                }

                if (glyphOption == RIGHT) {
                    if (backUltra.getUltrasonicReading() >= RED_LEFTS_RIGHT) {
                        drivetrain.setPowerWithoutAcceleration(0, 0);
                        gyroSensor.resetGyro();;
                        stateCurrent++;
                    } else {
                        driveGyroCorrection = PROPORTIONAL_GYRO_SCALAR * gyroSensor.headingGyro();
                        drivetrain.setPowerWithoutAcceleration(.08 - driveGyroCorrection, .08 + driveGyroCorrection);
                    }
                }
                break;

            case 109:
                if (getStateRuntime() > 1) { stateCurrent++; }
                break;

            case 110:
                gyroSensor.calibrateGyro();
                stateCurrent++;
                break;

            case 111:
                gyroPID.resetValues(getRuntime());
                gyroPID.setSetpoint(84);
                gyroPID.setMarginOfError(2);
                if (!gyroSensor.isCalibrating() && getStateRuntime() > 2) { stateCurrent++; }
                break;

            case 112:
                drivetrain.setPowerPidCorrection(gyroPID.getLeftNewPower(0),
                        gyroPID.getRightNewPower(0));
                gyroPID.updatePidValues(gyroSensor.headingGyro(), getRuntime());

                if(gyroPID.isSetpointReached() || getStateRuntime() > 6) stateCurrent++;
                break;

            case 113:
                drivetrain.brake();
                if (getStateRuntime() > 1) {
                    stateCurrent++;
                }
                break;

            case 114:
                drivetrain.setPowerWithoutAcceleration(.1,.1);
                if (getStateRuntime() > 1.5) {
                    stateCurrent++;
                }
                break;

            case 115:
                glyphLift.midAuto();
                drivetrain.setPowerWithoutAcceleration(-.16, -.16);
                if (getStateRuntime() > .15) {
                    drivetrain.setPowerWithoutAcceleration(0,0);
                    stateCurrent++;
                }
                break;

            case 116:
                drivetrain.setPowerWithoutAcceleration(.15,.15);
                if (getStateRuntime() > 1.2) {
                    drivetrain.setPowerWithoutAcceleration(0,0);
                    stateCurrent++;
                }
                break;

            case 117:
                drivetrain.setPowerWithoutAcceleration(-.16,-.16);
                if (getStateRuntime() > .25) {
                    drivetrain.setPowerWithoutAcceleration(0,0);
                    stateCurrent++;
                }
                break;



            case 200:
                if (getStateRuntime() > 0.2) {
                    glyphLift.stop();
                }
                else {
                    glyphLift.raiseLiftPowerUp();
                }
                if (getStateRuntime() > 1) stateCurrent++;
                break;

            case 201:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > .9) stateCurrent++;
                break;

            case 202:
                jCSensor.getJewelColor();
                if (getStateRuntime() > .8) stateCurrent++;

            case 203:
                CompareAllianceJewel();
                if (jewelOption == 1) {
                    jewelKnocker.hitLeft();
                    stateCurrent++;
                }
                else if (jewelOption == 3) {
                    jewelKnocker.hitRight();
                    stateCurrent++;
                }
                else {
                    stateCurrent++;
                }
                break;

            case 204:
                jewelKnocker.resetPos();
                if (getStateRuntime() > .5) {
                    jewelKnocker.changeGoUp();
                }
                if (getStateRuntime() > 1) {
                    stateCurrent++;
                }
                break;

            case 205:
                drivetrain.setPowerWithoutAcceleration(.08,.08);
                if (drivetrain.getEncodersMagnitude() > 610) {
                    drivetrain.setPowerWithoutAcceleration(0,0);
                    stateCurrent++;
                }
                break;

            case 206:
                if (glyphOption == LEFT) {
                    gyroPID.resetValues(getRuntime());
                    gyroPID.setSetpoint(05);
                    stateCurrent++;
                }

                if (glyphOption == CENTER) {
                    gyroPID.resetValues(getRuntime());
                    gyroPID.setSetpoint(20);
                    stateCurrent++;
                }

                if (glyphOption == RIGHT) {
                    gyroPID.resetValues(getRuntime());
                    gyroPID.setSetpoint(44);
                    stateCurrent++;
                }
                break;




            case 300:
                if (getStateRuntime() > 0.2) {
                    glyphLift.stop();
                }
                else {
                    glyphLift.raiseLiftPowerUp();
                }
                if (getStateRuntime() > 1) stateCurrent++;
                break;

            case 301:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > .9) stateCurrent++;
                break;

            case 302:
                jCSensor.getJewelColor();
                if (getStateRuntime() > .8) stateCurrent++;

            case 303:
                CompareAllianceJewel();
                if (jewelOption == 2) {
                    jewelKnocker.hitRight();
                    stateCurrent++;
                }
                else if (jewelOption == 4) {
                    jewelKnocker.hitLeft();
                    stateCurrent++;
                }
                else {
                    stateCurrent++;
                }
                break;

            case 304:
                jewelKnocker.resetPos();
                if (getStateRuntime() > .5) {
                    jewelKnocker.changeGoUp();
                }
                if (getStateRuntime() > 1) {
                    stateCurrent++;
                }
                break;


            case 305:
                drivetrain.setPowerWithoutAcceleration(-.08,-.08);
                if (drivetrain.getEncodersMagnitude() > 850) {
                    drivetrain.setPowerWithoutAcceleration(0,0);
                    stateCurrent++;
                }
                break;





            case 400:
                camera.takeSnapshot();
                if (getStateRuntime() > 2) {stateCurrent++;}
                break;

            case 401:
                if (camera.getCryptoKey() == LEFT) {
                    glyphOption = LEFT;
                }
                if (camera.getCryptoKey() == CENTER || camera.getCryptoKey() == UNKNOWN) {
                    glyphOption = CENTER;
                }
                if (camera.getCryptoKey() == RIGHT) {
                    glyphOption = RIGHT;
                }
                stateCurrent++;
                break;

            case 402:
                if (getStateRuntime() > 0.2) {
                    glyphLift.stop();
                }
                else {
                    glyphLift.raiseLiftPowerUp();
                }
                if (getStateRuntime() > .5) stateCurrent++;
                break;

            case 403:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > .9) stateCurrent++;
                break;

            case 404:
                jCSensor.getJewelColor();
                if (getStateRuntime() > .8) stateCurrent++;
                break;

            case 405:
                CompareAllianceJewel();
                if (jewelOption == 2) {
                    jewelKnocker.hitRight();
                    stateCurrent++;
                }
                else if (jewelOption == 4) {
                    jewelKnocker.hitLeft();
                    stateCurrent++;
                }
                else {
                    stateCurrent++;
                }
                break;

            case 406:
                jewelKnocker.resetPos();
                if (getStateRuntime() > .5) {
                    jewelKnocker.changeGoUp();
                }
                if (getStateRuntime() > 1) {
                    stateCurrent++;
                }
                break;

            case 407:
                if (drivetrain.getEncodersMagnitude() >= 610) {
                    drivetrain.setPowerWithoutAcceleration(0,0);
                    gyroSensor.resetGyro();
                    stateCurrent++;
                } else {
                    drivetrain.setPowerWithoutAcceleration(-.08,-.08);
                }
                break;

            case 408:
                if (glyphOption == LEFT) {
                    if (frontUltra.getUltrasonicReading() >= BLUE_RIGHTS_LEFT) {
                        drivetrain.setPowerWithoutAcceleration(0, 0);
                        gyroSensor.resetGyro();
                        stateCurrent++;
                    } else {
                        driveGyroCorrection = PROPORTIONAL_GYRO_SCALAR * gyroSensor.headingGyro();
                        drivetrain.setPowerWithoutAcceleration(-.08 + driveGyroCorrection, -.08 - driveGyroCorrection);
                    }
                }

                if (glyphOption == CENTER) {
                    if (frontUltra.getUltrasonicReading() >= BLUE_RIGHTS_CENTER) {
                        drivetrain.setPowerWithoutAcceleration(0, 0);
                        gyroSensor.resetGyro();
                        stateCurrent++;
                    } else {
                        driveGyroCorrection = PROPORTIONAL_GYRO_SCALAR * gyroSensor.headingGyro();
                        drivetrain.setPowerWithoutAcceleration(-.08 + driveGyroCorrection, -.08 - driveGyroCorrection);
                    }
                }

                if (glyphOption == RIGHT) {
                    if (frontUltra.getUltrasonicReading() >= BLUE_RIGHTS_RIGHT) {
                        drivetrain.setPowerWithoutAcceleration(0, 0);
                        gyroSensor.resetGyro();
                        stateCurrent++;
                    } else {
                        driveGyroCorrection = PROPORTIONAL_GYRO_SCALAR * gyroSensor.headingGyro();
                        drivetrain.setPowerWithoutAcceleration(-.08 + driveGyroCorrection, -.08 - driveGyroCorrection);
                    }
                }
                break;

            case 409:
                if (getStateRuntime() > 1) { stateCurrent++; }
                break;

            case 410:
                gyroSensor.calibrateGyro();
                stateCurrent++;
                break;

            case 411:
                gyroPID.resetValues(getRuntime());
                gyroPID.setSetpoint(84);
                gyroPID.setMarginOfError(2);
                if (!gyroSensor.isCalibrating() && getStateRuntime() > 2) { stateCurrent++; }
                break;

            case 412:
                drivetrain.setPowerPidCorrection(gyroPID.getLeftNewPower(0),
                        gyroPID.getRightNewPower(0));
                gyroPID.updatePidValues(gyroSensor.headingGyro(), getRuntime());

                if(gyroPID.isSetpointReached() || getStateRuntime() > 6) stateCurrent++;
                break;

            case 413:
                drivetrain.brake();
                if (getStateRuntime() > 1) {
                    stateCurrent++;
                }
                break;

            case 414:
                drivetrain.setPowerWithoutAcceleration(.1,.1);
                if (getStateRuntime() > 1.5) {
                    stateCurrent++;
                }
                break;

            case 415:
                glyphLift.midAuto();
                drivetrain.setPowerWithoutAcceleration(-.16, -.16);
                if (getStateRuntime() > .15) {
                    drivetrain.setPowerWithoutAcceleration(0,0);
                    stateCurrent++;
                }
                break;

            case 416:
                drivetrain.setPowerWithoutAcceleration(.15,.15);
                if (getStateRuntime() > 1.2) {
                    drivetrain.setPowerWithoutAcceleration(0,0);
                    stateCurrent++;
                }
                break;

            case 417:
                drivetrain.setPowerWithoutAcceleration(-.16,-.16);
                if (getStateRuntime() > .25) {
                    drivetrain.setPowerWithoutAcceleration(0,0);
                    stateCurrent++;
                }
                break;





            default:
                drivetrain.stop();
                glyphLift.stop();
                relicGrabber.noHorizMove();
        }
        //telemetry.addData("Alliance", allianceColor);
        //telemetry.addData("Position (Wall):", position);
        telemetry.addData("Switch Statement: ", stateCurrent);
        telemetry.addData("Crypto Pos:", camera.getCryptoKey());
        //telemetry.addData("Jewel Color:", jCSensor.getJewelColor());
        //telemetry.addData("Jewel Option:", jewelOption);
        telemetry.addData("Front Range (CM):", frontUltra.getUltrasonicReading());
        telemetry.addData("Back Range (CM):", backUltra.getUltrasonicReading());
        telemetry.addData("Gyro Heading:", gyroSensor.headingGyro());
        //telemetry.addData("Good Gyro(?) Heading:", otherGyroSensor.headingGyro());
        //telemetry.addData("Encoder Distance:", drivetrain.getEncodersMagnitude());
        telemetry.addData("PID E. Values:", gyroPID.returnErrorValues());
    }
    @Override
    public void stop() {
        drivetrain.stop();
        glyphLift.stop();
        relicGrabber.noHorizMove();
        jewelKnocker.initServoPos();
    }
    private double getStateRuntime() {
        if(lastReadRunState != stateCurrent) {
            lastReadRuntime = runtime.seconds();
            lastReadRunState = stateCurrent;
        }
        return runtime.seconds() - lastReadRuntime;
    }
    public void readMenuParameters() {
        if (autonomousParamsMenu.selectedOption("Alliance Color").equals("Red")) {
            allianceColor = RED_ALLIANCE;
        }
        if (autonomousParamsMenu.selectedOption("Alliance Color").equals("Blue")) {
            allianceColor = BLUE_ALLIANCE;
        }
        if (autonomousParamsMenu.selectedOption("Position").equals("Left")) {
            position = LEFT_SQUARE;
        }
        if (autonomousParamsMenu.selectedOption("Position").equals("Right")) {
            position = RIGHT_SQUARE;
        }

      /*  try {
            delayTime = Integer.parseInt(autonomousParamsMenu.selectedOption("Delay"));
        } catch (NumberFormatException e) {
            delayTime = 0;
        } */

    }
    public void CompareAllianceJewel() {
        if (jCSensor.getJewelColor() == RED_JEWEL && allianceColor == RED_ALLIANCE) {
            jewelOption = 1;
        }
        if (jCSensor.getJewelColor() == RED_JEWEL && allianceColor == BLUE_ALLIANCE) {
            jewelOption = 2;
        }
        if (jCSensor.getJewelColor() == BLUE_JEWEL && allianceColor == RED_ALLIANCE) {
            jewelOption = 3;
        }
        if (jCSensor.getJewelColor() == BLUE_JEWEL && allianceColor == BLUE_ALLIANCE) {
            jewelOption = 4;
        }
    }
}
