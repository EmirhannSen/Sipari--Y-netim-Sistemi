package dao;

import core.Database;
import entity.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerDao {
    private Connection connection;

    public CustomerDao() {
        this.connection = Database.getInstance();
    }

    public ArrayList<Customer> findAll() {
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            ResultSet rs = this.connection.createStatement().executeQuery("SELECT * FROM customer");
            while (rs.next()){
                customers.add(this.match(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customers;
    }

    public boolean save(Customer customer){
        String query = "INSERT INTO  customer (name,type,phone,mail,address) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setString(1,customer.getName());
            pr.setString(2,customer.getType().toString());
            pr.setString(3,customer.getPhone());
            pr.setString(4,customer.getMail());
            pr.setString(5,customer.getAddress());
            return pr.executeUpdate() != -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Customer getById(int id){
        Customer customer = null;
        String query = "SELECT * FROM customer WHERE id  = ?";
        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setInt(1, id);
            ResultSet rs = pr.executeQuery();
            if (rs.next()){
                customer = this.match(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customer;
    }

    public boolean update(Customer customer){
        String query = "UPDATE customer SET " +
                "name = ?, " +
                "type = ?, " +
                "phone = ?, " +
                "mail = ?, " +
                "address = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setString(1, customer.getName());
            pr.setString(2, customer.getType().toString());
            pr.setString(3, customer.getPhone());
            pr.setString(4, customer.getMail());
            pr.setString(5, customer.getAddress());
            pr.setInt(6, customer.getId());
            return pr.executeUpdate() != -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(int id){
        String query = "DELETE FROM customer WHERE id = ?";
        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setInt(1, id);
            return pr.executeUpdate() != -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Yeni filtreleme metodu
    public ArrayList<Customer> filter(String name, Customer.TYPE type) {
        String sql = "SELECT * FROM customer WHERE 1=1";

        if (name != null && !name.isEmpty()) {
            sql += " AND name LIKE ?";
        }
        if (type != null) {
            sql += " AND type = ?";
        }

        ArrayList<Customer> customers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int parameterIndex = 1;
            if (name != null && !name.isEmpty()) {
                stmt.setString(parameterIndex++, "%" + name + "%");
            }
            if (type != null) {
                stmt.setString(parameterIndex++, type.name()); // Enum değeri string olarak ekleniyor
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                customers.add(this.match(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return customers;
    }

    public ArrayList<Customer> query(String query){
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            ResultSet rs = this.connection.createStatement().executeQuery(query);
            while (rs.next()){
                customers.add(this.match(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customers;
    }


    public Customer match(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setName(rs.getString("name"));
        customer.setMail(rs.getString("mail"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));
        customer.setType(Customer.TYPE.valueOf(rs.getString("type")));
        return customer;
    }
}
