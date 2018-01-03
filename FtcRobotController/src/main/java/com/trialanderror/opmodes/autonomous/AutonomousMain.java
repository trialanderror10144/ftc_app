package com.trialanderror.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.trialanderror.robothandlers.Drivetrain;
import com.trialanderror.robothandlers.GlyphLift;
import com.trialanderror.robothandlers.JewelKnocker;
import com.trialanderror.robothandlers.RelicGrabber;
import com.trialanderror.robothandlers.VuforiaCameraRegister;

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


@Autonomous(name = "Auto: Main")
public class AutonomousMain extends OpMode {


    //Creates dropdown menu for Robot Controller
    private OptionMenu autonomousParamsMenu;

    //Defines DCMotor Parts
    private GlyphLift glyphLift;
    private Drivetrain drivetrain;
    private RelicGrabber relicGrabber;

    //Defines Servos
    private JewelKnocker jewelKnocker;

    //Defines all Sensors, Addons, and Encoders (if needed)
    private VuforiaCameraRegister camera;
    private JewelColorSensor jCSensor;
    private PanelRangeSensor backUltra;
    private PanelRangeSensor frontUltra;

    //Defines Enumeration
    private Alliances allianceColor;
    private PositionToWall position;
    public CryptoKeys keyPosition;

    //Main Variables for Switch Statements and Runtime
    private int delayTime;
    private int stateCurrent;

    //Variables for Runtime
    private ElapsedTime runtime = new ElapsedTime();
    private double lastReadRuntime;
    private int lastReadRunState;

    private int jewelOption;

    //List of Values for Optical Distance Sensor (Ultrasonic)

    //100
    public final static double RED_LEFTS_LEFT = 91.0;
    public final static double RED_LEFTS_CENTER = 107.0;
    public final static double RED_LEFTS_RIGHT = 123.0;

    /*

    //200
    public final static double RED_RIGHTS_LEFT;
    public final static double RED_RIGHTS_CENTER;
    public final static double RED_RIGHTS_RIGHT;

    //300
    public final static double BLUE_LEFTS_LEFT;
    public final static double BLUE_LEFTS_CENTER;
    public final static double BLUE_LEFTS_RIGHT;
*/

    //The opposite of the Red Left Distances, since its flipped ????? NOT TRUE

    //400
    public final static double BLUE_RIGHTS_LEFT = 123.0;
    public final static double BLUE_RIGHTS_CENTER = 107.0;
    public final static double BLUE_RIGHTS_RIGHT = 91.0;


    public void init() {
        try {
            camera = new VuforiaCameraRegister();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        glyphLift = new GlyphLift((hardwareMap));
        jewelKnocker = new JewelKnocker((hardwareMap));
        drivetrain = new Drivetrain((hardwareMap));
        jCSensor = new JewelColorSensor(hardwareMap.colorSensor.get("csensor"), 0x3c);
        frontUltra = new PanelRangeSensor((hardwareMap.i2cDevice.get("rsensorback")), 0x28);
        backUltra = new PanelRangeSensor((hardwareMap.i2cDevice.get("rsensorfront")), 0x10);
        relicGrabber = new RelicGrabber((hardwareMap));

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
                glyphLift.closeAuto();
                drivetrain.stop();
               if (getStateRuntime() > .9) { stateCurrent++;}
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
                //For 300 and 400, use values 2 and 4



            case 100:
                jewelKnocker.changeGoDown();
                glyphLift.raiseLiftPowerUp();
                if (getStateRuntime() > 0.2) {
                    glyphLift.stop();
                }
                if (getStateRuntime() > 0.9) stateCurrent++;
                break;

            case 101:
                jCSensor.getJewelColor();
                if (getStateRuntime() > 2.1) stateCurrent++;
                break;

            case 102:
                CompareAllianceJewel();
                if (jewelOption == 1) {
                    //v drivetrain.
                    drivetrain.setPowerWithoutAcceleration(-.2, -.2);
                }
                if (jewelOption == 3) {
                    drivetrain.setPowerWithoutAcceleration(.2, .2);
                }
                if (getStateRuntime() > .14) {
                    drivetrain.setPowerWithoutAcceleration(0, 0);
                    drivetrain.stop();
                    stateCurrent++;
                }
                break;

            case 103:
                jewelKnocker.changeGoUp();
                if (getStateRuntime() > .5) {
                    stateCurrent++;
                }
                break;

            case 104:
                drivetrain.setPowerWithoutAcceleration(.26, .26);

                if (jewelOption == 1) {
                    if (getStateRuntime() > 1.1) {
                        drivetrain.setPowerWithoutAcceleration(0, 0);
                        drivetrain.stop();
                        stateCurrent++;
                    }
                }
                if (jewelOption == 3) {
                    if (getStateRuntime() > .32) {
                        drivetrain.setPowerWithoutAcceleration(0, 0);
                        drivetrain.stop();
                        stateCurrent++;
                    }
                }
                else {
                    if (getStateRuntime() > .39) {
                        drivetrain.setPowerWithoutAcceleration(0, 0);
                        drivetrain.stop();
                        stateCurrent++;
                    }
                }
                break;

            case 105:
                backUltra.getUltrasonicReading();
                stateCurrent++;
                break;

            case 106:
                drivetrain.setPowerWithoutAcceleration(.1,.1);
                if (readCamera() == LEFT && backUltra.getUltrasonicReading() >= RED_LEFTS_LEFT) {
                    drivetrain.setPowerWithoutAcceleration(0, 0);
                    stateCurrent++;
                }
                if (readCamera() == CENTER && backUltra.getUltrasonicReading() >= RED_LEFTS_CENTER) {
                    drivetrain.setPowerWithoutAcceleration(0, 0);
                    stateCurrent++;
                }
                if (readCamera() == RIGHT && backUltra.getUltrasonicReading() >= RED_LEFTS_RIGHT) {
                    drivetrain.setPowerWithoutAcceleration(0, 0);
                    stateCurrent++;
                }
                if (readCamera() == UNKNOWN && backUltra.getUltrasonicReading() >= RED_LEFTS_LEFT) {
                    drivetrain.setPowerWithoutAcceleration(0, 0);
                    stateCurrent++;
                }
                break;

            case 107:
                drivetrain.setPowerWithoutAcceleration(.65,-.65);
                if (getStateRuntime() > .64) {
                    stateCurrent++;
                }
                break;

            case 108:
                drivetrain.setPowerWithoutAcceleration(0,0);
                stateCurrent++;
                break;

            case 109:
                drivetrain.setPowerWithoutAcceleration(.1,.1);
                if (getStateRuntime()> 1.5) {
                    stateCurrent++;
                }
                break;

            case 110:
                glyphLift.midAuto();
                drivetrain.setPowerWithoutAcceleration(-.16, -.16);
                if (getStateRuntime() > .9) { stateCurrent++; }
                break;

            case 111:
                drivetrain.setPowerWithoutAcceleration(0,0);
                break;









            case 200:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > 0.9) stateCurrent++;
                break;
            case 201:
                readCamera();
                jCSensor.getJewelColor();
                if (getStateRuntime() > 4.0) stateCurrent++;
                break;
            case 202:
                CompareAllianceJewel();
                if (jewelOption == 1) {
                    //v drivetrain.
                    drivetrain.setPowerWithoutAcceleration(-.2, -.2);
                }
                if (jewelOption == 3) {
                    drivetrain.setPowerWithoutAcceleration(.2, .2);
                }
                if (getStateRuntime() > .3) {
                    stateCurrent++;
                }
                break;
            case 203:
                jewelKnocker.changeGoUp();
                drivetrain.setPowerWithoutAcceleration(0, 0);
                if (getStateRuntime() > 1.0) {
                    stateCurrent++;
                }
                break;

            case 204:
                drivetrain.setPowerWithoutAcceleration(-.1, -.1);
                if (getStateRuntime() > 1.1) {
                    stateCurrent++;
                    drivetrain.setPowerWithoutAcceleration(0, 0);
                }
                break;
            case 205:
                backUltra.getUltrasonicReading();
                stateCurrent++;
                break;








            case 300:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > 0.9) stateCurrent++;
                break;
            case 301:
                readCamera();
                jCSensor.getJewelColor();
                if (getStateRuntime() > 4.0) stateCurrent++;
                break;
            case 302:
                CompareAllianceJewel();
                if (jewelOption == 2) {
                    //v drivetrain.
                    drivetrain.setPowerWithoutAcceleration(-.2, -.2);
                }
                if (jewelOption == 4) {
                    drivetrain.setPowerWithoutAcceleration(.2, .2);
                }
                if (getStateRuntime() > .3) {
                    stateCurrent++;
                }
                break;
            case 303:
                jewelKnocker.changeGoUp();
                drivetrain.setPowerWithoutAcceleration(0, 0);
                if (getStateRuntime() > 1.0) {
                    stateCurrent++;
                }
                break;

            case 304:
                drivetrain.setPowerWithoutAcceleration(.1, .1);
                if (getStateRuntime() > 1.1) {
                    stateCurrent++;
                    drivetrain.setPowerWithoutAcceleration(.1, .1);
                }
                break;
            case 305:
                backUltra.getUltrasonicReading();
                stateCurrent++;
                break;











            case 400:
                jewelKnocker.changeGoDown();
                glyphLift.raiseLiftPowerUp();
                if (getStateRuntime() > 0.2) {
                    glyphLift.stop();
                }
                if (getStateRuntime() > 0.9) stateCurrent++;
                break;

            case 401:
                jCSensor.getJewelColor();
                if (getStateRuntime() > 2.1) stateCurrent++;
                break;

            case 402:
                CompareAllianceJewel();
                if (jewelOption == 2) {
                    //v drivetrain.
                    drivetrain.setPowerWithoutAcceleration(.2, .2);
                }
                if (jewelOption == 4) {
                    drivetrain.setPowerWithoutAcceleration(-.2, -.2);
                }
                if (getStateRuntime() > .14) {
                    drivetrain.setPowerWithoutAcceleration(0, 0);
                    drivetrain.stop();
                    stateCurrent++;
                }
                break;

            case 403:
                jewelKnocker.changeGoUp();
                if (getStateRuntime() > .5) {
                    stateCurrent++;
                }
                break;

            case 404:
                drivetrain.setPowerWithoutAcceleration(-.26, -.26);
                if (jewelOption == 2) {
                    if (getStateRuntime() > 1.1) {
                        drivetrain.setPowerWithoutAcceleration(0, 0);
                        drivetrain.stop();
                        stateCurrent++;
                    }
                }
                if (jewelOption == 4) {
                    if (getStateRuntime() > .32) {
                        drivetrain.setPowerWithoutAcceleration(0, 0);
                        drivetrain.stop();
                        stateCurrent++;
                    }
                }
                else {
                    if (getStateRuntime() > .39) {
                        drivetrain.setPowerWithoutAcceleration(0, 0);
                        drivetrain.stop();
                        stateCurrent++;
                    }
                }

                break;

            case 405:
                frontUltra.getUltrasonicReading();
                stateCurrent++;
                break;

            case 406:
                drivetrain.setPowerWithoutAcceleration(.1,.1);
                if (readCamera() == LEFT && frontUltra.getUltrasonicReading() >= BLUE_RIGHTS_LEFT) {
                    drivetrain.setPowerWithoutAcceleration(0, 0);
                    stateCurrent++;
                }
                if (readCamera() == CENTER && frontUltra.getUltrasonicReading() >= BLUE_RIGHTS_CENTER) {
                    drivetrain.setPowerWithoutAcceleration(0, 0);
                    stateCurrent++;
                }
                if (readCamera() == RIGHT && frontUltra.getUltrasonicReading() >= BLUE_RIGHTS_RIGHT) {
                    drivetrain.setPowerWithoutAcceleration(0, 0);
                    stateCurrent++;
                }
                if (readCamera() == UNKNOWN && frontUltra.getUltrasonicReading() >= BLUE_RIGHTS_RIGHT) {
                    drivetrain.setPowerWithoutAcceleration(0, 0);
                    stateCurrent++;
                }
                break;

            case 407:
                drivetrain.setPowerWithoutAcceleration(.65,-.65);
                if (getStateRuntime() > .64) {
                    stateCurrent++;
                }
                break;

            case 408:
                drivetrain.setPowerWithoutAcceleration(0,0);
                stateCurrent++;
                break;

            case 409:
                drivetrain.setPowerWithoutAcceleration(.1,.1);
                if (getStateRuntime()> 1.5) {
                    stateCurrent++;
                }
                break;

            case 410:
                glyphLift.midAuto();
                drivetrain.setPowerWithoutAcceleration(-.15, -.15);
                if (getStateRuntime() > .9) { stateCurrent++; }
                break;

            case 411:
                drivetrain.setPowerWithoutAcceleration(0,0);
                break;





            default:
                //Fucking Done!!! #EatMyAssGary
                drivetrain.stop();
                glyphLift.stop();

        }
        telemetry.addData("Alliance", allianceColor);
        telemetry.addData("Position (Wall):", position);
        telemetry.addData("Switch Statement: ", stateCurrent);
        telemetry.addData("Crypto Pos:", camera.getCryptoKey());
        telemetry.addData("Jewel Color:", jCSensor.getJewelColor());
        telemetry.addData("Jewel Option:", jewelOption);
        telemetry.addData("Front Range (CM):", frontUltra.getUltrasonicReading());
        telemetry.addData("Back Range (CM):", backUltra.getUltrasonicReading());
    }
    @Override
    public void stop() {
        drivetrain.stop();
        glyphLift.stop();
        relicGrabber.noHorizMove();
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
    public CryptoKeys readCamera() {
        return camera.getCryptoKey();
    }
}