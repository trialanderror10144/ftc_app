package com.trialanderror;

/**
 * Created by Esposito's 9-16 on 1/28/2018.
 */

public class TurnCorrectionCalc {

    private int wantedValue;
    private int actualValueNeeded;

    public TurnCorrectionCalc() {

    }
    public int correctionCalc(int aWantedValue) {
        wantedValue = aWantedValue;

        if (wantedValue == 90) {
            return actualValueNeeded = 84;
        } else {
            return actualValueNeeded = (int) wantedValue;
        }
    }
}
