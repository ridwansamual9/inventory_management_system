/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package inventory.managment.system;

/**
 *
 * @author REDWAN
 */
public class Desktop extends INVItem {
    private String towerSerialNumber;
    private String towerGeneration;
    private String towerModel;
    private condition desktopCondition;
    private String keyboardModel;
    private boolean haveMouse;

   public Desktop(String towerSerialNumber, String towerGeneration, String towerModel, condition desktopCondition, String keyboardModel, boolean haveMouse){
       this.towerSerialNumber = towerSerialNumber;
       this.towerGeneration = towerGeneration;
       this.towerModel = towerModel;
       this.desktopCondition = desktopCondition;
       this.keyboardModel = keyboardModel;
       this.haveMouse = haveMouse;
   }
    public String getTowerSerialNumber() {
        return towerSerialNumber;
    }

    public void setTowerSerialNumber(String towerSerialNumber) {
        this.towerSerialNumber = towerSerialNumber;
    }

    public String getTowerGeneration() {
        return towerGeneration;
    }

    public void setTowerGeneration(String towerGeneration) {
        this.towerGeneration = towerGeneration;
    }

    public String getTowerModel() {
        return towerModel;
    }

    public void setTowerModel(String towerModel) {
        this.towerModel = towerModel;
    }

    public condition getDesktopCondition() {
        return desktopCondition;
    }

    public void setDesktopCondition(condition desktopCondition) {
        this.desktopCondition = desktopCondition;
    }

    public String getKeyboardModel() {
        return keyboardModel;
    }

    public void setKeyboardModel(String keyboardModel) {
        this.keyboardModel = keyboardModel;
    }

    public boolean isHaveMouse() {
        return haveMouse;
    }

    public void setHaveMouse(boolean haveMouse) {
        this.haveMouse = haveMouse;
    }
    
    
}
