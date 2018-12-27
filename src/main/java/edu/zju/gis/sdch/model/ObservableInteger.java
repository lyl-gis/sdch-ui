package edu.zju.gis.sdch.model;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ObservableInteger implements ObservableValue<Integer> {
    private static final Object DEFAULT_BEAN = null;
    private static final String DEFAULT_NAME = "";

//    private final Object bean;
//    private final String name;

    /**
     * The constructor of {@code IntegerProperty}
     */
//    public ObservableInteger() {
//        this(DEFAULT_BEAN, DEFAULT_NAME);
//    }

    /**
     * The constructor of {@code IntegerProperty}
     * <p>
     * //     * @param initialValue the initial value of the wrapped value
     */
//    public ObservableInteger(int initialValue) {
//        this(DEFAULT_BEAN, DEFAULT_NAME, initialValue);
//    }

//    /**
//     * The constructor of {@code IntegerProperty}
//     *
//     * @param bean the bean of this {@code IntegerProperty}
//     * @param name the name of this {@code IntegerProperty}
//     */
//    public ObservableInteger(Object bean, String name) {
//        this.bean = bean;
//        this.name = (name == null) ? DEFAULT_NAME : name;
//    }
    @Override
    public void addListener(ChangeListener<? super Integer> listener) {

    }

    @Override
    public void removeListener(ChangeListener<? super Integer> listener) {

    }

    @Override
    public Integer getValue() {
        return null;
    }

    @Override
    public void addListener(InvalidationListener listener) {

    }

    @Override
    public void removeListener(InvalidationListener listener) {

    }
}
