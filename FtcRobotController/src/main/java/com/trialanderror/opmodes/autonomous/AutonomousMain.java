package com.trialanderror.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.trialanderror.robothandlers.Drivetrain;
import com.trialanderror.robothandlers.GlyphLift;
import com.trialanderror.robothandlers.JewelKnocker;
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

    //Defines Servos
    private JewelKnocker jewelKnocker;

    //Defines all Sensors, Addons, and Encoders (if needed)
    private VuforiaCameraRegister camera;
    private JewelColorSensor jCSensor;
    private PanelRangeSensor pRSensor;

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

    /*public final static double RED_CLOSE_LEFT;
    public final static double RED_CLOSE_CENTER;
    public final static double RED_CLOSE_RIGHT;

    public final static double RED_FAR_LEFT;
    public final static double RED_FAR_CENTER;
    public final static double RED_FAR_RIGHT;

    public final static double BLUE_CLOSE_LEFT;
    public final static double BLUE_CLOSE_CENTER;
    public final static double BLUE_CLOSE_RIGHT;

    public final static double BLUE_FAR_LEFT;
    public final static double BLUE_FAR_CENTER;
    public final static double BLUE_FAR_RIGHT;
    */

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
        pRSensor = new PanelRangeSensor((hardwareMap));

        //Creates Select Menu on Robot Controller
        OptionMenu.Builder ParamsBuilder = new OptionMenu.Builder(hardwareMap.appContext);
        NumberCategory delaySelectMenu = new NumberCategory("Delay"); //ALWAYS IN SECONDS
        SingleSelectCategory teamAllianceColor = new SingleSelectCategory("Alliance Color");
        SingleSelectCategory alliancePosition = new SingleSelectCategory("Position");
        teamAllianceColor.addOption("Red");
        teamAllianceColor.addOption("Blue");
        alliancePosition.addOption("Left");
        alliancePosition.addOption("Right");
        ParamsBuilder.addCategory(teamAllianceColor);
        ParamsBuilder.addCategory(alliancePosition);
        ParamsBuilder.addCategory(delaySelectMenu);
        autonomousParamsMenu = ParamsBuilder.create();
        autonomousParamsMenu.show();

        stateCurrent = 0;

    }


    public void loop() {

        switch (stateCurrent) {

            case 0:
                if(runtime.seconds() > delayTime) stateCurrent++;
            break;

            case 1:
                readMenuParameters();
                if (allianceColor == RED_ALLIANCE && position == LEFT_SQUARE && camera.getCryptoKey() == LEFT) {
                    stateCurrent = 100;
                }
                if (allianceColor == RED_ALLIANCE && position == LEFT_SQUARE && camera.getCryptoKey() == CENTER) {
                    stateCurrent = 200;
                }
                if (allianceColor == RED_ALLIANCE && position == LEFT_SQUARE && camera.getCryptoKey() == RIGHT) {
                    stateCurrent = 300;
                }


                if (allianceColor == RED_ALLIANCE && position == RIGHT_SQUARE && camera.getCryptoKey() == LEFT) {
                    stateCurrent = 400;
                }
                if (allianceColor == RED_ALLIANCE && position == RIGHT_SQUARE && camera.getCryptoKey() == CENTER) {
                    stateCurrent = 500;
                }
                if (allianceColor == RED_ALLIANCE && position == RIGHT_SQUARE && camera.getCryptoKey() == RIGHT) {
                    stateCurrent = 600;
                }


                if (allianceColor == BLUE_ALLIANCE && position == LEFT_SQUARE && camera.getCryptoKey() == LEFT) {
                    stateCurrent = 700;
                }
                if (allianceColor == BLUE_ALLIANCE && position == LEFT_SQUARE && camera.getCryptoKey() == CENTER) {
                    stateCurrent = 800;
                }
                if (allianceColor == BLUE_ALLIANCE && position == LEFT_SQUARE && camera.getCryptoKey() == RIGHT) {
                    stateCurrent = 900;
                }


                if (allianceColor == BLUE_ALLIANCE && position == RIGHT_SQUARE && camera.getCryptoKey() == LEFT) {
                    stateCurrent = 1000;
                }
                if (allianceColor == BLUE_ALLIANCE && position == RIGHT_SQUARE && camera.getCryptoKey() == CENTER) {
                    stateCurrent = 1100;
                }
                if (allianceColor == BLUE_ALLIANCE && position == RIGHT_SQUARE && camera.getCryptoKey() == RIGHT) {
                    stateCurrent = 1200;
                }
                break;

            //If we have stateCurrent values of 100, 200, 300, 400, 500, 600, use values 1 and 2
            //For others, use values 3 and 4


            /* ______    _____    ______
              |   _  |  | ____|  |  ___ |
              |  |_| |  | ___    | |   | |
              |   _  |  | ___|   | |   | |
              |  | | |  | ____   | |___| |
              |__| |_|  |_____|  |______|
            */
            case 100:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > 1.0) stateCurrent++;
                break;

            case 101:
                jCSensor.getJewelColor();
                if (getStateRuntime() > 1.0) stateCurrent++;
                break;

            case 102:
                if (jewelOption == 1) {
                    //v drivetrain.
                    drivetrain.setPowerWithoutAcceleration(-.2, -.2);
                }
                if (jewelOption == 3) {
                    drivetrain.setPowerWithoutAcceleration(.2, .2);
                }

                if (getStateRuntime() > .3) stateCurrent++;
                break;

            case 103:
                    drivetrain.setPowerWithoutAcceleration(0.0 ,0.0);
                    stateCurrent++;
                break;

            case 104:
                drivetrain.setPowerWithoutAcceleration(.3, .3);
                if (stateCurrent > 7.2) {
                    drivetrain.setPowerWithoutAcceleration(0.0, 0.0);
                    stateCurrent++;
                }
                break;
            case 105:
                   // pRSensor.getUltrasonicReading();
                    stateCurrent++;
                break;
            case 106:





            case 200:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > 3.5) stateCurrent++;
                break;
            case 201:
                jCSensor.getJewelColor();
                if (getStateRuntime() > 4.1) stateCurrent++;
                break;










            case 300:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > 3.5) stateCurrent++;
                break;

            case 301:
                jCSensor.getJewelColor();
                if (getStateRuntime() > 4.1) stateCurrent++;
                break;




            case 400:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > 3.5) stateCurrent++;
                break;

            case 401:
                jCSensor.getJewelColor();
                if (getStateRuntime() > 4.1) stateCurrent++;
                break;




            case 500:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > 3.5) stateCurrent++;
                break;

            case 501:
                jCSensor.getJewelColor();
                if (getStateRuntime() > 4.1) stateCurrent++;
                break;



            case 600:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > 3.5) stateCurrent++;
                break;

            case 601:
                jCSensor.getJewelColor();
                if (getStateRuntime() > 4.1) stateCurrent++;
                break;



            case 700:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > 3.5) stateCurrent++;
                break;

            case 701:
                jCSensor.getJewelColor();
                if (getStateRuntime() > 4.1) stateCurrent++;
                break;




            case 800:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > 3.5) stateCurrent++;
                break;

            case 801:
                jCSensor.getJewelColor();
                if (getStateRuntime() > 4.1) stateCurrent++;
                break;




            case 900:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > 3.5) stateCurrent++;
                break;

            case 901:
                jCSensor.getJewelColor();
                if (getStateRuntime() > 4.1) stateCurrent++;
                break;



            case 1000:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > 3.5) stateCurrent++;
                break;

            case 1001:
                jCSensor.getJewelColor();
                if (getStateRuntime() > 4.1) stateCurrent++;
                break;



            case 1100:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > 3.5) stateCurrent++;
                break;

            case 1101:
                jCSensor.getJewelColor();
                if (getStateRuntime() > 4.1) stateCurrent++;
                break;



            case 1200:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > 3.5) stateCurrent++;
                break;

            case 1201:
                jCSensor.getJewelColor();
                if (getStateRuntime() > 4.1) stateCurrent++;
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
        telemetry.addData("Run Time:", getStateRuntime());
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

        try {
            delayTime = Integer.parseInt(autonomousParamsMenu.selectedOption("Delay"));
        } catch (NumberFormatException e) {
            delayTime = 0;
        }

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