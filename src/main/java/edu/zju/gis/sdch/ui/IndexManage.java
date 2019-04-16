package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.config.CommonSetting;
import edu.zju.gis.sdch.mapper.CategoryMapper;
import edu.zju.gis.sdch.mapper.IndexMapper;
import edu.zju.gis.sdch.mapper.IndexMappingMapper;
import edu.zju.gis.sdch.mapper.IndexTypeMapper;
import edu.zju.gis.sdch.model.Index;
import edu.zju.gis.sdch.service.IndexService;
import edu.zju.gis.sdch.service.impl.IndexServiceImpl;
import edu.zju.gis.sdch.util.ElasticSearchHelper;
import edu.zju.gis.sdch.util.MyBatisUtil;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;


public class IndexManage implements Initializable {
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnAdd;
    @FXML
    public TableView<MyIndex> tvIndex;
    @FXML
    public TableColumn<MyIndex, String> tcIndice;
    @FXML
    private TableColumn<MyIndex, Number> tcShards;
    @FXML
    private TableColumn<MyIndex, Number> tcReplicas;
    @FXML
    private TableColumn<MyIndex, String> tcDescription;
    @FXML
    private TableColumn<MyIndex, String> tcCategory;

    @FXML
    private BorderPane rootLayout;

    public static IndexManage instance = null;
    public static IndexMapper mapper;
    public ElasticSearchHelper helper;
    public CommonSetting setting;
    public IndexService indexService;
    public String indexNames;//选择的索引
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        mapper = MyBatisUtil.getMapper(IndexMapper.class);
        InputStream is = Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("config.properties"));
        // 创建会话工厂，传入mybatis的配置文件信息
        Properties props = new Properties();
        try {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setting = new CommonSetting();
        setting.setEsName(props.getProperty("es.name", "elasticsearch"));
        setting.setEsHosts(Arrays.asList(props.getProperty("es.hosts").split(",")));
        setting.setEsPort(Integer.parseInt(props.getProperty("es.port", "9300")));
        setting.setEsShards(Integer.parseInt(props.getProperty("es.number_of_shards", "4")));
        setting.setEsReplicas(Integer.parseInt(props.getProperty("es.number_of_replicas", "0")));
        setting.setEsFieldBoostDefault(Float.parseFloat(props.getProperty("es.field_boost_default", "4.0f")));
        try {
            helper = new ElasticSearchHelper(setting.getEsHosts(), setting.getEsPort(), setting.getEsName());
            indexService = new IndexServiceImpl(helper
                    , MyBatisUtil.getMapper(CategoryMapper.class)
                    , MyBatisUtil.getMapper(IndexMapper.class)
                    , MyBatisUtil.getMapper(IndexTypeMapper.class)
                    , MyBatisUtil.getMapper(IndexMappingMapper.class));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
//        tvIndex.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);//设置表格中数据可以多选
        tcIndice.setCellFactory(TextFieldTableCell.forTableColumn());
        tcIndice.setCellValueFactory(cellData -> cellData.getValue().getIndice());
        tcShards.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object.toString();
            }

            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string);
            }
        }));
        tcShards.setCellValueFactory(cellData -> cellData.getValue().getShards());
        tcReplicas.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object.toString();
            }

            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string);
            }
        }));
        tcReplicas.setCellValueFactory(cellData -> cellData.getValue().getReplicas());
        tcCategory.setCellFactory(TextFieldTableCell.forTableColumn());
        tcCategory.setCellValueFactory(cellData -> cellData.getValue().getCategory());
        tcDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        tcDescription.setCellValueFactory(cellData -> cellData.getValue().getDescription());
//        tvIndex.setEditable(true);
        //将数据库中的索引信息呈现在表格中
        ObservableList<MyIndex> indices = tvIndex.getItems();//必须放在列与数据绑定之后
        //修改为一进入页面便有数据在表格中
        List<Index> indexList = mapper.selectAll();
        for (int i = 0; i < indexList.size(); i++) {
            MyIndex index = new MyIndex();
            index.getDescription().set(indexList.get(i).getDescription());
            index.getCategory().set(indexList.get(i).getCategory());
            index.getReplicas().set(indexList.get(i).getReplicas());
            index.getShards().set(indexList.get(i).getShards());
            index.getIndice().set(indexList.get(i).getIndice());
            indices.add(index);
        }
        tvIndex.setRowFactory(param -> new TableRowControl());
        btnRefresh.setOnMouseClicked(event -> {
            indices.clear();
            List<Index> indexListt = mapper.selectAll();
            for (int i = 0; i < indexListt.size(); i++) {
                MyIndex index = new MyIndex();
                index.getDescription().set(indexListt.get(i).getDescription());
                index.getCategory().set(indexListt.get(i).getCategory());
                index.getReplicas().set(indexListt.get(i).getReplicas());
                index.getShards().set(indexListt.get(i).getShards());
                index.getIndice().set(indexListt.get(i).getIndice());
                indices.add(index);
            }

        });
        btnAdd.setOnMouseClicked(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("IndexAdd.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle(IndexAdd.TITLE);
            stage.setScene(scene);
            stage.show();

        });

    }

    class TableRowControl extends TableRow<MyIndex> {
        TableRowControl() {
            this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    //右键下拉选项删除选中行或进入文档编辑页面
                    if (event.getButton().equals(MouseButton.SECONDARY)
                            && event.getClickCount() == 1
                            && TableRowControl.this.getIndex() < tvIndex.getItems().size()) {
                        final ContextMenu contextMenu = new ContextMenu();
                        MenuItem deleteItem = new MenuItem("删除");
                        MenuItem manageDocsItem = new MenuItem("编辑文档");
                        contextMenu.getItems().addAll(deleteItem, manageDocsItem);
                        deleteItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
//                                List<Integer> newDeleteItems = new ArrayList<>();
//                                for (int i = 0; i < tvIndex.getSelectionModel().getSelectedIndices().size(); i++) {
//                                    //因为tvIndex.getSelectionModel().getSelectedIndices().size()的值observable类型，所以这样赋值，不然删除多行时，删除第一行后之后行的行号会发生变化
//                                    newDeleteItems.add(tvIndex.getSelectionModel().getSelectedIndices().get(i));
//                                }
                                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "确认删除?");
                                Optional<ButtonType> result = confirmation.showAndWait();
                                if (result.isPresent() && result.get() == ButtonType.OK) {
                                    //数据库中删除
                                    int res1 = mapper.deleteByPrimaryKey(tvIndex.getSelectionModel().getSelectedItem().getIndice().get());
                                    //ES中删除
                                    Boolean res2 = helper.delete(tvIndex.getSelectionModel().getSelectedItem().getIndice().get());
                                    //表中删除
                                    tvIndex.getItems().remove(tvIndex.getSelectionModel().getSelectedItem());
                                    if (res1 == 1 && res2) {
                                        new Alert(Alert.AlertType.INFORMATION, "成功删除1个索引", ButtonType.OK).showAndWait();
                                    }
                                }
//                                    if (newDeleteItems.size() == 0) {
//                                        new Alert(Alert.AlertType.INFORMATION, "没有要删除的内容", ButtonType.OK)
//                                                .showAndWait();
//                                    } else {
//                                        int deleted = 0;
//                                        List<Integer> deleteResult = new ArrayList<>();
//                                        for (int i = 0; i < newDeleteItems.size(); i++) {
//                                            helper.delete(tvIndex.getItems().get(newDeleteItems.get(i) - deleted).getIndice().getValue());//在ES中删除
//                                            int deleteresult = mapper.deleteByPrimaryKey(tvIndex.getItems().get(newDeleteItems.get(i) - deleted).getIndice().getValue());//在数据库中删除
//                                            deleteResult.add(deleteresult);
//                                            tvIndex.getItems().remove(newDeleteItems.get(i).intValue() - deleted);   //在表中删除打钩的那一行
//                                            deleted++;
//                                        }
//                                        int succedDelete = 0;
//                                        int failedDelete = 0;
//                                        for (int i = 0; i < deleteResult.size(); i++) {
//                                            if (deleteResult.get(i) == 1)
//                                                succedDelete++;
//                                            else
//                                                failedDelete++;
//                                        }
//                                        new Alert(Alert.AlertType.INFORMATION, "成功删除" + succedDelete + "条数据," + failedDelete + "条数据删除失败", ButtonType.OK)
//                                                .showAndWait();
//                                    }
//                                }
                            }
                        });
                        manageDocsItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                indexNames = tvIndex.getSelectionModel().getSelectedItem().getIndice().get();
                                Parent root = null;
                                try {
                                    root = FXMLLoader.load(getClass().getResource("DocManage.fxml"));
                                    Scene scene = new Scene(root);
                                    Stage primaryStage = (Stage) rootLayout.getScene().getWindow();
                                    Stage stage = new Stage();
                                    stage.initOwner(primaryStage);
                                    stage.setTitle(DocManage.TITLE);
                                    stage.setScene(scene);
                                    stage.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        tvIndex.setContextMenu(contextMenu);
                    }
                }
            });
        }
    }

}

