/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package inventory.managment.system;

/**
 *
 * @author REDWAN
 */
public class Laptop extends INVItem{
    private String serialNumber=null;

    private String brand=null;

    private String model=null;

    private String generation=null;

    private condition itemCondition;

    private boolean haveCharger=false; 

    private boolean haveMouse =false;

    public Laptop(String sn, String brand, String model, String gen, condition cond, boolean haveCharger, boolean haveMouse) {
        this.serialNumber=sn;
        this.brand=brand;
        this.model=model;
        this.generation=gen;
        this.itemCondition=cond;
        this.haveCharger=haveCharger;
        this.haveMouse= haveMouse;
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

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    public condition getItemCondition() {
        return itemCondition;
    }

    public void setItemCondition(condition itemCondition) {
        this.itemCondition = itemCondition;
    }

    public boolean isHaveCharger() {
        return haveCharger;
    }

    public void setHaveCharger(boolean haveCharger) {
        this.haveCharger = haveCharger;
    }

    public boolean isHaveMouse() {
        return haveMouse;
    }

    public void setHaveMouse(boolean haveMouse) {
        this.haveMouse = haveMouse;
    }
}
