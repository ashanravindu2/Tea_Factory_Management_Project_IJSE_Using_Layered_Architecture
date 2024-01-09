package lk.captain.Controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lk.captain.QrGenerate.QrcodeForUser;
import lk.captain.bo.BOFactory;
import lk.captain.bo.custom.AddCustomerBO;
import lk.captain.bo.custom.TeaCollectorBO;
import lk.captain.dao.custom.TeaCollectorDAO;
import lk.captain.dto.SupplierManageDTO;
import lk.captain.dto.TeaCollctorDTO;
import lk.captain.dto.WorkerManageDTO;
import lk.captain.dto.tm.TeaCollectorTM;
import lk.captain.dto.tm.TeaSupplierManageTM;
import lk.captain.entity.TeaCollector;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class TeaCollectorManageController {

    @FXML
    private JFXComboBox<String> cmbGender;


    @FXML
    private AnchorPane root;

    @FXML
    private TextField txtColceName;

    @FXML
    private TextField txtColecAdd;

    @FXML
    private Label lbltxtColecId;

    @FXML
    private TextField txtColecTele;

    @FXML
    private TableView<TeaCollectorTM> tblTeaColec;

    @FXML
    private TableColumn<?, ?> coladd;

    @FXML
    private TableColumn<?, ?> colgen;

    @FXML
    private TableColumn<?, ?> colid;

    @FXML
    private TableColumn<?, ?> colname;

    @FXML
    private TableColumn<?, ?> coltele;

    @FXML
    private TextField txtsearchId;


    private QrcodeForUser qrcodeForUser = new QrcodeForUser();
    TeaCollectorBO teaCollectorBO = (TeaCollectorBO) BOFactory.getBoFactory().getBOTypes(BOFactory.BOTypes.TEACOLLECTOR);
    public void initialize(){
        try {
            gender();
            generateColecId();
            setCellValueFactory();
            loadAllTeaColector();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    private void setCellValueFactory() {
        colid.setCellValueFactory(new PropertyValueFactory<>("teaColecId"));
        colname.setCellValueFactory(new PropertyValueFactory<>("Name"));
        coladd.setCellValueFactory(new PropertyValueFactory<>("Address"));
        coltele.setCellValueFactory(new PropertyValueFactory<>("Telephone"));
        colgen.setCellValueFactory(new PropertyValueFactory<>("Gender"));
    }

    @FXML
    void btnColecSaveOnAction(ActionEvent event) throws ClassNotFoundException {
        boolean isValid = Valid();
        if (isValid == true) {
            String teaColecId = lbltxtColecId.getText();
            String Name = txtColceName.getText();
            String Address = txtColecAdd.getText();
            String Telephone = txtColecTele.getText();
            String Gender = cmbGender.getValue();

            try {
                boolean isSaved =teaCollectorBO.save(new TeaCollctorDTO(teaColecId, Name, Address, Telephone, Gender));
                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Tea Collector added Successfully").show();
                    qrcodeForUser.CreateQr(teaColecId);
                    generateColecId();
                    setCellValueFactory();
                    loadAllTeaColector();
                    return;


                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }
    private void gender(){
        ObservableList<String> obList = FXCollections.observableArrayList();
        obList.addAll("Male","Female");


        try {
            cmbGender.setItems(obList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void generateColecId() throws ClassNotFoundException {
        try {
            ResultSet resultSet = teaCollectorBO.generateColecId();
            boolean isExist = resultSet.next();

            if(isExist){
                String oldSupId = resultSet.getString(1);
                oldSupId =oldSupId.substring(1,oldSupId.length());
                int intId =Integer.parseInt(oldSupId);
                intId =intId+1;

                if (intId <10){
                    lbltxtColecId.setText("K00" +intId);
                } else if (intId <100) {
                    lbltxtColecId.setText( "K0"+intId);

                }else {
                    lbltxtColecId.setText("S"+intId);
                }
            }else {
                lbltxtColecId.setText("K001");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    private void loadAllTeaColector() throws ClassNotFoundException {
        ObservableList<TeaCollectorTM> obList = FXCollections.observableArrayList();

        try {
            List<TeaCollctorDTO> dtoList = teaCollectorBO.getAll();

            for(TeaCollctorDTO dto : dtoList) {
                obList.add(
                        new TeaCollectorTM(
                                dto.getTeaColecId(),
                                dto.getName(),
                                dto.getAddress(),
                                dto.getTelephone(),
                                dto.getGender()

                        )
                );
            }

            tblTeaColec.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void btnDeleteOnAction(ActionEvent event) throws ClassNotFoundException {
        String deleteId = txtsearchId.getText();

        try {
            boolean isDeleted = teaCollectorBO.delete(deleteId);
            if (isDeleted)
                new Alert(Alert.AlertType.CONFIRMATION,deleteId+" "+deleteId+" "+"Tea Collector is Deleted !").show();
            loadAllTeaColector();
            setCellValueFactory();
            generateColecId();
        }catch (SQLException e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }

    }

    @FXML
    void btnUpdateOnaction(ActionEvent event) throws ClassNotFoundException {
        boolean isValid = Valid();
        if (isValid == true) {
            String teaColecId = lbltxtColecId.getText();
            String Name = txtColceName.getText();
            String Address = txtColecAdd.getText();
            String Telephone = txtColecTele.getText();
            String Gender = cmbGender.getValue();

            var dto = new TeaCollctorDTO(teaColecId, Name, Address, Telephone, Gender);


            try {
                boolean isUpdated = teaCollectorBO.update(dto);
                System.out.println(isUpdated);
                if (isUpdated) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Tea Collector Updated Success!").show();
                    loadAllTeaColector();
                    setCellValueFactory();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }

        }
    }

    @FXML
    void txtSearchIdOnAction(ActionEvent event) {
        String id =txtsearchId .getText();

        try {
            TeaCollctorDTO dto = null;
            try {
                dto = teaCollectorBO.searchTeaColecId(id);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            if(dto != null) {
                fillFields(dto);
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Tea Collector is not found!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }


    }
    private void fillFields(TeaCollctorDTO dto) {
        lbltxtColecId.setText(dto.getTeaColecId());
        txtColceName.setText(dto.getName());
        txtColecAdd.setText(dto.getAddress());
        txtColecTele.setText(dto.getTelephone());
        cmbGender.setValue(dto.getGender());


    }

    @FXML
    void btnClearOnAction(ActionEvent event) throws ClassNotFoundException {
        txtColecTele.setText("");
        txtColecAdd.setText("");
        txtColceName.setText("");
        txtsearchId.setText("");
        cmbGender.setValue("");
        generateColecId();

    }
    private boolean Valid() {
        String id = txtColceName.getText();
        String add = txtColecAdd.getText();
        String tele = txtColecTele.getText();

        boolean isSupName = Pattern.matches("([A-Z][a-z\\s]{3,}|[a-z])", id);
        boolean issuppAddres = Pattern.matches("^([\\w\\s]+)\\s([\\w\\s]+)$", add);
        boolean issuppTele = Pattern.matches("^\\+?\\d{3}[-\\s]?\\(?\\d{3}\\)?[-\\s]?\\d{3}[-\\s]?\\d", tele);


        if (!isSupName) {
            new Alert(Alert.AlertType.ERROR, "Invalid Name").show();
            return false;
        }
        if (!issuppAddres) {
            new Alert(Alert.AlertType.ERROR, "Invalid Address").show();
            return false;
        }
        if (!issuppTele) {
            new Alert(Alert.AlertType.ERROR, "Invalid Telephone").show();
            return false;
        }
        return true;

    }


}
