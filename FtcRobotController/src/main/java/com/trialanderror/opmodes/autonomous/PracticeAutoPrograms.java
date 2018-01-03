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
import static com.trialanderror.fieldhandlers.JewelColors.BLUE_JEWEL;
import static com.trialanderror.fieldhandlers.PositionToWall.LEFT_SQUARE;


//@Autonomous(name = "practice")
public class PracticeAutoPrograms extends OpMode {

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
    private int keyOption;

    public void init() {
        glyphLift = new GlyphLift((hardwareMap));
        jewelKnocker = new JewelKnocker((hardwareMap));
        drivetrain = new Drivetrain((hardwareMap));
        jCSensor = new JewelColorSensor(hardwareMap.colorSensor.get("csensor"), 0x03c);

        try {
            camera = new VuforiaCameraRegister();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        OptionMenu.Builder ParamsBuilder = new OptionMenu.Builder(hardwareMap.appContext);
        SingleSelectCategory teamAllianceColor = new SingleSelectCategory("Alliance Color");
        teamAllianceColor.addOption("Red");
        teamAllianceColor.addOption("Blue");
        ParamsBuilder.addCategory(teamAllianceColor);
        autonomousParamsMenu = ParamsBuilder.create();
        autonomousParamsMenu.show();

        jewelOption = 0;
        stateCurrent = 0;
        keyOption = 0;

        jCSensor.zeroSensorValues();
    }

    public void loop() {

        switch (stateCurrent) {
            case 0:
                if (.02 > getStateRuntime()) {
                    glyphLift.raiseLiftPowerUp();
                }
                glyphLift.closeAuto();
                jCSensor.zeroSensorValues();
                readMenuParameters();
                if(allianceColor == RED_ALLIANCE && position == LEFT_SQUARE) {
                    stateCurrent = 100;
                }
                if(allianceColor == BLUE_ALLIANCE) {
                    stateCurrent = 200;
                }


            case 100:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > 1.0) stateCurrent++;
                break;
            case 101:
                readCamera();
                if (getStateRuntime() > 4.0) stateCurrent++;
                break;
            case 102:
                jCSensor.getJewelColor();
                if (getStateRuntime() > 3.0) stateCurrent++;
                break;
            case 103:
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
            case 104:
                jewelKnocker.changeGoUp();
                drivetrain.setPowerWithoutAcceleration(0,0);
                if (getStateRuntime() > 1.0) {
                    stateCurrent++;
                }
                break;






            case 200:
                jewelKnocker.changeGoDown();
                if (getStateRuntime() > 1.0) stateCurrent++;
                break;

            case 201:
                readCamera();
                if (getStateRuntime() > 4.0) stateCurrent++;
                break;

            case 202:
                jCSensor.getJewelColor();
                if (getStateRuntime() > 1.0) stateCurrent++;
                break;

            case 203:
                CompareAllianceJewel();
                if (jewelOption == 2) {
                    //v drivetrain.
                    drivetrain.setPowerWithoutAcceleration(.2, .2);
                }
                if (jewelOption == 4) {
                    drivetrain.setPowerWithoutAcceleration(-.2, -.2);
                }
                if (getStateRuntime() > .3) {
                    stateCurrent++;
                }
                break;

            case 204:
                jewelKnocker.changeGoUp();
                drivetrain.setPowerWithoutAcceleration(0,0);
                if (getStateRuntime() > 1.0) {
                    stateCurrent++;
                }
                break;


            default:
                drivetrain.stop();
                glyphLift.stop();
                jewelKnocker.changeGoUp();

        }
        telemetry.addData("Alliance", allianceColor);
        telemetry.addData("Jewel Color:", jCSensor.getJewelColor());
        telemetry.addData("Run Time:", getStateRuntime());
        telemetry.addData("Switch Statement: ", stateCurrent);
        telemetry.addData("Jewel Option:", jewelOption);
        telemetry.addData("Crypto Pos:", camera.getCryptoKey());
        telemetry.addData("Key Option:", keyOption);
    }

    public void stop() {
        drivetrain.stop();
        glyphLift.stop();
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
    public void returnStateCamera() {
        if (readCamera() == LEFT) {
            keyOption = 1;
        }
        if (readCamera() == CENTER) {
            keyOption = 2;
        }
        if (readCamera() == RIGHT) {
            keyOption = 3;
        }
        if (readCamera() == UNKNOWN) {
            keyOption = 4;
        }
    }
}
