package sample;
import com.jfoenix.controls.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.EnumSet;
import java.util.EventListener;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML private JFXListView<Employee> employeeListView;
    @FXML private JFXTextField firstNameTextField;
    @FXML private JFXTextField lastNameTextField;
    @FXML private JFXCheckBox isActiveCheckBox;
    @FXML private JFXTextField miscTextField;
    @FXML private JFXButton clearButton;
    @FXML private JFXButton addButton;
    @FXML private JFXButton deleteButton;
    @FXML private Label errorTextField;
    @FXML private HBox departmentContainer;
    @FXML private HBox accessLevelContainer;
    @FXML private JFXComboBox departmentSelection;
    @FXML private JFXComboBox accessLevelDropDown;
    @FXML private JFXRadioButton staffRadioButton;
    @FXML private JFXRadioButton facultyRadioButton;
    private ToggleGroup radioGroup;

    public void clearFields() {
        firstNameTextField.clear();
        lastNameTextField.clear();
        isActiveCheckBox.setSelected(false);
        departmentContainer.setVisible(false);
        accessLevelContainer.setVisible(false);
        departmentSelection.setValue(null);
        accessLevelDropDown.setValue(null);
        radioGroup.selectToggle(null);
    }

    public void clearFontColors() {
        firstNameTextField.setStyle("-fx-text-fill: #f2f2f2");
        firstNameTextField.setStyle("-fx-prompt-text-fill: #737373");
        lastNameTextField.setStyle("-fx-text-fill: #f2f2f2");
        lastNameTextField.setStyle("-fx-prompt-text-fill: #737373");
    }

    public boolean checkFName() {
        if (!firstNameTextField.getText().matches("^[a-zA-Z]+$")) {
            firstNameTextField.setStyle("-jfx-unfocus-color: #ff5050");
            return false;
        } else {
            return true;
        }
    }

    public boolean checkLName() {
        if (!lastNameTextField.getText().matches("^[a-zA-Z]+$")) {
            lastNameTextField.setStyle("-jfx-unfocus-color: #ff5050");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Arrays to populate Combo/Choice Boxes
        Integer[] accessLevels = {1,2,3,4,5,6,7,8,9,10};
        String[] departments = {"IT", "Maintenance", "Accounting", "Marketing", "HR"};
        accessLevelDropDown.setItems(FXCollections.observableArrayList(accessLevels));
        departmentSelection.setItems(FXCollections.observableArrayList(departments));

        errorTextField.setVisible(false); // hiding error message
        departmentContainer.setVisible(false); // hiding optional selection
        accessLevelContainer.setVisible(false); // ^^^

        // Setting up radio button group
        radioGroup = new ToggleGroup();
        staffRadioButton.setToggleGroup(radioGroup);
        facultyRadioButton.setToggleGroup(radioGroup);
        staffRadioButton.addEventHandler(KeyEvent.ANY, event -> {
            if (event.getCode().isArrowKey()) {
                departmentSelection.setValue(null);
                accessLevelDropDown.setValue(null);
                departmentContainer.setVisible(false);
                accessLevelContainer.setVisible(true);
            }
        });
        facultyRadioButton.addEventHandler(KeyEvent.ANY, event -> {
            if (event.getCode().isArrowKey()) {
                departmentSelection.setValue(null);
                accessLevelDropDown.setValue(null);
                accessLevelContainer.setVisible(false);
                departmentContainer.setVisible(true);
            }
        });

        firstNameTextField.addEventHandler(KeyEvent.ANY, event -> {
            if (event.getCode().isArrowKey()) {
                departmentSelection.setValue(null);
                accessLevelDropDown.setValue(null);
                accessLevelContainer.setVisible(false);
                departmentContainer.setVisible(true);
            }
        });

        // Creating & Populating List with Generic Employees
        ObservableList<Employee> items = employeeListView.getItems();
        for (int i = 0; i < 5; i++) {
            Employee employee = new Employee();
            employee.firstName = "Employee";
            employee.lastName = "" + (i + 1);
            employee.hire();
            items.add(employee);
        }

        // Method to clear fields
        clearButton.setOnAction((actionEvent -> {
            clearFields();
            clearFontColors();
        }));

        firstNameTextField.setOnMouseClicked((actionEvent -> {
            firstNameTextField.setStyle("-jfx-unfocus-color: #f2f2f2");
        }));

        lastNameTextField.setOnMouseClicked((actionEvent -> {
            lastNameTextField.setStyle("-jfx-unfocus-color: #f2f2f2");
        }));

        // Method to populate fields with selected employee
        employeeListView.getSelectionModel().selectedItemProperty().addListener((
                ObservableValue<? extends Worker> ov, Worker old_val, Worker new_val)
                -> {
            clearFields();
            if (!items.isEmpty()) {
                deleteButton.setDisable(false);
                errorTextField.setVisible(false);
                Worker selectedItem = employeeListView.getSelectionModel().getSelectedItem();
                if (selectedItem.getClass().toString().equals("class sample.Staff")) {
                    staffRadioButton.setSelected(true);
                    accessLevelContainer.setVisible(true);
                    accessLevelDropDown.setValue(((Staff) selectedItem).accessLevel);
                } else if(selectedItem.getClass().toString().equals("class sample.Faculty")) {
                    facultyRadioButton.setSelected(true);
                    departmentContainer.setVisible(true);
                    departmentSelection.setValue(((Faculty) selectedItem).department);
                }
                firstNameTextField.setText(((Employee) selectedItem).firstName);
                lastNameTextField.setText(((Employee) selectedItem).lastName);
                isActiveCheckBox.setSelected(((Employee) selectedItem).isActive);
            } else {
                deleteButton.setDisable(true);
            }
        });

        // Method to delete selected employee
        deleteButton.setOnAction((actionEvent -> {
            errorTextField.setVisible(false);
            try {
                items.remove(employeeListView.getSelectionModel().getSelectedItem());
            } catch (NullPointerException e) {
                clearFields();
            }
        }));

        // Method to display department container
        staffRadioButton.setOnAction((actionEvent -> {
            departmentContainer.setVisible(false);
            departmentSelection.setValue(null);
            accessLevelContainer.setVisible(true);
        }));

        // Method to display access level drop down
        facultyRadioButton.setOnAction((actionEvent -> {
            accessLevelContainer.setVisible(false);
            accessLevelDropDown.setValue(null);
            departmentContainer.setVisible(true);
        }));

        // Method to add employee
        addButton.setOnAction((actionEvent -> {
            // first/last name Validation
            boolean checkFName = checkFName();
            boolean checkLName = checkLName();
            if (checkFName && checkLName) {
                Employee newEmp;
                if (staffRadioButton.isSelected()) {
                    newEmp = new Staff();
                    ((Staff) newEmp).accessLevel = (int) accessLevelDropDown.getValue();
                } else if (facultyRadioButton.isSelected()) {
                    newEmp = new Faculty();
                    ((Faculty) newEmp).department = (String) departmentSelection.getValue();
                } else {
                    newEmp = new Employee();
                }
                newEmp.firstName = firstNameTextField.getText();
                newEmp.lastName = lastNameTextField.getText();
                newEmp.isActive = isActiveCheckBox.isSelected();
                items.add(newEmp);
                errorTextField.setVisible(false);
                clearFields();
                clearFontColors();
            }
        }));
    }
}