package inventory.managment.system.model;

import javafx.scene.control.Button;

/**
 * Class to represent an item in the inventory display table.
 */
public class InventoryDisplayItem {
    private String itemType;
    private String serialNumber;
    private String brand;
    private String model;
    private String condition;
    private String addDate;
    private Button editBtn;
    private Button deleteBtn;

    public InventoryDisplayItem(String itemType, String serialNumber, String brand, String model, String condition, String addDate) {
        this.itemType = itemType;
        this.serialNumber = serialNumber;
        this.brand = brand;
        this.model = model;
        this.condition = condition;
        this.addDate = addDate;
        this.editBtn = null;
        this.deleteBtn = null;
    }

    // Getters and setters
    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public Button getEditBtn() {
        return editBtn;
    }

    public void setEditBtn(Button editBtn) {
        this.editBtn = editBtn;
    }

    public Button getDeleteBtn() {
        return deleteBtn;
    }

    public void setDeleteBtn(Button deleteBtn) {
        this.deleteBtn = deleteBtn;
    }

    // For backward compatibility, perhaps add getDescription
    public String getDescription() {
        return itemType + ": " + brand + " " + model + " (SN: " + serialNumber + ") - " + condition;
    }
}