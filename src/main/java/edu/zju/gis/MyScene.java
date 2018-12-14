package edu.zju.gis;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/11/18
 */
public class MyScene implements Initializable {
    public Button btnSay;
    public TextField btnTextField;

    public void btnSayClick(MouseEvent mouseEvent) {
        btnTextField.setText("hello, world!");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
