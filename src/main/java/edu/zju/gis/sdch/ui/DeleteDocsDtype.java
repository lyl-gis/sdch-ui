package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.mapper.IndexTypeMapper;
import edu.zju.gis.sdch.model.IndexType;
import edu.zju.gis.sdch.util.MyBatisUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class DeleteDocsDtype implements Initializable {
    @FXML
    private ChoiceBox<String> cbDtype;

    @FXML
    private Button btnConfirmDelete;


    public static IndexManage indexManage;
    public IndexTypeMapper mapper;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        indexManage = IndexManage.instance;
        mapper = MyBatisUtil.getMapper(IndexTypeMapper.class);
        List<IndexType> listIndexType = new ArrayList<>();

        listIndexType.addAll(mapper.selectByIndice(indexManage.indexNames));

        List<String> indexType = new ArrayList<>();
        for (int j = 0; j < listIndexType.size(); j++) {
            indexType.add(listIndexType.get(j).getDtype());
        }
        //indexType2是去除indexType中重复值之后的结果
        List<String> indexType2 = new ArrayList<>();
        for (int i = 0; i < indexType.size(); i++) {
            if (!indexType2.contains(indexType.get(i)))
                indexType2.add(indexType.get(i));
        }
        Collections.sort(indexType2);
        cbDtype.getItems().addAll(indexType2);
        btnConfirmDelete.setOnMouseClicked(event -> {
            String dtype = cbDtype.getValue();
            long number = 0;
            long result = 0;
            //在数据库中删除该类别数据
            String id = "";
            for (int m = 0; m < listIndexType.size(); m++) {
                if (listIndexType.get(m).getDtype().equals(dtype))
                    id = listIndexType.get(m).getId();
                mapper.deleteByPrimaryKey(id);
            }
            //在ES中删除该类别数据
            number = +indexManage.helper.getDtypeDocCount(indexManage.indexNames, dtype);
            result = +indexManage.helper.deleteDtype(indexManage.indexNames, dtype);
            //列表中删除该类别
            cbDtype.getItems().remove(dtype);
            new Alert(Alert.AlertType.INFORMATION, "该类别数据共有" + number + "条，成功删除" + result + "条", ButtonType.OK)
                    .showAndWait();
        });

    }
}
