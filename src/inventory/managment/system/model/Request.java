package inventory.managment.system.model;

import java.sql.Timestamp;

/**
 * Represents a request in the system, including details from temporary tables.
 */
public class Request {
    private int requestId;
    private String requestType;
    private String itemType;
    private String itemId;
    private String status;
    private Timestamp requestDate;
    private String username;
    private int userId;

    // Temp data fields (for Laptop)
    private String tempSerialNumber;
    private String tempBrand;
    private String tempGeneration;
    private String tempModel;
    private String tempCondition;
    private Boolean tempHaveCharger;
    private Boolean tempHaveMouse;
    private Integer tempOriginalItemId;

    // Temp data fields (for Desktop)
    private String tempTowerSerialNumber;
    private String tempTowerGeneration;
    private String tempTowerModel;
    private String tempDesktopCondition;
    private String tempKeyboardModel;
    private Boolean tempDesktopHaveMouse;

    // Temp data fields (for Accessory)
    private String tempAccessoryType;
    private String tempAccessorySerialNumber;
    private String tempAccessoryBrand;
    private String tempAccessoryModel;
    private String tempAccessoryCondition;

    public Request() {}

    // Getters and Setters
    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getRequestDate() { return requestDate; }
    public void setRequestDate(Timestamp requestDate) { this.requestDate = requestDate; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTempSerialNumber() { return tempSerialNumber; }
    public void setTempSerialNumber(String tempSerialNumber) { this.tempSerialNumber = tempSerialNumber; }

    public String getTempBrand() { return tempBrand; }
    public void setTempBrand(String tempBrand) { this.tempBrand = tempBrand; }

    public String getTempGeneration() { return tempGeneration; }
    public void setTempGeneration(String tempGeneration) { this.tempGeneration = tempGeneration; }

    public String getTempModel() { return tempModel; }
    public void setTempModel(String tempModel) { this.tempModel = tempModel; }

    public String getTempCondition() { return tempCondition; }
    public void setTempCondition(String tempCondition) { this.tempCondition = tempCondition; }

    public Boolean getTempHaveCharger() { return tempHaveCharger; }
    public void setTempHaveCharger(Boolean tempHaveCharger) { this.tempHaveCharger = tempHaveCharger; }

    public Boolean getTempHaveMouse() { return tempHaveMouse; }
    public void setTempHaveMouse(Boolean tempHaveMouse) { this.tempHaveMouse = tempHaveMouse; }

    public Integer getTempOriginalItemId() { return tempOriginalItemId; }
    public void setTempOriginalItemId(Integer tempOriginalItemId) { this.tempOriginalItemId = tempOriginalItemId; }

    public String getTempTowerSerialNumber() { return tempTowerSerialNumber; }
    public void setTempTowerSerialNumber(String tempTowerSerialNumber) { this.tempTowerSerialNumber = tempTowerSerialNumber; }

    public String getTempTowerGeneration() { return tempTowerGeneration; }
    public void setTempTowerGeneration(String tempTowerGeneration) { this.tempTowerGeneration = tempTowerGeneration; }

    public String getTempTowerModel() { return tempTowerModel; }
    public void setTempTowerModel(String tempTowerModel) { this.tempTowerModel = tempTowerModel; }

    public String getTempDesktopCondition() { return tempDesktopCondition; }
    public void setTempDesktopCondition(String tempDesktopCondition) { this.tempDesktopCondition = tempDesktopCondition; }

    public String getTempKeyboardModel() { return tempKeyboardModel; }
    public void setTempKeyboardModel(String tempKeyboardModel) { this.tempKeyboardModel = tempKeyboardModel; }

    public Boolean getTempDesktopHaveMouse() { return tempDesktopHaveMouse; }
    public void setTempDesktopHaveMouse(Boolean tempDesktopHaveMouse) { this.tempDesktopHaveMouse = tempDesktopHaveMouse; }

    public String getTempAccessoryType() { return tempAccessoryType; }
    public void setTempAccessoryType(String tempAccessoryType) { this.tempAccessoryType = tempAccessoryType; }

    public String getTempAccessorySerialNumber() { return tempAccessorySerialNumber; }
    public void setTempAccessorySerialNumber(String tempAccessorySerialNumber) { this.tempAccessorySerialNumber = tempAccessorySerialNumber; }

    public String getTempAccessoryBrand() { return tempAccessoryBrand; }
    public void setTempAccessoryBrand(String tempAccessoryBrand) { this.tempAccessoryBrand = tempAccessoryBrand; }

    public String getTempAccessoryModel() { return tempAccessoryModel; }
    public void setTempAccessoryModel(String tempAccessoryModel) { this.tempAccessoryModel = tempAccessoryModel; }

    public String getTempAccessoryCondition() { return tempAccessoryCondition; }
    public void setTempAccessoryCondition(String tempAccessoryCondition) { this.tempAccessoryCondition = tempAccessoryCondition; }
}
