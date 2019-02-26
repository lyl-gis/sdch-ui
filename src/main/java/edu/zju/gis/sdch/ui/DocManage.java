package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.service.IndexService;
import edu.zju.gis.sdch.util.Contants;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.json.JSONObject;

import java.net.URL;
import java.util.*;

//import javafx.scene.input.MouseEvent;
//import javafx.util.Callback;


public class DocManage implements Initializable {

    @FXML
    private Button btnSelectDoc;
    @FXML
    private Button btnAddDoc;
    @FXML
    private Button btnConfirmAddDocs;
    @FXML
    private Button btnSaveModifiedDocs;
    @FXML
    public TableView<Map<String, SimpleStringProperty>> tvDocs;
    @FXML
    private TextField tfWord;

    public IndexService indexService;
    public static final String TITLE = "文档编辑";
    public static DocManage instance = null;
    public static int addNumber;
    public static IndexManage indexManage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        indexManage = IndexManage.instance;
        this.indexService = indexManage.indexService;
        btnConfirmAddDocs.setDisable(true);
        tvDocs.setEditable(true);
        tvDocs.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);//设置可以多选
//        tvDocs.setRowFactory(new Callback<TableView<Map<String, SimpleStringProperty>>, TableRow<Map<String, SimpleStringProperty>>>() {
//            @Override
//            public TableRow<Map<String, SimpleStringProperty>> call(TableView<Map<String, SimpleStringProperty>> param) {
//                return new TableRowControl();
//            }
//        });
        tvDocs.setRowFactory(param -> new TableRowControl());
        btnSelectDoc.setOnMouseClicked(event -> {
            if (tfWord.getText().equals("")) {
                new Alert(Alert.AlertType.WARNING, "请填写查询条件", ButtonType.OK)
                        .showAndWait();
            } else {
//         String[] indexNames = indexManage.indexService.getIndexNames();
                String[] indexNames = {"sdmap"};
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
                List<Map<String, Object>> result = new ArrayList<>();
                result.clear();
                for (SearchHit hit : hits) {
                    Map<String, Object> item = hit.getSourceAsMap();
                    item.put("_id", hit.getId());
                    item = adjustMap(item);//调整列的顺序，将lsid,name,address三列提前
                    result.add(item);
                }
                //将查到的数据显示在表格中
                //result的size是一共有多少行，result中的每条记录中是个map，map中key的数量是一共有多少列
                List<TableColumn<Map<String, SimpleStringProperty>, String>> tableColumns = new ArrayList<>();
                Map<String, Object> map = new LinkedHashMap<>();
                //如果直接写成Map<String, Object> map = result.get(0)，则result.get(0)的结果会改变,所以要这样赋值
                for (String string : result.get(0).keySet())
                    map.put(string, result.get(0).get(string));
//                List<String> list = new ArrayList<>();
//                for (String string : map.keySet())
//                    list.add(string);
                List<String> list = new ArrayList<>(map.keySet());
                //得到不同类别结果中的所有字段
                for (int i = 1; i < result.size(); i++) {
                    Map<String, Object> map2 = result.get(i);
                    for (String string : map2.keySet()) {
                        if (!list.contains(string))
                            map.put(string, map2.get(string));
                    }
                }
                for (String key : map.keySet()) {
                    TableColumn<Map<String, SimpleStringProperty>, String> column = new TableColumn<>(key);
                    tableColumns.add(column);
                    column.setCellFactory(TextFieldTableCell.forTableColumn());
                    column.setCellValueFactory(param -> param.getValue().get(key));
                    if (key.equals("_id"))
                        column.setVisible(false);
                    if (key.equals("dtype"))
                        column.setVisible(false);
                }
                tvDocs.getColumns().clear();
                tvDocs.getColumns().addAll(tableColumns);
                tvDocs.getItems().clear();
//                for (int i = 0; i < result.size(); i++) {
                for (Map<String, Object> stringObjectMap : result) {
                    Map<String, SimpleStringProperty> map2 = new HashMap<>();
//                    for (String string : result.get(i).keySet()) {
//                        map2.put(string, new SimpleStringProperty(result.get(i).get(string).toString()));
                    for (String string : stringObjectMap.keySet()) {
                        map2.put(string, new SimpleStringProperty(stringObjectMap.get(string).toString()));
                    }
                    tvDocs.getItems().add(map2);
                }
            }
        });
        btnAddDoc.setOnMouseClicked(event -> {
            if (tvDocs.getColumns().size() == 1) {
                new Alert(Alert.AlertType.INFORMATION, "请先进行查询", ButtonType.OK)
                        .showAndWait();
            } else {
                //在表格最后一行加入数据
                Map<String, SimpleStringProperty> docAdd = new HashMap<>();
                for (int i = 0; i < tvDocs.getColumns().size(); i++) {
                    if (tvDocs.getColumns().get(i).getText().equals("lsid"))
                        docAdd.put(tvDocs.getColumns().get(i).getText(), new SimpleStringProperty("可以编辑的行"));
                    else
                        docAdd.put(tvDocs.getColumns().get(i).getText(), new SimpleStringProperty(""));
                }
                addNumber++;
                tvDocs.getItems().add(docAdd);
                btnConfirmAddDocs.setDisable(false);
            }
        });
        btnConfirmAddDocs.setOnAction(evnet -> {
            //获取要编辑行的行数
            int addValidNumber = 0;
            for (int i = tvDocs.getItems().size() - addNumber; i < tvDocs.getItems().size(); i++) {
                if (!tvDocs.getItems().get(i).get("lsid").getValue().equals("可以编辑的行"))
                    addValidNumber++;
            }
            if (addValidNumber == 0) {
                new Alert(Alert.AlertType.INFORMATION, "请修改插入数据的lsid", ButtonType.OK)
                        .showAndWait();
            } else {
//                Boolean bool = true;
                boolean bool = true;
                for (int i = tvDocs.getItems().size() - addNumber; i < tvDocs.getItems().size() - addNumber + addValidNumber; i++) {
                    if (tvDocs.getItems().get(i).containsKey("the_shape")) {
                        if (tvDocs.getItems().get(i).get("the_shape").getValue().equals(""))
                            bool = false;
                    }
                    if (tvDocs.getItems().get(i).containsKey("the_point")) {
                        if (tvDocs.getItems().get(i).get("the_point").getValue().equals(""))
                            bool = false;
                    }
                }
                if (!bool) {
                    new Alert(Alert.AlertType.INFORMATION, "请确保the_shape或the_point的值填写完整", ButtonType.OK)
                            .showAndWait();
                } else {
                    //将添加在表中最后一行的值添加到ES中
                    Map<String, Object> mapAdd = new HashMap<>();
                    Map<String, Map<String, Object>> kvIdDoc = new HashMap<>();
                    for (int i = tvDocs.getItems().size() - addNumber; i < tvDocs.getItems().size() - addNumber + addValidNumber; i++) {//减去addnumber这里还有问题，当添加两行之编辑一行时
                        Map<String, SimpleStringProperty> map = tvDocs.getItems().get(i);
                        for (String string : map.keySet()) {
                            if (string.equals("the_shape")) {
                                //用表格中的数据新建geoJSON对象
                                //测试数据：{ "type": "Polygon", "coordinates": [ [ [ 117.31902893230101, 35.661252038474515 ], [ 117.31897783372679, 35.661176457654626 ], [ 117.31890896454492, 35.661183484957867 ], [ 117.3188440874531, 35.661194830805385 ], [ 117.31878016903771, 35.661224561492759 ], [ 117.31869554642989, 35.661268044612683 ], [ 117.3186467042482, 35.661311798428088 ], [ 117.31858239732507, 35.661355229387155 ], [ 117.3184933050875, 35.661385386353643 ], [ 117.3184491070065, 35.661393223945552 ], [ 117.31841077520345, 35.66140002102204 ], [ 117.31822310468229, 35.661408437779571 ], [ 117.31785881810605, 35.661427766014022 ], [ 117.31750645384187, 35.661447453077663 ], [ 117.31724820722457, 35.661478403248637 ], [ 117.31706720877298, 35.661498339421883 ], [ 117.31684028824053, 35.661521637261458 ], [ 117.31679930613457, 35.661544096930093 ], [ 117.31677336249096, 35.661572285279796 ], [ 117.3167468873495, 35.661578482508105 ], [ 117.31668192752122, 35.661569635878458 ], [ 117.31657145030759, 35.661554055126288 ], [ 117.31636422223185, 35.661532955236559 ], [ 117.31616049162005, 35.661502109393915 ], [ 117.31600402307879, 35.661472584654753 ], [ 117.31589391278935, 35.661439364600568 ], [ 117.31581643799761, 35.661394133201128 ], [ 117.31571411673862, 35.661320483226298 ], [ 117.31564490941578, 35.661244946473509 ], [ 117.31560262509694, 35.661163931949908 ], [ 117.31557714011352, 35.661087200896667 ], [ 117.31556124100494, 35.660978357752228 ], [ 117.31556162142461, 35.660854329555193 ], [ 117.31557826698354, 35.660710789667704 ], [ 117.31562102345762, 35.660580884400687 ], [ 117.31566917136581, 35.660473347969891 ], [ 117.31572279524349, 35.660409395381926 ], [ 117.31579808918448, 35.660352596900346 ], [ 117.31618262580692, 35.660098122938429 ], [ 117.31634115740037, 35.659983425204686 ], [ 117.31643397733077, 35.65989197044955 ], [ 117.31653171925002, 35.659817086601791 ], [ 117.31663267984086, 35.659772841755753 ], [ 117.31673234810572, 35.659736891356701 ], [ 117.31680463291428, 35.659700652275532 ], [ 117.31684949919271, 35.659663012051347 ], [ 117.31687711827406, 35.659612105028785 ], [ 117.31689897180215, 35.659556164501168 ], [ 117.31691070076262, 35.659507461717226 ], [ 117.31690112568464, 35.659433575219218 ], [ 117.31685595994077, 35.659295962763821 ], [ 117.31681648780511, 35.659145718431148 ], [ 117.31667008177529, 35.659165384807203 ], [ 117.31668956108791, 35.659212411254543 ], [ 117.31670892078864, 35.659303580023582 ], [ 117.3167101978211, 35.659400201382212 ], [ 117.31668661939295, 35.659467323180507 ], [ 117.31662329093089, 35.659534185974714 ], [ 117.31652335916264, 35.659614122213725 ], [ 117.31632543456766, 35.65970043914384 ], [ 117.31611864085907, 35.659784613889059 ], [ 117.31580801142974, 35.659858265668888 ], [ 117.3156372274771, 35.659891871536594 ], [ 117.31555289535218, 35.65989785742542 ], [ 117.31551976432873, 35.659896137922132 ], [ 117.31548658474271, 35.659882882815559 ], [ 117.3154372488316, 35.659960145369304 ], [ 117.3154156417189, 35.65999398505825 ], [ 117.31538563223735, 35.660086937183678 ], [ 117.31539038065033, 35.66022924949651 ], [ 117.3154067402117, 35.660343140535495 ], [ 117.31539875512605, 35.660443753085296 ], [ 117.31538990939266, 35.660484209086256 ], [ 117.31536097640053, 35.660559960778805 ], [ 117.31535217922956, 35.660612217683081 ], [ 117.31531373770399, 35.660717234213443 ], [ 117.31529324574888, 35.660782905405071 ], [ 117.31529193992743, 35.660896839611745 ], [ 117.31531003967703, 35.661004597274612 ], [ 117.31533254520528, 35.66110873876351 ], [ 117.31540829958746, 35.66127475450547 ], [ 117.31551932627967, 35.661425899957287 ], [ 117.31559156072119, 35.661485565475068 ], [ 117.31566769372451, 35.661526472934334 ], [ 117.31577209691589, 35.661569110688689 ], [ 117.31591803259678, 35.661614526448155 ], [ 117.31610377227001, 35.661673875402826 ], [ 117.31621648429839, 35.661695940266618 ], [ 117.3163168513333, 35.661722722074643 ], [ 117.31635449065682, 35.66174642190709 ], [ 117.3164061594039, 35.66177248335898 ], [ 117.31643065603599, 35.661782750918015 ], [ 117.3164567705485, 35.661793696565667 ], [ 117.31650628002339, 35.661820304805445 ], [ 117.31657341081564, 35.661819771506394 ], [ 117.31679013572888, 35.6618960115268 ], [ 117.31696385686381, 35.661932340536147 ], [ 117.31705268919488, 35.66194653093671 ], [ 117.31714236059403, 35.661949906990216 ], [ 117.31725708440861, 35.66192365308099 ], [ 117.31737473282075, 35.661855568107377 ], [ 117.31742887470715, 35.661811441073326 ], [ 117.31747690120298, 35.661782834538663 ], [ 117.31755719717131, 35.661761355130466 ], [ 117.31764328657142, 35.661753173996644 ], [ 117.31770339365799, 35.661762753573917 ], [ 117.31777326378382, 35.6617849263571 ], [ 117.31779911119783, 35.661793981630048 ], [ 117.31787543395878, 35.661820716673724 ], [ 117.3179546336507, 35.661855485360846 ], [ 117.31809244216083, 35.66185801335299 ], [ 117.31816132393314, 35.661853870175285 ], [ 117.31823812513569, 35.661842493750612 ], [ 117.31830738462402, 35.661822485633536 ], [ 117.31836640083557, 35.661781950491374 ], [ 117.318510962359, 35.661707304963514 ], [ 117.31874065820362, 35.661607560255987 ], [ 117.31881598092247, 35.661558331367893 ], [ 117.31885116959683, 35.661523627430022 ], [ 117.31887045645897, 35.661487524147645 ], [ 117.31887853956924, 35.66141178954269 ], [ 117.3188915041992, 35.661341809699231 ], [ 117.31894206048939, 35.661285075069976 ], [ 117.31902893230101, 35.661252038474515 ] ] ] }
                                String geoJSON = map.get(string).getValue();
                                if (geoJSON.contains("="))
                                    geoJSON = geoJSON.replace("=", ":");
                                JSONObject jsonObject = new JSONObject(geoJSON);
                                Map<String, Object> map2 = new HashMap();
//                  String geoJSON="{ \"type\": \"Polygon\", \"coordinates\": [ [ [ 117.31902893230101, 35.661252038474515 ], [ 117.31897783372679, 35.661176457654626 ], [ 117.31890896454492, 35.661183484957867 ], [ 117.3188440874531, 35.661194830805385 ], [ 117.31878016903771, 35.661224561492759 ], [ 117.31869554642989, 35.661268044612683 ], [ 117.3186467042482, 35.661311798428088 ], [ 117.31858239732507, 35.661355229387155 ], [ 117.3184933050875, 35.661385386353643 ], [ 117.3184491070065, 35.661393223945552 ], [ 117.31841077520345, 35.66140002102204 ], [ 117.31822310468229, 35.661408437779571 ], [ 117.31785881810605, 35.661427766014022 ], [ 117.31750645384187, 35.661447453077663 ], [ 117.31724820722457, 35.661478403248637 ], [ 117.31706720877298, 35.661498339421883 ], [ 117.31684028824053, 35.661521637261458 ], [ 117.31679930613457, 35.661544096930093 ], [ 117.31677336249096, 35.661572285279796 ], [ 117.3167468873495, 35.661578482508105 ], [ 117.31668192752122, 35.661569635878458 ], [ 117.31657145030759, 35.661554055126288 ], [ 117.31636422223185, 35.661532955236559 ], [ 117.31616049162005, 35.661502109393915 ], [ 117.31600402307879, 35.661472584654753 ], [ 117.31589391278935, 35.661439364600568 ], [ 117.31581643799761, 35.661394133201128 ], [ 117.31571411673862, 35.661320483226298 ], [ 117.31564490941578, 35.661244946473509 ], [ 117.31560262509694, 35.661163931949908 ], [ 117.31557714011352, 35.661087200896667 ], [ 117.31556124100494, 35.660978357752228 ], [ 117.31556162142461, 35.660854329555193 ], [ 117.31557826698354, 35.660710789667704 ], [ 117.31562102345762, 35.660580884400687 ], [ 117.31566917136581, 35.660473347969891 ], [ 117.31572279524349, 35.660409395381926 ], [ 117.31579808918448, 35.660352596900346 ], [ 117.31618262580692, 35.660098122938429 ], [ 117.31634115740037, 35.659983425204686 ], [ 117.31643397733077, 35.65989197044955 ], [ 117.31653171925002, 35.659817086601791 ], [ 117.31663267984086, 35.659772841755753 ], [ 117.31673234810572, 35.659736891356701 ], [ 117.31680463291428, 35.659700652275532 ], [ 117.31684949919271, 35.659663012051347 ], [ 117.31687711827406, 35.659612105028785 ], [ 117.31689897180215, 35.659556164501168 ], [ 117.31691070076262, 35.659507461717226 ], [ 117.31690112568464, 35.659433575219218 ], [ 117.31685595994077, 35.659295962763821 ], [ 117.31681648780511, 35.659145718431148 ], [ 117.31667008177529, 35.659165384807203 ], [ 117.31668956108791, 35.659212411254543 ], [ 117.31670892078864, 35.659303580023582 ], [ 117.3167101978211, 35.659400201382212 ], [ 117.31668661939295, 35.659467323180507 ], [ 117.31662329093089, 35.659534185974714 ], [ 117.31652335916264, 35.659614122213725 ], [ 117.31632543456766, 35.65970043914384 ], [ 117.31611864085907, 35.659784613889059 ], [ 117.31580801142974, 35.659858265668888 ], [ 117.3156372274771, 35.659891871536594 ], [ 117.31555289535218, 35.65989785742542 ], [ 117.31551976432873, 35.659896137922132 ], [ 117.31548658474271, 35.659882882815559 ], [ 117.3154372488316, 35.659960145369304 ], [ 117.3154156417189, 35.65999398505825 ], [ 117.31538563223735, 35.660086937183678 ], [ 117.31539038065033, 35.66022924949651 ], [ 117.3154067402117, 35.660343140535495 ], [ 117.31539875512605, 35.660443753085296 ], [ 117.31538990939266, 35.660484209086256 ], [ 117.31536097640053, 35.660559960778805 ], [ 117.31535217922956, 35.660612217683081 ], [ 117.31531373770399, 35.660717234213443 ], [ 117.31529324574888, 35.660782905405071 ], [ 117.31529193992743, 35.660896839611745 ], [ 117.31531003967703, 35.661004597274612 ], [ 117.31533254520528, 35.66110873876351 ], [ 117.31540829958746, 35.66127475450547 ], [ 117.31551932627967, 35.661425899957287 ], [ 117.31559156072119, 35.661485565475068 ], [ 117.31566769372451, 35.661526472934334 ], [ 117.31577209691589, 35.661569110688689 ], [ 117.31591803259678, 35.661614526448155 ], [ 117.31610377227001, 35.661673875402826 ], [ 117.31621648429839, 35.661695940266618 ], [ 117.3163168513333, 35.661722722074643 ], [ 117.31635449065682, 35.66174642190709 ], [ 117.3164061594039, 35.66177248335898 ], [ 117.31643065603599, 35.661782750918015 ], [ 117.3164567705485, 35.661793696565667 ], [ 117.31650628002339, 35.661820304805445 ], [ 117.31657341081564, 35.661819771506394 ], [ 117.31679013572888, 35.6618960115268 ], [ 117.31696385686381, 35.661932340536147 ], [ 117.31705268919488, 35.66194653093671 ], [ 117.31714236059403, 35.661949906990216 ], [ 117.31725708440861, 35.66192365308099 ], [ 117.31737473282075, 35.661855568107377 ], [ 117.31742887470715, 35.661811441073326 ], [ 117.31747690120298, 35.661782834538663 ], [ 117.31755719717131, 35.661761355130466 ], [ 117.31764328657142, 35.661753173996644 ], [ 117.31770339365799, 35.661762753573917 ], [ 117.31777326378382, 35.6617849263571 ], [ 117.31779911119783, 35.661793981630048 ], [ 117.31787543395878, 35.661820716673724 ], [ 117.3179546336507, 35.661855485360846 ], [ 117.31809244216083, 35.66185801335299 ], [ 117.31816132393314, 35.661853870175285 ], [ 117.31823812513569, 35.661842493750612 ], [ 117.31830738462402, 35.661822485633536 ], [ 117.31836640083557, 35.661781950491374 ], [ 117.318510962359, 35.661707304963514 ], [ 117.31874065820362, 35.661607560255987 ], [ 117.31881598092247, 35.661558331367893 ], [ 117.31885116959683, 35.661523627430022 ], [ 117.31887045645897, 35.661487524147645 ], [ 117.31887853956924, 35.66141178954269 ], [ 117.3188915041992, 35.661341809699231 ], [ 117.31894206048939, 35.661285075069976 ], [ 117.31902893230101, 35.661252038474515 ] ] ] }";
                                map2.put("coordinates", jsonObject.getJSONArray("coordinates"));
                                map2.put("type", jsonObject.get("type").toString());
                                mapAdd.put(string, map2);
                            } else if (string.equals("the_point")) {
                                String geoJSON = map.get(string).getValue();
                                if (geoJSON.contains("="))
                                    geoJSON = geoJSON.replace("=", ":");
                                JSONObject jsonObject = new JSONObject(geoJSON);
                                Map<String, Object> map2 = new HashMap();
                                map2.put("lat", jsonObject.get("lat"));
                                map2.put("lon", jsonObject.get("lon"));
                                mapAdd.put(string, map2);
                            } else if (string.equals("_id")) {
                                continue;
                            } else
                                mapAdd.put(string, map.get(string).getValue());
                        }
                        if (mapAdd.get("lsid").toString().equals("")) {
                            kvIdDoc.put("0", mapAdd);
                        } else
                            kvIdDoc.put(mapAdd.get("lsid").toString(), mapAdd);
                    }
                    int erroNumber = indexManage.helper.upsert("sdmap", "_doc", kvIdDoc);
                    int resultNumber = addValidNumber - erroNumber;
                    new Alert(Alert.AlertType.INFORMATION, "成功插入" + resultNumber + "条数据", ButtonType.OK)
                            .showAndWait();
                    addNumber = 0;
                    btnConfirmAddDocs.setDisable(true);
                }
            }
        });

        btnSaveModifiedDocs.setOnAction(event -> {
            //在ES中修改
            Map<String, Map<String, Object>> kvIdDoc = new HashMap<>();
            for (int i = 0; i < tvDocs.getItems().size(); i++) {
                Map<String, SimpleStringProperty> mapModify = tvDocs.getItems().get(i);
                if (!mapModify.get("lsid").getValue().equals("可以编辑的行")) {
                    Map<String, Object> map = new HashMap<>();
                    for (String string : mapModify.keySet()) {
                        if (string.equals("the_shape")) {
                            String geoJSON = mapModify.get(string).getValue();
                            geoJSON = geoJSON.replace("=", ":");//不改变the_shape时，需要将原来值中的等号换为冒号
                            JSONObject jsonObject = new JSONObject(geoJSON);
                            Map<String, Object> map2 = new HashMap<>();
                            map2.put("coordinates", jsonObject.getJSONArray("coordinates"));
                            map2.put("type", jsonObject.get("type").toString());
//                      String geoJSON="{ \"type\": \"Polygon\", \"coordinates\": [ [ [ 117.31902893230101, 35.661252038474515 ], [ 117.31897783372679, 35.661176457654626 ], [ 117.31890896454492, 35.661183484957867 ], [ 117.3188440874531, 35.661194830805385 ], [ 117.31878016903771, 35.661224561492759 ], [ 117.31869554642989, 35.661268044612683 ], [ 117.3186467042482, 35.661311798428088 ], [ 117.31858239732507, 35.661355229387155 ], [ 117.3184933050875, 35.661385386353643 ], [ 117.3184491070065, 35.661393223945552 ], [ 117.31841077520345, 35.66140002102204 ], [ 117.31822310468229, 35.661408437779571 ], [ 117.31785881810605, 35.661427766014022 ], [ 117.31750645384187, 35.661447453077663 ], [ 117.31724820722457, 35.661478403248637 ], [ 117.31706720877298, 35.661498339421883 ], [ 117.31684028824053, 35.661521637261458 ], [ 117.31679930613457, 35.661544096930093 ], [ 117.31677336249096, 35.661572285279796 ], [ 117.3167468873495, 35.661578482508105 ], [ 117.31668192752122, 35.661569635878458 ], [ 117.31657145030759, 35.661554055126288 ], [ 117.31636422223185, 35.661532955236559 ], [ 117.31616049162005, 35.661502109393915 ], [ 117.31600402307879, 35.661472584654753 ], [ 117.31589391278935, 35.661439364600568 ], [ 117.31581643799761, 35.661394133201128 ], [ 117.31571411673862, 35.661320483226298 ], [ 117.31564490941578, 35.661244946473509 ], [ 117.31560262509694, 35.661163931949908 ], [ 117.31557714011352, 35.661087200896667 ], [ 117.31556124100494, 35.660978357752228 ], [ 117.31556162142461, 35.660854329555193 ], [ 117.31557826698354, 35.660710789667704 ], [ 117.31562102345762, 35.660580884400687 ], [ 117.31566917136581, 35.660473347969891 ], [ 117.31572279524349, 35.660409395381926 ], [ 117.31579808918448, 35.660352596900346 ], [ 117.31618262580692, 35.660098122938429 ], [ 117.31634115740037, 35.659983425204686 ], [ 117.31643397733077, 35.65989197044955 ], [ 117.31653171925002, 35.659817086601791 ], [ 117.31663267984086, 35.659772841755753 ], [ 117.31673234810572, 35.659736891356701 ], [ 117.31680463291428, 35.659700652275532 ], [ 117.31684949919271, 35.659663012051347 ], [ 117.31687711827406, 35.659612105028785 ], [ 117.31689897180215, 35.659556164501168 ], [ 117.31691070076262, 35.659507461717226 ], [ 117.31690112568464, 35.659433575219218 ], [ 117.31685595994077, 35.659295962763821 ], [ 117.31681648780511, 35.659145718431148 ], [ 117.31667008177529, 35.659165384807203 ], [ 117.31668956108791, 35.659212411254543 ], [ 117.31670892078864, 35.659303580023582 ], [ 117.3167101978211, 35.659400201382212 ], [ 117.31668661939295, 35.659467323180507 ], [ 117.31662329093089, 35.659534185974714 ], [ 117.31652335916264, 35.659614122213725 ], [ 117.31632543456766, 35.65970043914384 ], [ 117.31611864085907, 35.659784613889059 ], [ 117.31580801142974, 35.659858265668888 ], [ 117.3156372274771, 35.659891871536594 ], [ 117.31555289535218, 35.65989785742542 ], [ 117.31551976432873, 35.659896137922132 ], [ 117.31548658474271, 35.659882882815559 ], [ 117.3154372488316, 35.659960145369304 ], [ 117.3154156417189, 35.65999398505825 ], [ 117.31538563223735, 35.660086937183678 ], [ 117.31539038065033, 35.66022924949651 ], [ 117.3154067402117, 35.660343140535495 ], [ 117.31539875512605, 35.660443753085296 ], [ 117.31538990939266, 35.660484209086256 ], [ 117.31536097640053, 35.660559960778805 ], [ 117.31535217922956, 35.660612217683081 ], [ 117.31531373770399, 35.660717234213443 ], [ 117.31529324574888, 35.660782905405071 ], [ 117.31529193992743, 35.660896839611745 ], [ 117.31531003967703, 35.661004597274612 ], [ 117.31533254520528, 35.66110873876351 ], [ 117.31540829958746, 35.66127475450547 ], [ 117.31551932627967, 35.661425899957287 ], [ 117.31559156072119, 35.661485565475068 ], [ 117.31566769372451, 35.661526472934334 ], [ 117.31577209691589, 35.661569110688689 ], [ 117.31591803259678, 35.661614526448155 ], [ 117.31610377227001, 35.661673875402826 ], [ 117.31621648429839, 35.661695940266618 ], [ 117.3163168513333, 35.661722722074643 ], [ 117.31635449065682, 35.66174642190709 ], [ 117.3164061594039, 35.66177248335898 ], [ 117.31643065603599, 35.661782750918015 ], [ 117.3164567705485, 35.661793696565667 ], [ 117.31650628002339, 35.661820304805445 ], [ 117.31657341081564, 35.661819771506394 ], [ 117.31679013572888, 35.6618960115268 ], [ 117.31696385686381, 35.661932340536147 ], [ 117.31705268919488, 35.66194653093671 ], [ 117.31714236059403, 35.661949906990216 ], [ 117.31725708440861, 35.66192365308099 ], [ 117.31737473282075, 35.661855568107377 ], [ 117.31742887470715, 35.661811441073326 ], [ 117.31747690120298, 35.661782834538663 ], [ 117.31755719717131, 35.661761355130466 ], [ 117.31764328657142, 35.661753173996644 ], [ 117.31770339365799, 35.661762753573917 ], [ 117.31777326378382, 35.6617849263571 ], [ 117.31779911119783, 35.661793981630048 ], [ 117.31787543395878, 35.661820716673724 ], [ 117.3179546336507, 35.661855485360846 ], [ 117.31809244216083, 35.66185801335299 ], [ 117.31816132393314, 35.661853870175285 ], [ 117.31823812513569, 35.661842493750612 ], [ 117.31830738462402, 35.661822485633536 ], [ 117.31836640083557, 35.661781950491374 ], [ 117.318510962359, 35.661707304963514 ], [ 117.31874065820362, 35.661607560255987 ], [ 117.31881598092247, 35.661558331367893 ], [ 117.31885116959683, 35.661523627430022 ], [ 117.31887045645897, 35.661487524147645 ], [ 117.31887853956924, 35.66141178954269 ], [ 117.3188915041992, 35.661341809699231 ], [ 117.31894206048939, 35.661285075069976 ], [ 117.31902893230101, 35.661252038474515 ] ] ] }";
                            map.put(string, map2);
                        } else if (string.equals("the_point")) {
                            String geoJSON = mapModify.get(string).getValue();
                            geoJSON = geoJSON.replace("=", ":");//不改变the_shape时，需要将原来值中的等号换为冒号
                            JSONObject jsonObject = new JSONObject(geoJSON);
                            Map<String, Object> map2 = new HashMap<>();
                            map2.put("lon", jsonObject.get("lon"));
                            map2.put("lat", jsonObject.get("lat"));
                            map.put(string, map2);
                        } else if (string.equals("_id")) {
                            continue;
                        } else {
                            map.put(string, mapModify.get(string).getValue());
                        }
                    }
                    //kvIdDoc第二个参数应该是map<string,object>
                    kvIdDoc.put(mapModify.get("lsid").get(), map);
                }
            }
            int resultNumber = indexManage.helper.updateFromMap("sdmap", "_doc", kvIdDoc);
            int modifyNumber = kvIdDoc.size();
            new Alert(Alert.AlertType.INFORMATION, "共编辑条数" + modifyNumber + "失败条数" + resultNumber, ButtonType.OK)
                    .showAndWait();
            kvIdDoc.clear();
        });
    }

    class TableRowControl extends TableRow<Map<String, SimpleStringProperty>> {
        //        public TableRowControl() {
//            this.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent event) {
//                    //右键下拉选项删除选中行
//                    if (event.getButton().equals(MouseButton.SECONDARY)
//                            && event.getClickCount() == 1
//                            && TableRowControl.this.getIndex() < tvDocs.getItems().size()) {
//                        final ContextMenu contextMenu = new ContextMenu();
//                        MenuItem deleteItem = new MenuItem("删除");
//                        contextMenu.getItems().addAll(deleteItem);
//                        deleteItem.setOnAction(new EventHandler<ActionEvent>() {
//                            @Override
//                            public void handle(ActionEvent event) {
//                                List<Integer> newDeleteItems = new ArrayList<>();
//                                for (int i = 0; i < tvDocs.getSelectionModel().getSelectedIndices().size(); i++) {
//                                    //因为tvDocs.getSelectionModel().getSelectedIndices().size()的值observable类型，所以这样赋值，不然删除多行时，删除第一行后之后行的行号会发生变化
//                                    newDeleteItems.add(tvDocs.getSelectionModel().getSelectedIndices().get(i));
//                                }
//                                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "确认删除?");
//                                Optional<ButtonType> result = confirmation.showAndWait();
//                                if (result.isPresent() && result.get() == ButtonType.OK) {
//                                    if (newDeleteItems.size() == 0) {
//                                        new Alert(Alert.AlertType.INFORMATION, "没有要删除的内容", ButtonType.OK)
//                                                .showAndWait();
//                                    } else {
//                                        List<String> idList = new ArrayList<>();
//                                        int deleted = 0;
//                                        for (int i = 0; i < newDeleteItems.size(); i++) {
//                                            Map<String, SimpleStringProperty> mapDelete = tvDocs.getItems().get(newDeleteItems.get(i) - deleted);
//                                            idList.add(mapDelete.get("lsid").get());
//                                            tvDocs.getItems().remove(mapDelete);
//                                            deleted++;
//                                        }
//                                        int resultNumber = indexManage.helper.delete("sdmap", "_doc", idList);
//                                        new Alert(Alert.AlertType.INFORMATION, "共删除条数" + newDeleteItems.size() + "失败数" + resultNumber, ButtonType.OK)
//                                                .showAndWait();
        TableRowControl() {
            this.setOnMouseClicked(event -> {
                //右键下拉选项删除选中行
                if (event.getButton().equals(MouseButton.SECONDARY)
                        && event.getClickCount() == 1
                        && TableRowControl.this.getIndex() < tvDocs.getItems().size()) {
                    final ContextMenu contextMenu = new ContextMenu();
                    MenuItem deleteItem = new MenuItem("删除");
                    contextMenu.getItems().addAll(deleteItem);
                    deleteItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            //因为tvDocs.getSelectionModel().getSelectedIndices().size()的值observable类型，所以这样赋值，不然删除多行时，删除第一行后之后行的行号会发生变化
                            List<Integer> newDeleteItems = new ArrayList<>(tvDocs.getSelectionModel().getSelectedIndices());
                            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "确认删除?");
                            Optional<ButtonType> result = confirmation.showAndWait();
                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                if (newDeleteItems.size() == 0) {
                                    new Alert(Alert.AlertType.INFORMATION, "没有要删除的内容", ButtonType.OK)
                                            .showAndWait();
                                } else {
                                    List<String> idList = new ArrayList<>();
                                    int deleted = 0;
                                    for (Integer newDeleteItem : newDeleteItems) {
                                        Map<String, SimpleStringProperty> mapDelete = tvDocs.getItems().get(newDeleteItem - deleted);
                                        idList.add(mapDelete.get("lsid").get());
                                        tvDocs.getItems().remove(mapDelete);
                                        deleted++;
                                    }
                                    int resultNumber = indexManage.helper.delete("sdmap", "_doc", idList);
                                    new Alert(Alert.AlertType.INFORMATION, "共删除条数" + newDeleteItems.size() + "失败数" + resultNumber, ButtonType.OK)
                                            .showAndWait();
                                }
                            }
//                        });
//                        tvDocs.setContextMenu(contextMenu);
//                    }
                        }
                    });
                    tvDocs.setContextMenu(contextMenu);
                }
            });
        }
    }

    //调整列的顺序
    Map<String, Object> adjustMap(Map<String, Object> map) {
        Map<String, Object> adjustedMap = new LinkedHashMap<>();
        if (map.containsKey("lsid"))
            adjustedMap.put("lsid", map.get("lsid"));
        if (map.containsKey("name"))
            adjustedMap.put("name", map.get("name"));
        if (map.containsKey("address"))
            adjustedMap.put("address", map.get("address"));

        for (String key : map.keySet()) {
            if (key.equals("lsid") || key.equals("name") || key.equals("address"))
                continue;
            adjustedMap.put(key, map.get(key));
        }
        return adjustedMap;
    }

}