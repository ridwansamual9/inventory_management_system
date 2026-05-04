/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package inventory.managment.system.model;

/**
 *
 * @author REDWAN
 */
public class Accessory extends INVItem {
    public enum type {MONITOR, TOWER, KEYBOARD, MOUSE};
    private type accessoryType;
    private String brand=null;
    private String serialNumber=null;
    private String model=null;
    private condition itemCondition;

    public Accessory(type accessoryType, String brand, String serialNumber, String model, condition itemCondition){
        this.accessoryType = accessoryType;
        this.brand=brand;
        this.serialNumber=serialNumber;
        this.model=model;
        this.itemCondition=itemCondition;
    }
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public condition getItemCondition() {
        return itemCondition;
    }

    public void setItemCondition(condition itemCondition) {
        this.itemCondition = itemCondition;
    }

    public type getType() {
        return accessoryType;
    }

    public void setType(type accessoryType) {
        this.accessoryType = accessoryType;
    }


}
