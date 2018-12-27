package edu.zju.gis.sdch.model;

import lombok.Getter;

import java.util.Observable;
import java.util.Observer;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/12/27
 */
@Getter
public class ProgressObserver implements Observer {
    private double progress;

    @Override
    public void update(Observable o, Object arg) {
        progress = (double) arg;
    }
}
