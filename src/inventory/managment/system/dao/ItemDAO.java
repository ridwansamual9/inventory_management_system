package inventory.managment.system.dao;

import inventory.managment.system.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ItemDAO handles database operations for Laptops, Desktops, and Accessories.
 */
public class ItemDAO extends BaseDAO {

    public List<InventoryDisplayItem> getAllItems() throws SQLException {
        List<InventoryDisplayItem> items = new ArrayList<>();
        String laptopQuery = "SELECT 'Laptop' AS item_type, serial_number, brand, CONCAT(generation, ' ', model) AS model, `condition`, created_in_date FROM Laptop";
        String desktopQuery = "SELECT 'Desktop' AS item_type, tower_serial_number AS serial_number, '' AS brand, CONCAT(tower_generation, ' ', tower_model) AS model, desktop_condition AS `condition`, created_in_date FROM Desktop";
        String accessoryQuery = "SELECT type AS item_type, serial_number, brand, model, `condition`, created_in_date FROM Accessory";
        
        try (Connection con = getConnection()) {
            // Laptops
            try (PreparedStatement ps = con.prepareStatement(laptopQuery);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToDisplayItem(rs));
                }
            }
            // Desktops
            try (PreparedStatement ps = con.prepareStatement(desktopQuery);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToDisplayItem(rs));
                }
            }
            // Accessories
            try (PreparedStatement ps = con.prepareStatement(accessoryQuery);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToDisplayItem(rs));
                }
            }
        }
        return items;
    }

    public List<InventoryDisplayItem> getFilteredItems(String itemType, String brand, String condition) throws SQLException {
        List<InventoryDisplayItem> items = new ArrayList<>();
        
        if (itemType == null || "Laptop".equalsIgnoreCase(itemType)) {
            items.addAll(getFilteredLaptops(brand, condition));
        }
        if (itemType == null || "Desktop".equalsIgnoreCase(itemType)) {
            items.addAll(getFilteredDesktops(brand, condition));
        }
        if (itemType == null || "Accessory".equalsIgnoreCase(itemType)) {
            items.addAll(getFilteredAccessories(brand, condition));
        }
        
        return items;
    }

    private List<InventoryDisplayItem> getFilteredLaptops(String brand, String condition) throws SQLException {
        List<InventoryDisplayItem> items = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT 'Laptop' AS item_type, serial_number, brand, CONCAT(generation, ' ', model) AS model, `condition`, created_in_date FROM Laptop WHERE 1=1");
        if (brand != null) query.append(" AND brand = ?");
        if (condition != null) query.append(" AND `condition` = ?");
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query.toString())) {
            int paramIdx = 1;
            if (brand != null) ps.setString(paramIdx++, brand);
            if (condition != null) ps.setString(paramIdx++, condition);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToDisplayItem(rs));
                }
            }
        }
        return items;
    }

    private List<InventoryDisplayItem> getFilteredDesktops(String brand, String condition) throws SQLException {
        List<InventoryDisplayItem> items = new ArrayList<>();
        // Desktop doesn't have a brand column in current schema, matching legacy logic
        StringBuilder query = new StringBuilder("SELECT 'Desktop' AS item_type, tower_serial_number AS serial_number, '' AS brand, CONCAT(tower_generation, ' ', tower_model) AS model, desktop_condition AS `condition`, created_in_date FROM Desktop WHERE 1=1");
        if (condition != null) query.append(" AND desktop_condition = ?");
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query.toString())) {
            int paramIdx = 1;
            if (condition != null) ps.setString(paramIdx++, condition);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToDisplayItem(rs));
                }
            }
        }
        return items;
    }

    private List<InventoryDisplayItem> getFilteredAccessories(String brand, String condition) throws SQLException {
        List<InventoryDisplayItem> items = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT type AS item_type, serial_number, brand, model, `condition`, created_in_date FROM Accessory WHERE 1=1");
        if (brand != null) query.append(" AND brand = ?");
        if (condition != null) query.append(" AND `condition` = ?");
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query.toString())) {
            int paramIdx = 1;
            if (brand != null) ps.setString(paramIdx++, brand);
            if (condition != null) ps.setString(paramIdx++, condition);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToDisplayItem(rs));
                }
            }
        }
        return items;
    }

    public List<String> getAllBrands(String itemType) throws SQLException {
        List<String> brands = new ArrayList<>();
        String query;
        if ("Laptop".equalsIgnoreCase(itemType)) {
            query = "SELECT DISTINCT brand FROM Laptop WHERE brand IS NOT NULL AND brand != '' ORDER BY brand";
        } else if ("Desktop".equalsIgnoreCase(itemType)) {
            // Desktop doesn't have brand column
            return brands;
        } else if ("Accessory".equalsIgnoreCase(itemType)) {
            query = "SELECT DISTINCT brand FROM Accessory WHERE brand IS NOT NULL AND brand != '' ORDER BY brand";
        } else {
            query = "SELECT brand FROM (SELECT brand FROM Laptop UNION SELECT brand FROM Accessory) AS all_brands WHERE brand IS NOT NULL AND brand != '' GROUP BY brand ORDER BY brand";
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                brands.add(rs.getString("brand"));
            }
        }
        return brands;
    }

    public Laptop getLaptopBySerial(String serialNumber) throws SQLException {
        String query = "SELECT * FROM Laptop WHERE serial_number = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, serialNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Laptop laptop = new Laptop(
                        rs.getString("serial_number"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getString("generation"),
                        INVItem.condition.valueOf(rs.getString("condition")),
                        rs.getBoolean("have_charger"),
                        rs.getBoolean("have_mouse")
                    );
                    laptop.setItemId(rs.getInt("id"));
                    return laptop;
                }
            }
        }
        return null;
    }

    public Desktop getDesktopBySerial(String serialNumber) throws SQLException {
        String query = "SELECT * FROM Desktop WHERE tower_serial_number = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, serialNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Desktop desktop = new Desktop(
                        rs.getString("tower_serial_number"),
                        rs.getString("tower_generation"),
                        rs.getString("tower_model"),
                        INVItem.condition.valueOf(rs.getString("desktop_condition")),
                        rs.getString("keyboard_model"),
                        rs.getBoolean("have_mouse")
                    );
                    desktop.setItemId(rs.getInt("id"));
                    return desktop;
                }
            }
        }
        return null;
    }

    public Accessory getAccessoryBySerial(String serialNumber) throws SQLException {
        String query = "SELECT * FROM Accessory WHERE serial_number = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, serialNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Accessory accessory = new Accessory(
                        Accessory.type.valueOf(rs.getString("type")),
                        rs.getString("brand"),
                        rs.getString("serial_number"),
                        rs.getString("model"),
                        INVItem.condition.valueOf(rs.getString("condition"))
                    );
                    accessory.setItemId(rs.getInt("id"));
                    return accessory;
                }
            }
        }
        return null;
    }

    public int getTotalItemsCount() throws SQLException {
        String query = "SELECT (SELECT COUNT(*) FROM Laptop) + (SELECT COUNT(*) FROM Desktop) + (SELECT COUNT(*) FROM Accessory) AS total";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    public int getLowStockCount(int threshold) throws SQLException {
        String query = "SELECT (SELECT COUNT(*) FROM Laptop) + (SELECT COUNT(*) FROM Desktop) AS total";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int total = rs.getInt("total");
                return total < threshold ? total : 0;
            }
        }
        return 0;
    }

    public int getLaptopCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Laptop";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getDesktopCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Desktop";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getAccessoryCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Accessory";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private InventoryDisplayItem mapResultSetToDisplayItem(ResultSet rs) throws SQLException {
        return new InventoryDisplayItem(
            rs.getString("item_type"),
            rs.getString("serial_number"),
            rs.getString("brand"),
            rs.getString("model"),
            rs.getString("condition"),
            rs.getString("created_in_date")
        );
    }
}
