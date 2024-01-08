package lk.captain.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.captain.bo.custom.AddCustomerBO;
import lk.captain.bo.BOFactory;
import lk.captain.dto.AddCustomerDTO;
import lk.captain.dto.tm.AddCustomerTM;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class AddCustomerController {

    @FXML
    private TableColumn<?, ?> ColId;

    @FXML
    private TableColumn<?, ?> ColName;

    @FXML
    private TableColumn<?, ?> colAdd;

    @FXML
    private TableColumn<?, ?> colTele;

    @FXML
    private Label lblCusId;

    @FXML
    private TableView<AddCustomerTM> tblCustomer;

    @FXML
    private TextField txtCusAdd;

    @FXML
    private TextField txtCusName;

    @FXML
    private TextField txtCusTele;

    @FXML
    private TextField txtsearchId;

    AddCustomerBO addCustomerBO =(AddCustomerBO) BOFactory.getBoFactory().getBOTypes(BOFactory.BOTypes.ADDCUSTOMER);

    public void initialize() throws ClassNotFoundException {
        generateCusId();
        setCellValueFactory();
        loadAllCustomer();
    }

    @FXML
    void btnClerAction(ActionEvent event) {

    }

    @FXML
    void btnDeleteAction(ActionEvent event) throws ClassNotFoundException {
        String deleteId = txtsearchId.getText();

        try {
            boolean isDeleted = addCustomerBO.deleteCus(deleteId);
            if (isDeleted)
                new Alert(Alert.AlertType.CONFIRMATION,deleteId+" Customer is Deleted !").show();
            setCellValueFactory();
            loadAllCustomer();
        }catch (SQLException e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }

    }

    @FXML
    void btnSaveAction(ActionEvent event) throws ClassNotFoundException {
        boolean isValid = Valid();
        if (isValid == true) {
            String cusId = lblCusId.getText();
            String cusName = txtCusName.getText();
            String cusTele = txtCusTele.getText();
            String cusAddress = txtCusAdd.getText();

            try {
                boolean isSaved = addCustomerBO.addCustomer(new AddCustomerDTO(cusId, cusName, cusTele, cusAddress));
                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION, "New Customer added Successfully").show();
                    generateCusId();
                    setCellValueFactory();
                    loadAllCustomer();
                    return;

                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }

        }
    }

    public void generateCusId() throws ClassNotFoundException {
        try {
            String cusId = addCustomerBO.generateCusId();
            lblCusId.setText(cusId);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnUpdateAction(ActionEvent event) throws ClassNotFoundException {
        boolean isValid = Valid();
        if (isValid == true) {
            String cusId = lblCusId.getText();
            String cusName = txtCusName.getText();
            String cusTele = txtCusTele.getText();
            String cusAddress = txtCusAdd.getText();

            var dto = new AddCustomerDTO(cusId, cusName, cusTele, cusAddress);


            try {
                boolean isUpdated = addCustomerBO.updateCustomer(dto);

                if (isUpdated) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Customer Updated Success!").show();
                    setCellValueFactory();
                    loadAllCustomer();
                    loadAllCustomer();


                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }

    }

    @FXML
    void txtSerchOnAction(ActionEvent event) throws ClassNotFoundException {
        String id =txtsearchId .getText();

        try {
            AddCustomerDTO dto = addCustomerBO.searchCusId(id);

            if(dto != null) {
                fillFields(dto);
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Tea Collector is not found!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void fillFields(AddCustomerDTO dto) {
        lblCusId.setText(dto.getCusId());
        txtCusName.setText(dto.getCusName());
        txtCusAdd.setText(dto.getCusAddress());
        txtCusTele.setText(dto.getCusTele());
    }

    private void loadAllCustomer() throws ClassNotFoundException {
        tblCustomer.getItems().clear();
        try {
            ArrayList<AddCustomerDTO> dtoList = addCustomerBO.getAllCus();

                for(AddCustomerDTO dto : dtoList) {
                tblCustomer.getItems().add(
                        new AddCustomerTM(
                                dto.getCusId(),
                                dto.getCusName(),
                                dto.getCusTele(),
                                dto.getCusAddress()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void setCellValueFactory() {
        ColId.setCellValueFactory(new PropertyValueFactory<>("cusId"));
        ColName.setCellValueFactory(new PropertyValueFactory<>("cusName"));
        colTele.setCellValueFactory(new PropertyValueFactory<>("cusTele"));
        colAdd.setCellValueFactory(new PropertyValueFactory<>("cusAddress"));

    }
    private boolean Valid() {
        String name = txtCusName.getText();
        String tele = txtCusTele.getText();
        String add = txtCusAdd.getText();

        boolean isCusName = Pattern.matches("([A-Z][a-z\\s]{3,}|[a-z])", name);
        boolean isCusAdd = Pattern.matches("^([\\w\\s]+)\\s([\\w\\s]+)$", add);
        boolean isCusTele = Pattern.matches("^\\+?\\d{3}[-\\s]?\\(?\\d{3}\\)?[-\\s]?\\d{3}[-\\s]?\\d", tele);


        if (!isCusName) {
            new Alert(Alert.AlertType.ERROR, "Invalid Name").show();
            return false;
        }
        if (!isCusAdd) {
            new Alert(Alert.AlertType.ERROR, "Invalid Address").show();
            return false;
        }
        if (!isCusTele) {
            new Alert(Alert.AlertType.ERROR, "Invalid Telephone").show();
            return false;
        }
        return true;

    }

}
