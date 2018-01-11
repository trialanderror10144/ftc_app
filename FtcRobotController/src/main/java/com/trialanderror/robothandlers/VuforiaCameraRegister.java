package com.trialanderror.robothandlers;

import com.qualcomm.ftcrobotcontroller.R;
import com.vuforia.HINT;
import com.vuforia.Vuforia;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import com.trialanderror.fieldhandlers.CryptoKeys;
import static com.trialanderror.fieldhandlers.CryptoKeys.LEFT;
import static com.trialanderror.fieldhandlers.CryptoKeys.CENTER;
import static com.trialanderror.fieldhandlers.CryptoKeys.RIGHT;
import static com.trialanderror.fieldhandlers.CryptoKeys.UNKNOWN;

public class VuforiaCameraRegister {

    private VuforiaTrackables relicTrackables;
    private VuforiaTrackable relicTemplate;
    private VuforiaLocalizer.Parameters params;
    private VuforiaLocalizer vuforia;
    private CryptoKeys snapshot;

    public VuforiaCameraRegister() throws InterruptedException {

        params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        params.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;
        params.vuforiaLicenseKey = "AQRRqHr/////AAAAGa+JA7T4hEKQv9WC0GbPjX+BWYM59ffdN82q1f8GKDlqQkxyPZU0m3ubmLxQpWVimHxRvp6tySvoVvHfX78z1CE0OT78O42K+o5VtWmSPgOMPzrWNYRA94pUCSK8BhhWQC+tUKyWUoNa3AczKNAbBi1NVY73kZYPhCpoTHSEgltEX+EU3W9d/BCW+68iH8kIdEofBcY3TvhMwv+QuE2usZ7wJL+s6jBD8lu+frGZEoBBXNycUetiIBDE6dm2a5Z0wO1e7ccBCe6Oah+pTiHyecohuZAHTKmeF0fpRFlpdzCT1qrQuOJhgajCi8gEZZFnrzaMKRcSNYsNj9epcjhseiRSeY/g+asH8oq8oGcCorE1";
        params.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;

        vuforia = ClassFactory.createVuforiaLocalizer(params);
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 1);

        relicTrackables = vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate");

        relicTrackables.activate();
    }
    public void takeSnapshot() {
        RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
        if (vuMark == RelicRecoveryVuMark.LEFT) {
            snapshot = LEFT;
        } else if (vuMark == RelicRecoveryVuMark.CENTER) {
            snapshot =  CENTER;
        } else if (vuMark == RelicRecoveryVuMark.RIGHT) {
            snapshot = RIGHT;
        } else {
            snapshot = UNKNOWN;
        }
    }

    public CryptoKeys getCryptoKey() {
        return snapshot;
    }
    String format(OpenGLMatrix transformationMatrix) {
        return (transformationMatrix != null) ? transformationMatrix.formatAsTransform() : "null";
    }
}