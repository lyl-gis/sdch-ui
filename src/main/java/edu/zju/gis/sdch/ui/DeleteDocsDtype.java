package edu.zju.gis.sdch.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class DeleteDocsDtype implements Initializable {
    @FXML
    private ChoiceBox<String> cbDtype;

    @FXML
    private Button btnConfirmDelete;

    @FXML
    private Button btnCancelDelete;
    public static IndexManage indexManage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        indexManage = IndexManage.instance;
        Map<String, String> mappingDtype = new HashMap<>();
        mappingDtype.put("保税区", "fe_bsq");
        mappingDtype.put("政区", "fe_zq");
        mappingDtype.put("水系面", "fe_shuixi");
        mappingDtype.put("水系结构线", "fe_shuixijgx");
        mappingDtype.put("文化遗产", "fe_whyc");
        mappingDtype.put("风景名胜区", "fe_fjmsq");
        mappingDtype.put("世界自然文化遗产", "fe_sjzrwhyc");
        mappingDtype.put("省级森林公园", "fe_sjslgy");
        mappingDtype.put("POI", "poi");
        mappingDtype.put("国家级森林公园", "fe_gjjslgy");
        mappingDtype.put("省级开发区", "fe_sjkfq");
        mappingDtype.put("地质公园", "fe_dzgy");
        mappingDtype.put("省级高新区", "fe_sjgxq");
        mappingDtype.put("乡镇面", "fe_xzm");
        mappingDtype.put("国家级开发区", "fe_gjjkfq");
        mappingDtype.put("国家旅游度假区", "fe_gjlydjq");
        mappingDtype.put("国家级高新区", "fe_gjjgxq");
        mappingDtype.put("自然保护区", "fe_zrbhq");
        mappingDtype.put("道路", "fe_road");

        cbDtype.getItems().addAll(mappingDtype.keySet());
        btnConfirmDelete.setOnMouseClicked(event -> {
            String dtype = mappingDtype.get(cbDtype.getValue());
            long number = 0;
            long result = 0;
            for (int k = 0; k < indexManage.indexNames.size(); k++) {
                number = +indexManage.helper.getDtypeDocCount(indexManage.indexNames.get(k), dtype);
                result = +indexManage.helper.deleteDtype(indexManage.indexNames.get(k), dtype);
            }
            new Alert(Alert.AlertType.INFORMATION, "该类别数据共有" + number + "条，成功删除" + result + "条", ButtonType.OK)
                    .showAndWait();
        });
        btnCancelDelete.setOnMouseClicked(evnet -> {
            Stage stage = (Stage) btnCancelDelete.getScene().getWindow();
            stage.close();
        });
    }
}
