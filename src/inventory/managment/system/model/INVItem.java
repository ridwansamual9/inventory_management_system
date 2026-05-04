/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package inventory.managment.system.model;
import java.util.Date;
/**
 *
 * @author REDWAN
 */
public class INVItem {
    private int itemId;
    public enum condition {NEW, GOOD, FAIR, POOR};
    private Date createdDate;

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    public void setItemId(int id){
    this.itemId=id;
    }

    public int getItemId() {
        return itemId;
    }
}