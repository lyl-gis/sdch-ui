package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.config.CommonSetting;
import edu.zju.gis.sdch.model.Index;
import edu.zju.gis.sdch.service.IndexService;
import edu.zju.gis.sdch.util.Contants;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import java.io.IOException;
import java.net.URL;
import java.util.*;


public class DocManage implements Initializable {
    public IndexService indexService;

    @FXML
    private Button btnSelectDoc;


    @FXML
    private Button btnAddDoc;

    @FXML
    private Button btnDeleteDoc;
    @FXML
    private Button btnModifyDoc;
    @FXML
    private Button btnConfirmDeleteDocs;
    @FXML
    private Button btnStartModifiedDocs;
    @FXML
    public TableView<Map<String, Object>> tvDocs;
    @FXML
    private Button btnTest;
    public static final String TITLE = "文档编辑";
    static public List<Map<String, Object>> result;
    public static DocManage instance = null;
    public static int modifyNumber;//记录有多少个要修改的文档
    public static ArrayList<Integer> modifyItems;//记录要修改在表格中是第几行
    @FXML
    private TextField tfWord;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        IndexManage indexManage = IndexManage.instance;
        this.indexService = indexManage.indexService;
        btnStartModifiedDocs.setDisable(true);
        btnConfirmDeleteDocs.setDisable(true);
        //用查到的索引名称初始下拉框中的值，初始化成功的条件是上个窗口中查找索引
        List<Index> indexList = indexManage.mapper.selectAll();

//        btnSelectDoc.setOnMouseClicked(event -> {
//            System.out.println(cbSelectedIndex.getValue());
//            if (cbSelectedIndex.getValue().equals(" ")) {
//                new Alert(Alert.AlertType.INFORMATION, "请先选择索引名称", ButtonType.OK)
//                        .showAndWait();
//            }
//            try {
//                result = indexManage.helper.getAsMap(cbSelectedIndex.getValue(), "_doc", 0, 20);
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            List<TableColumn<Map<String, Object>, String>> tableColumns = new ArrayList<>();
//            //将查到的数据显示在表格中
//            //result的size是一共有多少行，result中的每条记录中是个map，map中key的数量是一共有多少列
//            Map<String, Object> map = result.get(0);
//            for (String string : map.keySet()) {
//                TableColumn<Map<String, Object>, String> column = new TableColumn<>(string);
//                tableColumns.add(column);
//                column.setCellFactory(TextFieldTableCell.forTableColumn());
//                column.setCellValueFactory(param -> {
//                    String value = "";
//                    for (String string2 : param.getValue().keySet())
//                        if (string.equals(string2)) {
//                            value = param.getValue().get(string2).toString();
//                        }
//                    return new SimpleStringProperty(value);
//                });
//            }
//            tvDocs.getColumns().clear();
//            tvDocs.getColumns().addAll(tableColumns);
//            tvDocs.getItems().clear();
//            for (int i = 0; i < result.size(); i++)
//                tvDocs.getItems().add(result.get(i));
//        });
        btnSelectDoc.setOnMouseClicked(event -> {
            String[] indexNames = indexManage.indexService.getIndexNames();
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            String words = tfWord.getText();
            words = QueryParser.escape(words);
            String[] analysisFields = indexManage.indexService.getAnalyzable(indexNames);
            BoolQueryBuilder f = QueryBuilders.boolQuery();
            f.should(QueryBuilders.multiMatchQuery(words, analysisFields).analyzer("ik_max_word"))
                    .should(QueryBuilders.matchQuery(Contants.ADDRESS, words));
            query.must(f);
            SearchRequestBuilder request = null;
            request = indexManage.helper.getClient().prepareSearch().setIndices(indexNames).setQuery(query);
            SearchResponse response = request.get();
            SearchHits hits = response.getHits();
            result = new ArrayList<>();//即为查询到的结果，显示到表格中
            for (SearchHit hit : hits) {
                Map<String, Object> item = hit.getSourceAsMap();
                result.add(item);
            }
            List<TableColumn<Map<String, Object>, String>> tableColumns = new ArrayList<>();
            //将查到的数据显示在表格中
            //result的size是一共有多少行，result中的每条记录中是个map，map中key的数量是一共有多少列
            Map<String, Object> map = result.get(0);
            for (String string : map.keySet()) {
                TableColumn<Map<String, Object>, String> column = new TableColumn<>(string);
                tableColumns.add(column);
                column.setCellFactory(TextFieldTableCell.forTableColumn());
                column.setCellValueFactory(param -> {
                    String value = param.getValue().getOrDefault(string, "").toString();
                    return new SimpleStringProperty(value);
                });
            }
            tvDocs.getColumns().clear();
            tvDocs.getColumns().addAll(tableColumns);
            tvDocs.getItems().clear();
            for (int i = 0; i < result.size(); i++)
                tvDocs.getItems().add(result.get(i));
        });
        btnAddDoc.setOnMouseClicked(event -> {
            new Alert(Alert.AlertType.INFORMATION, "请务必填写the_shape的值", ButtonType.OK)
                    .showAndWait();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("DocAdd.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle(DocAdd.TITLE);
            stage.setScene(scene);
            stage.show();
        });
        btnDeleteDoc.setOnMouseClicked(event -> {
            TableColumn<Map<String, Object>, Boolean> tcDeletedDoc = new TableColumn<>("是否删除");
            for (int i = 0; i < result.size(); i++) {
                result.get(i).put("是否删除", new Docs());
            }
            Map<String, Object> map = result.get(0);
            tcDeletedDoc.setCellFactory(CheckBoxTableCell.forTableColumn(tcDeletedDoc));
            tcDeletedDoc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, Object>, Boolean>, ObservableValue<Boolean>>() {
                Docs deleteddocs = new Docs();

                @Override
                public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Map<String, Object>, Boolean> param) {
                    deleteddocs = (Docs) param.getValue().get("是否删除");
                    return deleteddocs.getIfDeleted();
                }
            });
            tvDocs.getColumns().add(tcDeletedDoc);//在表中增加一列
            new Alert(Alert.AlertType.INFORMATION, "在要删除的内容后打钩", ButtonType.OK)
                    .showAndWait();
            btnConfirmDeleteDocs.setDisable(false);
        });
        btnConfirmDeleteDocs.setOnMouseClicked(event -> {
            for (int i = 0; i < tvDocs.getItems().size(); i++) {
                if (((Docs) tvDocs.getItems().get(i).get("是否删除")).getIfDeleted().getValue()) {
                    //在ES中删除
                    Boolean bool = indexManage.helper.delete("sdmap", "_doc", tvDocs.getItems().get(i).get("lsid").toString());
                    System.out.println("是否删除" + bool);
                    tvDocs.getItems().remove(i);//在表中删除打钩的那一行
                    i--;
                }
            }
            tvDocs.getColumns().remove(tvDocs.getColumns().size() - 1);//删去新增的一列
            btnConfirmDeleteDocs.setDisable(true);
        });
        btnModifyDoc.setOnMouseClicked(event -> {
            TableColumn<Map<String, Object>, Boolean> tcModifiedDoc = new TableColumn<>("是否修改");
            for (int i = 0; i < result.size(); i++) {
                result.get(i).put("是否修改", new Docs());
            }
            Map<String, Object> map = result.get(0);
            tcModifiedDoc.setCellFactory(CheckBoxTableCell.forTableColumn(tcModifiedDoc));
            tcModifiedDoc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, Object>, Boolean>, ObservableValue<Boolean>>() {
                Docs modifidDocs = new Docs();

                @Override
                public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Map<String, Object>, Boolean> param) {
                    modifidDocs = (Docs) param.getValue().get("是否修改");
                    return modifidDocs.ifModified;
                }
            });
            tvDocs.getColumns().add(tcModifiedDoc);//在表中增加一列
            new Alert(Alert.AlertType.INFORMATION, "在要编辑的内容后打钩", ButtonType.OK)
                    .showAndWait();
            btnStartModifiedDocs.setDisable(false);
        });
        btnStartModifiedDocs.setOnMouseClicked(event -> {
            modifyNumber = 0;
            modifyItems = new ArrayList<>();
            Map<String, Map<String, Object>> kvIdDoc = new HashMap<>();
            for (int i = 0; i < tvDocs.getItems().size(); i++) {
                if (((Docs) tvDocs.getItems().get(i).get("是否修改")).getIfModified().getValue()) {
                    modifyNumber++;
                    modifyItems.add(i);
                }
            }
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("DocModify.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
            tvDocs.getColumns().remove(tvDocs.getColumns().size() - 1);//删去新增的一列
            btnStartModifiedDocs.setDisable(true);
        });
//        btnSeniorSelect.setOnAction(event -> {
//            String[] indexNames = indexManage.indexService.getIndexNames();
//            BoolQueryBuilder query = QueryBuilders.boolQuery();
//            String words = tfWord.getText();
//            words = QueryParser.escape(words);
//            String[] analysisFields = indexManage.indexService.getAnalyzable(indexNames);
//            BoolQueryBuilder f = QueryBuilders.boolQuery();
//            f.should(QueryBuilders.multiMatchQuery(words, analysisFields).analyzer("ik_max_word"))
//                    .should(QueryBuilders.matchQuery(Contants.ADDRESS, words));
//            query.must(f);
//            SearchRequestBuilder request = null;
//            request = indexManage.helper.getClient().prepareSearch().setIndices(indexNames).setQuery(query);
//            SearchResponse response = request.get();
//            SearchHits hits = response.getHits();
//            List<Map<String, Object>> selectResult = new ArrayList<>();//即为查询到的结果，显示到表格中
//            for (SearchHit hit : hits) {
//                Map<String, Object> item = hit.getSourceAsMap();
//                selectResult.add(item);
//            }
//        });
        btnTest.setOnAction(event -> {
            ObservableList<Map<String, Object>> obs = tvDocs.getItems();
            System.out.println("gfhsh");
        });

    }

}