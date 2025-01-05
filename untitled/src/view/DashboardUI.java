package view;

import business.BasketController;
import business.CustomerController;
import business.ProductController;
import core.Helper;
import core.Item;
import entity.Basket;
import entity.Customer;
import entity.Product;
import entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DashboardUI extends JFrame {
    private JPanel container;
    private JLabel lbl_welcome;
    private JButton btn_logout;
    private JTabbedPane pnl_basket;
    private JPanel pnl_customer;
    private JScrollPane scrl_customer;
    private JTable tbl_customer;
    private JPanel pnl_customer_filter;
    private JTextField fld_f_customer_name;
    private JComboBox<Customer.TYPE> cmb_f_customer_type;
    private JButton btn_customer_filter;
    private JButton btn_customer_filter_reset;
    private JButton btn_customer_new;
    private JLabel lbl_filt_customer_name;
    private JLabel lbl_f_customer_type;
    private JPanel pnl_product;
    private JScrollPane scrl_product;
    private JTable tbl_product;
    private JPanel pnl_product_filter;
    private JTextField fld_f_product_name;
    private JTextField fld_f_product_code;
    private JComboBox<Item> cmb_f_product_stok;
    private JButton btn_product_filter;
    private JButton btn_product_filter_reset;
    private JButton btn_product_new;
    private JLabel lbl_f_product_name;
    private JLabel lbl_f_product_code;
    private JLabel lbl_f_product_stok;
    private JPanel pnl_basket_tab;
    private JScrollPane scrl_basket;
    private JComboBox<Item> cmb_basket_customer;
    private JCheckBox lbl_basket_count;
    private JButton btn_basket_reset;
    private JButton btn_basket_new;
    private JLabel lbl_basket_price;
    private JTable tbl_basket;
    private User user;
    private CustomerController customerController;
    private ProductController productController;
    private BasketController basketController;
    private DefaultTableModel tmdl_customer = new DefaultTableModel();
    private DefaultTableModel tmdl_product = new DefaultTableModel();
    private DefaultTableModel tmdl_basket = new DefaultTableModel();
    private JPopupMenu popup_custumer = new JPopupMenu();
    private JPopupMenu popup_product = new JPopupMenu();

    public DashboardUI(User user) {
        this.user = user;
        this.customerController = new CustomerController();
        this.productController = new ProductController();
        this.basketController = new BasketController();
        if (user == null) {
            Helper.showMsg("error");
            dispose();
        }

        this.add(container);
        this.setTitle("Müşteri Yönetim Sistemi");
        this.setSize(1000, 500);
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getSize().height) / 2;

        this.setLocation(x, y);
        this.setVisible(true);

        this.lbl_welcome.setText("Hoşgeldin: " + this.user.getName());
        this.btn_logout.addActionListener(e -> {
            dispose();
            LoginUI loginUI = new LoginUI();
        });

        // CUSTOMER TABLOSU
        loadCustomerTable(null);
        loadCustomerPopupMenu();
        loaodCustomerButtonEvent();
        this.cmb_f_customer_type.setModel(new DefaultComboBoxModel<>(Customer.TYPE.values()));
        this.cmb_f_customer_type.setSelectedItem(null);

        //PRODUCT TABLOSU
        loadProductTable(null);
        loadProductPopupMenu();
        loadProductButtonEvent();
        this.cmb_f_product_stok.addItem(new Item(1,"Stokta Var"));
        this.cmb_f_product_stok.addItem(new Item(2, "Stokta Yok"));
        this.cmb_f_product_stok.setSelectedItem(null);

        //BASKET TABLOSU

        loadBasketTable();
        loadBasketButtonEvent();
        loadBasketCustomerCombo();

    }

    private void loadBasketCustomerCombo(){
        ArrayList<Customer> customers = this.customerController.findAll();
        this.cmb_basket_customer.removeAll();
        for (Customer customer : customers){
            int comboKey = customer.getId();
            String comboValue = customer.getName();
            this.cmb_basket_customer.addItem(new Item(comboKey, comboValue));
        }
        this.cmb_basket_customer.setSelectedItem(null);
    }

    private void loadBasketButtonEvent(){
        this.btn_basket_reset.addActionListener(e -> {
         if (this.basketController.clear()){
             Helper.showMsg("done");
             loadBasketTable();
         }else {
             Helper.showMsg("error");
         }
        });
    }

    private void loadBasketTable() {
        Object[] columnProduct = {"ID", "Ürün Adı", "Ürün Kodu", "Fiyat", "Stok"};
        ArrayList<Basket> baskets = this.basketController.findAll();

        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_basket.getModel(); // Tabloyu sıfırlar
        clearModel.setRowCount(0);

        this.tmdl_basket.setColumnIdentifiers(columnProduct);
        int totalPrice = 0;
        for (Basket basket : baskets) {
            Object[] rowObject = {
                    basket.getId(),
                    basket.getProduct().getName(),
                    basket.getProduct().getCode(),
                    basket.getProduct().getPrice(),
                    basket.getProduct().getStock()

            };

            this.tmdl_basket.addRow(rowObject);

            totalPrice += basket.getProduct().getPrice();
        }


        this.lbl_basket_price.setText(String.valueOf(totalPrice) + "TL");
        this.lbl_basket_count.setText(String.valueOf(baskets.size()) + "Adet");

        this.tbl_basket.setModel(tmdl_basket);
        this.tbl_basket.getTableHeader().setReorderingAllowed(false);
        this.tbl_basket.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_basket.setDefaultEditor(Object.class, null);
    }

    private void loadProductButtonEvent(){
        this.btn_product_new.addActionListener(e -> {
         ProductUI productUI  = new ProductUI(new Product());
         productUI.addWindowListener(new WindowAdapter() {
             @Override
             public void windowClosed(WindowEvent e) {
                 loadCustomerTable(null);
             }
         });

        });
        this. btn_product_filter.addActionListener(e -> {
         ArrayList<Product> filteredProducts = this.productController.filter(
                 this.fld_f_product_name.getText(),
                 this.fld_f_product_code.getText(),
                 (Item) this.cmb_f_product_stok.getSelectedItem()
         );
         loadProductTable(filteredProducts);
        });

        this.btn_product_filter_reset.addActionListener(e -> {
          this.fld_f_product_code.setText(null);
            this.fld_f_product_name.setText(null);
            this.cmb_f_product_stok.setSelectedItem(null);
            loadProductTable(null);
        });
    }

    private void loadProductPopupMenu(){
        this.tbl_product.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRow = tbl_product.rowAtPoint(e.getPoint());
                tbl_product.setRowSelectionInterval(selectedRow, selectedRow);
            }
        });
        this.popup_product.add("Sepete Ekle").addActionListener(e -> {
            int selectId = Integer.parseInt(this.tbl_product.getValueAt(this.tbl_product.getSelectedRow(),0).toString());
            Product basketProduct = this.productController.getById(selectId);
            if (basketProduct.getStock() <= 0){
                Helper.showMsg("Bu Ürün Stokta Yok");
            }else {
                Basket basket = new Basket(basketProduct.getId());
                if (this.basketController.save(basket)){
                    Helper.showMsg("done");
                    loadBasketTable();
                }else {
                    Helper.showMsg("error");
                }
            }
        });
        this.popup_product.add("Güncelle").addActionListener(e -> {
            int selectId = Integer.parseInt(this.tbl_product.getValueAt(this.tbl_product.getSelectedRow(),0).toString());
             ProductUI productUI  = new ProductUI(this.productController.getById(selectId));
             productUI.addWindowListener(new WindowAdapter() {
                 @Override
                 public void windowClosed(WindowEvent e) {
                     loadCustomerTable(null);
                     loadBasketTable();
                 }
             });
        });
        this.popup_product.add("Sil").addActionListener(e -> {
            int selectId = Integer.parseInt(this.tbl_product.getValueAt(this.tbl_product.getSelectedRow(),0).toString());
          if (Helper.confirm("sure")){
              if (this.productController.delete(selectId)){
                  Helper.showMsg("done");
                  loadProductTable(null);
                  loadBasketTable();
              }else {
                  Helper.showMsg("error");
              }
          }
        });

        this.tbl_product.setComponentPopupMenu(this.popup_product);

    }
    private void loadProductTable(ArrayList<Product> products) {
        Object[] columnProduct = {"ID", "Ürün Adı", "Ürün Kodu", "Fiyat", "Stok"};

        if (products == null) {
            products = this.productController.findAll();
        }

        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_product.getModel(); // Tabloyu sıfırlar
        clearModel.setRowCount(0);

        this.tmdl_product.setColumnIdentifiers(columnProduct);
        for (Product product : products) {
            Object[] rowObject = {
                    product.getId(),
                    product.getName(),
                    product.getCode(),
                    product.getPrice(),
                    product.getStock()
            };

            this.tmdl_product.addRow(rowObject);
        }

        this.tbl_product.setModel(tmdl_product);
        this.tbl_product.getTableHeader().setReorderingAllowed(false);
        this.tbl_product.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_product.setDefaultEditor(Object.class, null);
    }

    private void loaodCustomerButtonEvent() {
        this.btn_customer_new.addActionListener(e -> {
            CustomerUI customerUI = new CustomerUI(new Customer());
            customerUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCustomerTable(null);
                    loadBasketCustomerCombo();
                }
            });
        });

        // Filter button event
        this.btn_customer_filter.addActionListener(e -> {
            String name = this.fld_f_customer_name.getText();
            Customer.TYPE type = (Customer.TYPE) this.cmb_f_customer_type.getSelectedItem();
            System.out.println("Filtreleme yapılıyor: Ad = " + name + ", Tip = " + type);

            ArrayList<Customer> filteredCustomers = this.customerController.filter(
                    name, // Name entered by user
                    type // Customer type selected by user
            );
            loadCustomerTable(filteredCustomers); // Load the filtered results into the table
        });

        // Reset filter button event (if required)
        this.btn_customer_filter_reset.addActionListener(e -> {
            fld_f_customer_name.setText(""); // Clear name field
            cmb_f_customer_type.setSelectedItem(null); // Clear type combo box
            loadCustomerTable(null); // Load all customers again
        });
    }

    private void loadCustomerPopupMenu() {
        this.tbl_customer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRow = tbl_customer.rowAtPoint(e.getPoint());
                tbl_customer.setRowSelectionInterval(selectedRow, selectedRow);
                if (e.isPopupTrigger()) {
                    popup_custumer.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        this.popup_custumer.add("Güncelle").addActionListener(e -> {
            int selectedId = Integer.parseInt(tbl_customer.getValueAt(tbl_customer.getSelectedRow(), 0).toString());
            Customer editedCustomer = this.customerController.getById(selectedId);
            CustomerUI customerUI = new CustomerUI(editedCustomer);
            customerUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCustomerTable(null);
                    loadBasketCustomerCombo();
                }
            });
        });

        this.popup_custumer.add("Sil").addActionListener(e -> {
            int selectedId = Integer.parseInt(tbl_customer.getValueAt(tbl_customer.getSelectedRow(), 0).toString());
            if (Helper.confirm("sure")) {
                if (this.customerController.delete(selectedId)) {
                    Helper.showMsg("done");
                    loadCustomerTable(null);
                    loadBasketCustomerCombo();
                } else {
                    Helper.showMsg("error");
                }
            }
        });

        this.tbl_customer.setComponentPopupMenu(this.popup_custumer);
    }

    private void loadCustomerTable(ArrayList<Customer> customers) {
        Object[] columnCustomer = {"ID", "Müşteri Adı", "Tipi", "Telefon", "E-posta", "Adres"};

        if (customers == null) {
            customers = this.customerController.findAll();
        }

        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_customer.getModel(); // Tabloyu sıfırlar
        clearModel.setRowCount(0);

        this.tmdl_customer.setColumnIdentifiers(columnCustomer);
        for (Customer customer : customers) {
            Object[] rowObject = {
                    customer.getId(),
                    customer.getName(),
                    customer.getType(),
                    customer.getPhone(),
                    customer.getMail(),
                    customer.getAddress(),
            };

            this.tmdl_customer.addRow(rowObject);
        }

        this.tbl_customer.setModel(tmdl_customer);
        this.tbl_customer.getTableHeader().setReorderingAllowed(false);
        this.tbl_customer.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_customer.setDefaultEditor(Object.class, null);
    }
}

