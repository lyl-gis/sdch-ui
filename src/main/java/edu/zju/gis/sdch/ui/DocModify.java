package edu.zju.gis.sdch.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class DocModify implements Initializable {
    @FXML
    private TableView<DocAdded> tvDocModify;
    @FXML
    private TableColumn<DocAdded, String> tcKeys;

    @FXML
    private TableColumn<DocAdded, String> tcValues;
    @FXML
    private Button btnSaveModify;

    @FXML
    private Button btnCancelModify;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DocManage docManage = DocManage.instance;
        IndexManage indexManage = IndexManage.instance;
        //本来想批量编辑文档，但最后遇到问题没有实现
       /* for(int i=0;i<docManage.ModifyNumber;i++){
            TableColumn<DocAdded,String> t1=new TableColumn<>("keys");
            t1.setCellFactory(TextFieldTableCell.forTableColumn());
            t1.setCellValueFactory(cellData->cellData.getValue().getListkeys().get(i));
            tvDocModify.getColumns().add(t1);
            TableColumn<DocAdded,String> t2=new TableColumn<>("values");
            t2.setCellFactory(TextFieldTableCell.forTableColumn());
            t2.setCellValueFactory(cellData->cellData.getValue().getListkeys().get(i));
            tvDocModify.getColumns().add(t2);
        }
        ObservableList<DocAdded> docModify = tvDocModify.getItems();//必须放在列与数据绑定之后
        //表中共有2*number列

        for (String string : docManage.result.get(0).keySet()) {
            DocAdded docAdded = new DocAdded(docManage.ModifyNumber);
            for(int i=0;i<docManage.ModifyNumber;i++){
                docAdded.getListkeys().get(i).set(string);
            }
        }*/
        //最终实现需要被批量编辑的文档竖着排成一列
        tcKeys.setCellFactory(TextFieldTableCell.forTableColumn());
        tcKeys.setCellValueFactory(cellData -> cellData.getValue().getKeys());
        tcValues.setCellFactory(TextFieldTableCell.forTableColumn());
        tcValues.setCellValueFactory(cellData -> cellData.getValue().getValues());
        ObservableList<DocAdded> docModifys = tvDocModify.getItems();//必须放在列与数据绑定之后
        for (int j = 0; j < docManage.modifyNumber; j++) {
            int i = docManage.modifyItems.get(j);
            for (String string : docManage.tvDocs.getItems().get(i).keySet()) {
                DocAdded docAdded = new DocAdded();
                docAdded.setKeys(new SimpleStringProperty(string));
                docAdded.setValues(new SimpleStringProperty(docManage.tvDocs.getItems().get(i).get(string).toString()));
                docModifys.add(docAdded);
            }
        }
        btnSaveModify.setOnMouseClicked(event -> {
            //在ES中修改
            Map<String, Map<String, Object>> kvIdDoc = new HashMap<>();
            for (int i = 0; i < docManage.modifyNumber; i++) {
                Map<String, Object> mapModify = new HashMap<>();
                for (int j = i * docManage.tvDocs.getItems().get(i).size(); j < i * docManage.tvDocs.getItems().get(i).size() + docManage.tvDocs.getItems().get(i).size(); j++) {
                    if (docModifys.get(j).getKeys().getValue().equals("the_shape")) {
                        String geoJSON = docModifys.get(j).getValues().getValue();
                        geoJSON = geoJSON.replace("=", ":");//不改变the_shape时，需要将原来值中的等号换为冒号
                        JSONObject jsonObject = new JSONObject(geoJSON);
                        Map<String, Object> map = new HashMap();
                        map.put("coordinates", jsonObject.getJSONArray("coordinates"));
                        map.put("type", jsonObject.get("type").toString());
//                        String geoJSON="{ \"type\": \"Polygon\", \"coordinates\": [ [ [ 117.31902893230101, 35.661252038474515 ], [ 117.31897783372679, 35.661176457654626 ], [ 117.31890896454492, 35.661183484957867 ], [ 117.3188440874531, 35.661194830805385 ], [ 117.31878016903771, 35.661224561492759 ], [ 117.31869554642989, 35.661268044612683 ], [ 117.3186467042482, 35.661311798428088 ], [ 117.31858239732507, 35.661355229387155 ], [ 117.3184933050875, 35.661385386353643 ], [ 117.3184491070065, 35.661393223945552 ], [ 117.31841077520345, 35.66140002102204 ], [ 117.31822310468229, 35.661408437779571 ], [ 117.31785881810605, 35.661427766014022 ], [ 117.31750645384187, 35.661447453077663 ], [ 117.31724820722457, 35.661478403248637 ], [ 117.31706720877298, 35.661498339421883 ], [ 117.31684028824053, 35.661521637261458 ], [ 117.31679930613457, 35.661544096930093 ], [ 117.31677336249096, 35.661572285279796 ], [ 117.3167468873495, 35.661578482508105 ], [ 117.31668192752122, 35.661569635878458 ], [ 117.31657145030759, 35.661554055126288 ], [ 117.31636422223185, 35.661532955236559 ], [ 117.31616049162005, 35.661502109393915 ], [ 117.31600402307879, 35.661472584654753 ], [ 117.31589391278935, 35.661439364600568 ], [ 117.31581643799761, 35.661394133201128 ], [ 117.31571411673862, 35.661320483226298 ], [ 117.31564490941578, 35.661244946473509 ], [ 117.31560262509694, 35.661163931949908 ], [ 117.31557714011352, 35.661087200896667 ], [ 117.31556124100494, 35.660978357752228 ], [ 117.31556162142461, 35.660854329555193 ], [ 117.31557826698354, 35.660710789667704 ], [ 117.31562102345762, 35.660580884400687 ], [ 117.31566917136581, 35.660473347969891 ], [ 117.31572279524349, 35.660409395381926 ], [ 117.31579808918448, 35.660352596900346 ], [ 117.31618262580692, 35.660098122938429 ], [ 117.31634115740037, 35.659983425204686 ], [ 117.31643397733077, 35.65989197044955 ], [ 117.31653171925002, 35.659817086601791 ], [ 117.31663267984086, 35.659772841755753 ], [ 117.31673234810572, 35.659736891356701 ], [ 117.31680463291428, 35.659700652275532 ], [ 117.31684949919271, 35.659663012051347 ], [ 117.31687711827406, 35.659612105028785 ], [ 117.31689897180215, 35.659556164501168 ], [ 117.31691070076262, 35.659507461717226 ], [ 117.31690112568464, 35.659433575219218 ], [ 117.31685595994077, 35.659295962763821 ], [ 117.31681648780511, 35.659145718431148 ], [ 117.31667008177529, 35.659165384807203 ], [ 117.31668956108791, 35.659212411254543 ], [ 117.31670892078864, 35.659303580023582 ], [ 117.3167101978211, 35.659400201382212 ], [ 117.31668661939295, 35.659467323180507 ], [ 117.31662329093089, 35.659534185974714 ], [ 117.31652335916264, 35.659614122213725 ], [ 117.31632543456766, 35.65970043914384 ], [ 117.31611864085907, 35.659784613889059 ], [ 117.31580801142974, 35.659858265668888 ], [ 117.3156372274771, 35.659891871536594 ], [ 117.31555289535218, 35.65989785742542 ], [ 117.31551976432873, 35.659896137922132 ], [ 117.31548658474271, 35.659882882815559 ], [ 117.3154372488316, 35.659960145369304 ], [ 117.3154156417189, 35.65999398505825 ], [ 117.31538563223735, 35.660086937183678 ], [ 117.31539038065033, 35.66022924949651 ], [ 117.3154067402117, 35.660343140535495 ], [ 117.31539875512605, 35.660443753085296 ], [ 117.31538990939266, 35.660484209086256 ], [ 117.31536097640053, 35.660559960778805 ], [ 117.31535217922956, 35.660612217683081 ], [ 117.31531373770399, 35.660717234213443 ], [ 117.31529324574888, 35.660782905405071 ], [ 117.31529193992743, 35.660896839611745 ], [ 117.31531003967703, 35.661004597274612 ], [ 117.31533254520528, 35.66110873876351 ], [ 117.31540829958746, 35.66127475450547 ], [ 117.31551932627967, 35.661425899957287 ], [ 117.31559156072119, 35.661485565475068 ], [ 117.31566769372451, 35.661526472934334 ], [ 117.31577209691589, 35.661569110688689 ], [ 117.31591803259678, 35.661614526448155 ], [ 117.31610377227001, 35.661673875402826 ], [ 117.31621648429839, 35.661695940266618 ], [ 117.3163168513333, 35.661722722074643 ], [ 117.31635449065682, 35.66174642190709 ], [ 117.3164061594039, 35.66177248335898 ], [ 117.31643065603599, 35.661782750918015 ], [ 117.3164567705485, 35.661793696565667 ], [ 117.31650628002339, 35.661820304805445 ], [ 117.31657341081564, 35.661819771506394 ], [ 117.31679013572888, 35.6618960115268 ], [ 117.31696385686381, 35.661932340536147 ], [ 117.31705268919488, 35.66194653093671 ], [ 117.31714236059403, 35.661949906990216 ], [ 117.31725708440861, 35.66192365308099 ], [ 117.31737473282075, 35.661855568107377 ], [ 117.31742887470715, 35.661811441073326 ], [ 117.31747690120298, 35.661782834538663 ], [ 117.31755719717131, 35.661761355130466 ], [ 117.31764328657142, 35.661753173996644 ], [ 117.31770339365799, 35.661762753573917 ], [ 117.31777326378382, 35.6617849263571 ], [ 117.31779911119783, 35.661793981630048 ], [ 117.31787543395878, 35.661820716673724 ], [ 117.3179546336507, 35.661855485360846 ], [ 117.31809244216083, 35.66185801335299 ], [ 117.31816132393314, 35.661853870175285 ], [ 117.31823812513569, 35.661842493750612 ], [ 117.31830738462402, 35.661822485633536 ], [ 117.31836640083557, 35.661781950491374 ], [ 117.318510962359, 35.661707304963514 ], [ 117.31874065820362, 35.661607560255987 ], [ 117.31881598092247, 35.661558331367893 ], [ 117.31885116959683, 35.661523627430022 ], [ 117.31887045645897, 35.661487524147645 ], [ 117.31887853956924, 35.66141178954269 ], [ 117.3188915041992, 35.661341809699231 ], [ 117.31894206048939, 35.661285075069976 ], [ 117.31902893230101, 35.661252038474515 ] ] ] }";
                        mapModify.put(docModifys.get(j).getKeys().getValue(), map);
                    } else
                        mapModify.put(docModifys.get(j).getKeys().getValue(), docModifys.get(j).getValues().getValue());
                }
                kvIdDoc.put(docManage.tvDocs.getItems().get(docManage.modifyItems.get(i)).get("lsid").toString(), mapModify);
                int resultnumber = indexManage.helper.updateFromMap("sdmap", "_doc", kvIdDoc);
                kvIdDoc.clear();
                System.out.println("编辑失败条数" + resultnumber);
            }
            Stage stage = (Stage) btnCancelModify.getScene().getWindow();
            stage.close();
        });
        btnCancelModify.setOnMouseClicked(evnet -> {
            Stage stage = (Stage) btnCancelModify.getScene().getWindow();
            stage.close();
        });
    }
}
