package edu.zju.gis.sdch.model;

import java.util.Observable;

public class ProgressObservable extends Observable {
    private double progress;

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
        setChanged();
    }
}
