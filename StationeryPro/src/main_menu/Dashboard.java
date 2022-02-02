/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main_menu;

import com.barcodelib.barcode.Linear;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import stationerypro.StationeryPro;
import sub_menu.Print_Struk;
import static sub_menu.Print_Struk.txtprint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.Timer;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Muhammad Cakra, Jacinda Olga, Dimas Pratama, Gigih Dwi, Indra Bagus,
 * Kristian Fransicus
 */
public class Dashboard extends javax.swing.JFrame {

    int xmouse;
    int ymouse;

    /**
     * Creates new form Dashboard
     */
    public Dashboard() {
        initComponents();
        allignment();
        seticon();
        setTitle("Dashboard - Stationery Pro");
        maximize_button.setVisible(false);
        logout_popup.setVisible(false);
        load_table();
        kosong();
        sub_total_summmary();
        sub_total_summmary_beli();
        invoiceNo();
        diskon();
//        grand_total();
        showDate();
        showTime();
        id_member_label.setText("null");
        load_user();
        load_members();
        report_transaksi_table();
        pendapatan();
        total_harga_label.setText(Integer.toString(0));
        diskon_label.setText(Integer.toString(0));
    }

    public void reset_dashboard() {
        DefaultTableModel tblmodel = (DefaultTableModel) cart.getModel();
        try {
            for (int i = 0; i < tblmodel.getRowCount(); i++) {
                String sql = "select * from barang where nama_barang ='" + tblmodel.getValueAt(i, 1).toString() + "'";
                java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
                java.sql.Statement stm = conn.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                while (res.next()) {
                    int oldqty = Integer.parseInt(res.getString(3));
                    int newqty = oldqty + Integer.parseInt(tblmodel.getValueAt(i, 2).toString());
                    String sqll = "UPDATE `barang`" + "SET `stok_barang`='" + newqty + "' WHERE `nama_barang` = '" + tblmodel.getValueAt(i, 1).toString() + "'";
                    java.sql.PreparedStatement pstl = conn.prepareStatement(sqll);
                    pstl.execute();

                }
            }
        } catch (Exception e) {
        }
        load_table();

        id_member_text.setText(null);
        id_member_label.setText("null");
        kode_barang_dashboard.setText("Scan Atau Cari Kode Barang");
        kode_barang_dashboard.setForeground(new Color(153, 153, 153));
        kode_barang_dashboard.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        nama_barang_dashboard.setText(null);
        quantity_dashboard.setText(null);
        DefaultTableModel model = (DefaultTableModel) cart.getModel();
        DefaultTableModel model2 = (DefaultTableModel) tabel_total.getModel();
        model.setRowCount(0);
        model2.setRowCount(0);
        sub_total_sum.setText("0");
        test_label.setText("0");
        diskon_label.setText("0");
        total_harga_label.setText("0");
        bayar_textfield.setText(null);
        kembalian_textfield.setText(null);
        sub_menu.Print_Struk.txtprint.setText(null);
        struk();
    }

    public void pendapatan() {
        pendapatan_txt.setText("0");
        int sum = 0;
        for (int i = 0; i < transaksi_Report.getRowCount(); i++) {
            sum = sum + Integer.parseInt(transaksi_Report.getValueAt(i, 6).toString());
            pendapatan_txt.setText(Integer.toString(sum));
        }

        double pendapatan = Double.parseDouble(pendapatan_txt.getText());

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp.");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);
        pendapatan_txt.setText(kursIndonesia.format(pendapatan));
    }

    private void kosong() {
        kode_barang_text.setText(null);
        kode_barang_text.setFocusable(true);
        nama_barang_text.setText(null);
        stok_barang_text.setText(null);
        harga_satuan_barang_text.setText(null);
        harga_beli_text.setText(null);
    }

    private void showmessage_inventory() {
        if (kode_barang_text.getText().length() == 0 || nama_barang_text.getText().length() == 0 || stok_barang_text.getText().length() == 0 || harga_satuan_barang_text.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "Mohon Isi Semua Form");
        }
    }

    private void seticon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/Taskbar_Icon.png")));
    }

    private void bayar() {
        try {
            int sum = 0;
            for (int i = 0; i < cart.getRowCount(); i++) {
                sum = sum + Integer.parseInt(cart.getValueAt(i, 4).toString());
            }

            int diskon = Integer.parseInt(diskon_label.getText());
            int total = sum - diskon;
            total_harga_label.setText(Integer.toString(total));
            int bayar = Integer.parseInt(bayar_textfield.getText());
            int kembalian = bayar - total;
            kembalian_textfield.setText(Integer.toString(kembalian));

        } catch (Exception e) {

        }
    }

    public void struk() {
        txtprint.setText(txtprint.getText() + "\t        REKSA ATK\n");
        txtprint.setText(txtprint.getText() + "      Jl.Kalimantan Gang.IV/86 Sumbersari, Jember.\n");
        txtprint.setText(txtprint.getText() + "\n");
        txtprint.setText(txtprint.getText() + kode_transaksi.getText() + "\n");
        txtprint.setText(txtprint.getText() + date_label.getText() + "\n");
        txtprint.setText(txtprint.getText() + "----------------------------------------------------------------------\n");
        DefaultTableModel model = (DefaultTableModel) cart.getModel();
        txtprint.setText(txtprint.getText() + "Item" + "\t\t" + "Qty" + "     " + "Harga" + "\t" + "Total" + "\n");
        for (int i = 0; i < model.getRowCount(); i++) {
            String item = (String) model.getValueAt(i, 1).toString();
            String Qty = (String) model.getValueAt(i, 2).toString();
            String Harga = (String) model.getValueAt(i, 3).toString();
            String Subtotal = (String) model.getValueAt(i, 4).toString();
            txtprint.setText(txtprint.getText() + item + "\t\t" + Qty + "      " + Harga + "\t" + Subtotal + "\n");
        }
        txtprint.setText(txtprint.getText() + "----------------------------------------------------------------------\n");
        String Subtotal = sub_total_sum.getText();
        String Diskon = diskon_label.getText();
        String GrandTotal = total_harga_label.getText();
        String bayar = bayar_textfield.getText();
        String kembalian = kembalian_textfield.getText();
        txtprint.setText(txtprint.getText() + "Subtotal" + "\t" + ":" + "\t\t" + Subtotal + "\n");
        txtprint.setText(txtprint.getText() + "----------------------------------------------------------------------\n");
        txtprint.setText(txtprint.getText() + "Diskon" + "\t" + ":" + "\t\t" + Diskon + "\n");
        txtprint.setText(txtprint.getText() + "----------------------------------------------------------------------\n");
        txtprint.setText(txtprint.getText() + "Grand Total" + "\t" + ":" + "\t\t" + GrandTotal + "\n");
        txtprint.setText(txtprint.getText() + "----------------------------------------------------------------------\n");
        txtprint.setText(txtprint.getText() + "Bayar" + "\t" + "Kembalian" + "\n");
        txtprint.setText(txtprint.getText() + bayar_textfield.getText() + "\t" + kembalian_textfield.getText() + "\n");
        txtprint.setText(txtprint.getText() + "----------------------------------------------------------------------\n");
        txtprint.setText(txtprint.getText() + "\n");
        txtprint.setText(txtprint.getText() + "            SEMOGA HARIMU MENYENANGKAN");
    }

    private void allignment() {
        display_name.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        display_position.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        display_picture.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        header_name.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        dashboard_icon_menu.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        report_icon_menu.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        inventory_icon_menu.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        about_icon_menu.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        sr_icon_label.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        users_icon_menu.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        label_bayar.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        label_kembalian.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        member_menu.setHorizontalAlignment(javax.swing.JLabel.CENTER);
    }

    private void showDate() {
        new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date d = new Date();
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                date_label.setText(s.format(d));
            }
        }
        ).start();
    }

    private void showTime() {
        new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date d = new Date();
                SimpleDateFormat s = new SimpleDateFormat("HH:mm:ss");
                time_label.setText(s.format(d));
            }
        }
        ).start();
    }

    public void load_user() {
        DefaultTableModel model = (DefaultTableModel) users_table.getModel();
        try {
            String sql = "select * from akun join staf where akun.kode_staf=staf.kode_staf";
            java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
            java.sql.Statement stm = conn.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()) {
                model.addRow(new Object[]{res.getString(1), res.getString(6), res.getString(7), res.getString(8), res.getString(9),
                    res.getString(2), res.getString(3), res.getString(10), res.getString(4)});
            }
        } catch (Exception e) {
        }
    }

    private void pengurangan_tabel() {
        try {
            String sql = "select * from barang where kode_barang ='" + kode_barang_dashboard.getText() + "'OR nama_barang='" + nama_barang_dashboard.getText() + "'";
            java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
            java.sql.Statement stm = conn.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()) {
                int oldqty = Integer.parseInt(res.getString(3));
                int newqty = oldqty - Integer.parseInt(quantity_dashboard.getText());
                if (Integer.parseInt(res.getString(3)) < Integer.parseInt(quantity_dashboard.getText())) {

                } else {
                    String sqll = "UPDATE `barang`" + "SET `stok_barang`='" + newqty + "' WHERE `kode_barang` = '" + kode_barang_dashboard.getText() + "'OR nama_barang='" + nama_barang_dashboard.getText() + "'";
                    java.sql.PreparedStatement pstl = conn.prepareStatement(sqll);
                    pstl.execute();
                }
            }
        } catch (Exception e) {
        }
    }

    private void load_table() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("No");
        model.addColumn("Kode Barang");
        model.addColumn("Nama Barang");
        model.addColumn("Jumlah Stok");
        model.addColumn("Harga Jual (Rp)");
        model.addColumn("Harga Beli (Rp)");
        try {
            int no = 1;
            if (filter_inventory.getSelectedItem().equals("Nama Barang")) {
                String sql = "select * from barang order by nama_barang ASC";
                java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
                java.sql.Statement stm = conn.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                while (res.next()) {
                    model.addRow(new Object[]{no++, res.getString(1),
                        res.getString(2), res.getString(3), res.getString(4), res.getString(5)});
                }
            } else if (filter_inventory.getSelectedItem().equals("Jumlah Stok")) {
                String sql = "select * from barang order by stok_barang ASC";
                java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
                java.sql.Statement stm = conn.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                while (res.next()) {
                    model.addRow(new Object[]{no++, res.getString(1),
                        res.getString(2), res.getString(3), res.getString(4), res.getString(5)});
                }
            } else if (filter_inventory.getSelectedItem().equals("Kode Barang")) {
                String sql = "select * from barang order by kode_barang ASC";
                java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
                java.sql.Statement stm = conn.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                while (res.next()) {
                    model.addRow(new Object[]{no++, res.getString(1),
                        res.getString(2), res.getString(3), res.getString(4), res.getString(5)});
                }
            } else if (filter_inventory.getSelectedItem().equals("Harga Jual")) {
                String sql = "select * from barang order by harga_satuan ASC";
                java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
                java.sql.Statement stm = conn.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                while (res.next()) {
                    model.addRow(new Object[]{no++, res.getString(1),
                        res.getString(2), res.getString(3), res.getString(4), res.getString(5)});
                }

            } else if (filter_inventory.getSelectedItem().equals("Harga Beli")) {
                String sql = "select * from barang order by harga_beli ASC";
                java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
                java.sql.Statement stm = conn.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                while (res.next()) {
                    model.addRow(new Object[]{no++, res.getString(1),
                        res.getString(2), res.getString(3), res.getString(4), res.getString(5)});
                }
            }
            jTable1.setModel(model);
        } catch (Exception e) {
        }
    }

    private void load_members() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("No");
        model.addColumn("ID-Member");
        model.addColumn("Nama Member");
        model.addColumn("Nomor Telepon");
        try {
            int no = 1;
            String sql = "select * from membership";
            java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
            java.sql.Statement stm = conn.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()) {
                model.addRow(new Object[]{no++, res.getString(1),
                    res.getString(2), res.getString(3)});
            }
            members_table.setModel(model);
        } catch (Exception e) {
        }
    }

    public void cart_table() {
        DefaultTableModel model = (DefaultTableModel) cart.getModel();
        try {
            if (kode_barang_dashboard.getText().length() == 0 || nama_barang_dashboard.getText().length() == 0) {
                JOptionPane.showMessageDialog(this, "Tolong Masukkan Kode Barang atau Nama Barang");
            } else if (quantity_dashboard.getText().length() == 0) {
                JOptionPane.showMessageDialog(this, "Tolong Masukkan Quantity");
            } else {

                int qty = Integer.parseInt(quantity_dashboard.getText());
                String sql = "select * from barang where kode_barang='" + kode_barang_dashboard.getText() + "'OR nama_barang='" + nama_barang_dashboard.getText() + "'";
                java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
                java.sql.Statement stm = conn.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                while (res.next()) {
                    int harga_satuan = Integer.parseInt(res.getString(4));
                    int sub_total = qty * harga_satuan;
                    if (Integer.parseInt(res.getString(3)) < qty) {
                        JOptionPane.showMessageDialog(this, "Stok Barang Tidak Mencukupi\n" + "Stok Barang Saat Ini Adalah " + res.getString(3));
                    } else {
                        model.addRow(new Object[]{res.getString(1), res.getString(2),
                            qty, res.getString(4), sub_total});
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public void cart_table_beli() {
        DefaultTableModel model2 = (DefaultTableModel) tabel_total.getModel();
        DefaultTableModel model = (DefaultTableModel) cart.getModel();
        try {
            model2.setRowCount(0);
            for (int i = 0; i < model.getRowCount(); i++) {
                int qty = Integer.parseInt(model.getValueAt(i, 2).toString());
                String sql = "select * from barang where kode_barang='" + model.getValueAt(i, 0) + "'OR nama_barang='" + model.getValueAt(i, 1) + "'";
                java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
                java.sql.Statement stm = conn.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                while (res.next()) {
                    int harga_beli = Integer.parseInt(res.getString(5));
                    int subtotal_beli = qty * harga_beli;
                    model2.addRow(new Object[]{res.getString(5), subtotal_beli});
                }
            }
        } catch (Exception e) {
        }
    }

    public void report_transaksi_table() {
        DefaultTableModel model = (DefaultTableModel) transaksi_Report.getModel();
        try {
            String sql = "SELECT * from transaksi where tanggal_transaksi between '" + txtDate.getText()
                    + "'AND'" + txtDate2.getText() + "'";
            java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
            java.sql.Statement stm = conn.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()) {
                model.addRow(new Object[]{res.getString(1),
                    res.getString(2), res.getString(3), res.getString(4), res.getString(5), res.getString(6), res.getString(7)});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public void sub_total_summmary() {
        sub_total_sum.setText("0");
        int sum = 0;
        for (int i = 0; i < cart.getRowCount(); i++) {
            sum = sum + Integer.parseInt(cart.getValueAt(i, 4).toString());
            sub_total_sum.setText(Integer.toString(sum));
        }
    }

    public void sub_total_summmary_beli() {
        int sum = 0;
        for (int i = 0; i < tabel_total.getRowCount(); i++) {
            sum = sum + Integer.parseInt(tabel_total.getValueAt(i, 1).toString());
            test_label.setText(Integer.toString(sum));
        }
    }

    public void invoiceNo() {
        try {
            java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select MAX(kode_transaksi) from transaksi");
            rs.next();

            rs.getString("MAX(kode_transaksi)");
            if (rs.getString("MAX(kode_transaksi)") == null) {
                kode_transaksi.setText("ID-0000001");
            } else {
                long id = Long.parseLong(rs.getString("MAX(kode_transaksi)").substring(3, rs.getString("MAX(kode_transaksi)").length()));
                id++;
                kode_transaksi.setText("ID-" + String.format("%07d", id));
            }
        } catch (Exception e) {
        }
    }

    private void diskon() {
        try {
            java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
            String sql = "SELECT id_member from membership where id_member='"+id_member_label.getText()+"'";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()==true) {
                    diskon_label.setFocusable(true);
            } else {
                    diskon_label.setFocusable(false);
                    diskon_label.setText(Integer.toString(0));
                }
        } catch (Exception e) {
        }
    }

    private void grand_total() {
        int sum = 0;
        for (int i = 0; i < cart.getRowCount(); i++) {
            sum = sum + Integer.parseInt(cart.getValueAt(i, 4).toString());
        }

        int diskon = Integer.parseInt(diskon_label.getText());
        int total = sum - diskon;
        total_harga_label.setText(Integer.toString(total));
    }

    public void addPlaceHolderStyle(JTextField textField) {
        Font font = textField.getFont();
        font = font.deriveFont(font.ITALIC);
        textField.setFont(font);
        textField.setForeground(new Color(153, 153, 153));
    }

    public void removePlaceHolderStyle(JTextField textField) {
        Font font = textField.getFont();
        font = font.deriveFont(font.PLAIN);
        textField.setFont(font);
        textField.setForeground(Color.white);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        date = new com.raven.datechooser.DateChooser();
        date2 = new com.raven.datechooser.DateChooser();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabel_total = new javax.swing.JTable();
        Header = new javax.swing.JPanel();
        Min_Max_Close_Panel = new javax.swing.JPanel();
        close_button = new javax.swing.JLabel();
        minimize_button = new javax.swing.JLabel();
        maximize_button = new javax.swing.JLabel();
        Sr_Icon_Panel = new javax.swing.JPanel();
        sr_icon_label = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        header_name = new javax.swing.JLabel();
        Left_Panel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        dashboard_icon_menu = new javax.swing.JLabel();
        inventory_icon_menu = new javax.swing.JLabel();
        about_icon_menu = new javax.swing.JLabel();
        member_menu = new javax.swing.JLabel();
        logout_popup = new javax.swing.JPanel();
        yes_popup = new javax.swing.JLabel();
        no_popup = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        users_tab = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        Panel_Gap = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jLabel8 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        label_bayar = new javax.swing.JLabel();
        label_kembalian = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        kode_barang_dashboard = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        nama_barang_dashboard = new javax.swing.JTextField();
        quantity_dashboard = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        kode_transaksi = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        id_member_text = new javax.swing.JTextField();
        date_label = new javax.swing.JLabel();
        lihat_struk_button = new javax.swing.JButton();
        diskon_label = new javax.swing.JTextField();
        test_label = new javax.swing.JLabel();
        time_label = new javax.swing.JLabel();
        id_member_label = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        detail_transaksi_report = new javax.swing.JTable();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        id_text = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jSeparator17 = new javax.swing.JSeparator();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jSeparator19 = new javax.swing.JSeparator();
        jLabel29 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        pencarian_transaksi = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        kode_barang_text = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        nama_barang_text = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        stok_barang_text = new javax.swing.JTextField();
        harga_satuan_barang_text = new javax.swing.JTextField();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        image_preview_inventory = new javax.swing.JLabel();
        add_button_inventory = new javax.swing.JLabel();
        delete_button_inventory = new javax.swing.JLabel();
        edit_button_inventory = new javax.swing.JLabel();
        clear_button_inventory = new javax.swing.JLabel();
        search_inventory = new javax.swing.JTextField();
        filter_inventory = new javax.swing.JComboBox<>();
        jButton5 = new javax.swing.JButton();
        harga_beli_text = new javax.swing.JTextField();
        jSeparator15 = new javax.swing.JSeparator();
        jLabel22 = new javax.swing.JLabel();
        jButton12 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        users_table = new javax.swing.JTable();
        jSeparator8 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        pencarian_users = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jLabel18 = new javax.swing.JLabel();
        jSeparator11 = new javax.swing.JSeparator();
        jSeparator13 = new javax.swing.JSeparator();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jSeparator14 = new javax.swing.JSeparator();
        jSeparator16 = new javax.swing.JSeparator();
        jLabel23 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jSeparator10 = new javax.swing.JSeparator();
        jLabel19 = new javax.swing.JLabel();
        jSeparator12 = new javax.swing.JSeparator();
        role_combobox = new javax.swing.JComboBox<>();
        txt_kodestaf = new javax.swing.JTextField();
        txt_jabatan = new javax.swing.JTextField();
        txt_password = new javax.swing.JTextField();
        jPanel15 = new javax.swing.JPanel();
        foto_profil = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        txt_namalengkap = new javax.swing.JTextField();
        txt_namapanggilan = new javax.swing.JTextField();
        txt_alamat = new javax.swing.JTextField();
        txt_telepon = new javax.swing.JTextField();
        txt_username = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        about_PCS = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jSeparator18 = new javax.swing.JSeparator();
        txtnamamember = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jSeparator20 = new javax.swing.JSeparator();
        txttelpmember = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        members_table = new javax.swing.JTable();
        jLabel35 = new javax.swing.JLabel();
        jSeparator23 = new javax.swing.JSeparator();
        txtidmember = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        addmember_btn = new javax.swing.JButton();
        deletemember_btn = new javax.swing.JButton();
        editmember_btn = new javax.swing.JButton();
        clearmember_btn = new javax.swing.JButton();
        pencarian_member = new javax.swing.JTextField();

        date.setForeground(new java.awt.Color(240, 224, 96));
        date.setDateFormat("yyyy-MM-dd");
        date.setTextRefernce(txtDate);
        date.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dateMouseClicked(evt);
            }
        });

        date2.setForeground(new java.awt.Color(240, 224, 96));
        date2.setDateFormat("yyyy-MM-dd");
        date2.setTextRefernce(txtDate2);

        tabel_total.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sub Total Jual", "Sub Total Beli"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(tabel_total);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1100, 625));
        setUndecorated(true);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Header.setBackground(new java.awt.Color(31, 29, 43));
        Header.setPreferredSize(new java.awt.Dimension(800, 50));
        Header.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                HeaderMouseDragged(evt);
            }
        });
        Header.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                HeaderMousePressed(evt);
            }
        });
        Header.setLayout(new java.awt.BorderLayout());

        Min_Max_Close_Panel.setBackground(new java.awt.Color(31, 29, 43));
        Min_Max_Close_Panel.setPreferredSize(new java.awt.Dimension(150, 50));

        close_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Close_Button.png"))); // NOI18N
        close_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        close_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                close_buttonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                close_buttonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                close_buttonMouseExited(evt);
            }
        });

        minimize_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Minimize_Button.png"))); // NOI18N
        minimize_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        minimize_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                minimize_buttonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                minimize_buttonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                minimize_buttonMouseExited(evt);
            }
        });

        maximize_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Maximize_icon.png"))); // NOI18N
        maximize_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        maximize_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                maximize_buttonMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout Min_Max_Close_PanelLayout = new javax.swing.GroupLayout(Min_Max_Close_Panel);
        Min_Max_Close_Panel.setLayout(Min_Max_Close_PanelLayout);
        Min_Max_Close_PanelLayout.setHorizontalGroup(
            Min_Max_Close_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Min_Max_Close_PanelLayout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addComponent(maximize_button)
                .addGap(14, 14, 14)
                .addComponent(minimize_button, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(close_button, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        Min_Max_Close_PanelLayout.setVerticalGroup(
            Min_Max_Close_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Min_Max_Close_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Min_Max_Close_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(close_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(minimize_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(maximize_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        Header.add(Min_Max_Close_Panel, java.awt.BorderLayout.LINE_END);

        Sr_Icon_Panel.setBackground(new java.awt.Color(31, 29, 43));
        Sr_Icon_Panel.setPreferredSize(new java.awt.Dimension(150, 50));

        sr_icon_label.setBackground(new java.awt.Color(255, 255, 255));
        sr_icon_label.setFont(new java.awt.Font("Impact", 1, 14)); // NOI18N
        sr_icon_label.setForeground(new java.awt.Color(240, 224, 96));
        sr_icon_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Sr_Icon.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Stationery Pro");

        javax.swing.GroupLayout Sr_Icon_PanelLayout = new javax.swing.GroupLayout(Sr_Icon_Panel);
        Sr_Icon_Panel.setLayout(Sr_Icon_PanelLayout);
        Sr_Icon_PanelLayout.setHorizontalGroup(
            Sr_Icon_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Sr_Icon_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sr_icon_label, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                .addContainerGap())
        );
        Sr_Icon_PanelLayout.setVerticalGroup(
            Sr_Icon_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Sr_Icon_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Sr_Icon_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sr_icon_label, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        Header.add(Sr_Icon_Panel, java.awt.BorderLayout.LINE_START);

        jPanel3.setBackground(new java.awt.Color(31, 29, 43));

        header_name.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        header_name.setForeground(new java.awt.Color(255, 255, 255));
        header_name.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        header_name.setText("DASHBOARD");

        role_label.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        role_label.setForeground(new java.awt.Color(51, 255, 0));
        role_label.setText("ADMIN");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(role_label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(header_name, javax.swing.GroupLayout.PREFERRED_SIZE, 791, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(header_name, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(role_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        Header.add(jPanel3, java.awt.BorderLayout.CENTER);

        getContentPane().add(Header, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1100, -1));

        Left_Panel.setBackground(new java.awt.Color(31, 29, 43));
        Left_Panel.setPreferredSize(new java.awt.Dimension(156, 450));
        Left_Panel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                Left_PanelMouseDragged(evt);
            }
        });
        Left_Panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Left_PanelMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Left_PanelMousePressed(evt);
            }
        });
        Left_Panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(37, 40, 55));
        jPanel1.setPreferredSize(new java.awt.Dimension(120, 120));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        display_picture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/user_photo.png"))); // NOI18N
        jPanel1.add(display_picture, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 120, 120));

        Left_Panel.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jPanel2.setBackground(new java.awt.Color(31, 29, 43));

        display_name.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        display_name.setForeground(new java.awt.Color(255, 255, 255));
        display_name.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        display_name.setText("Cakra");
        display_name.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        display_name.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        display_position.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        display_position.setForeground(new java.awt.Color(240, 224, 96));
        display_position.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        display_position.setText("Cashier");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(display_name, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addComponent(display_position, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(display_name, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(display_position)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        Left_Panel.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 160, 50));

        jPanel7.setBackground(new java.awt.Color(31, 29, 43));

        dashboard_icon_menu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dashboard_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Dashboard_Icon_Hover.png"))); // NOI18N
        dashboard_icon_menu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dashboard_icon_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dashboard_icon_menuMouseClicked(evt);
            }
        });

        inventory_icon_menu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        inventory_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Inventory_Icon.png"))); // NOI18N
        inventory_icon_menu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        inventory_icon_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inventory_icon_menuMouseClicked(evt);
            }
        });

        about_icon_menu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        about_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/about_icon.png"))); // NOI18N
        about_icon_menu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        about_icon_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                about_icon_menuMouseClicked(evt);
            }
        });

        member_menu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        member_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/member_icon.png"))); // NOI18N
        member_menu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        member_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                member_menuMouseClicked(evt);
            }
        });

        users_icon_menu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        users_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/users_menu.png"))); // NOI18N
        users_icon_menu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        users_icon_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                users_icon_menuMouseClicked(evt);
            }
        });

        report_icon_menu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        report_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Report_Icon.png"))); // NOI18N
        report_icon_menu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        report_icon_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                report_icon_menuMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(dashboard_icon_menu, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(inventory_icon_menu, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(report_icon_menu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(member_menu, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(about_icon_menu, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(users_icon_menu, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dashboard_icon_menu, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inventory_icon_menu, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(member_menu, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(about_icon_menu, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(users_icon_menu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(report_icon_menu, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Left_Panel.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 140, 250));

        logout_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Logout_Icon.png"))); // NOI18N
        logout_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        logout_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logout_buttonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logout_buttonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logout_buttonMouseExited(evt);
            }
        });
        Left_Panel.add(logout_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 540, 70, 30));

        logout_popup.setBackground(new java.awt.Color(31, 29, 43));
        logout_popup.setPreferredSize(new java.awt.Dimension(130, 60));
        logout_popup.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        yes_popup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/yes_logout.png"))); // NOI18N
        yes_popup.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                yes_popupMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                yes_popupMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                yes_popupMouseExited(evt);
            }
        });
        logout_popup.add(yes_popup, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, -1, -1));

        no_popup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/no_logout.png"))); // NOI18N
        no_popup.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        no_popup.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                no_popupMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                no_popupMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                no_popupMouseExited(evt);
            }
        });
        logout_popup.add(no_popup, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Popup_Logout.png"))); // NOI18N
        logout_popup.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        Left_Panel.add(logout_popup, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 470, 140, -1));

        getContentPane().add(Left_Panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, -1, 580));

        users_tab.setBackground(new java.awt.Color(37, 40, 55));
        users_tab.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                users_tabMouseDragged(evt);
            }
        });
        users_tab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                users_tabMousePressed(evt);
            }
        });

        Panel_Gap.setBackground(new java.awt.Color(37, 40, 55));

        cart.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Barang", "Nama Barang", "Quantity", "Harga Satuan (Rp)", "Subtotal (Rp)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        cart.setToolTipText("");
        cart.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cartMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(cart);

        jLabel8.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Sub Total (Rp)");

        jLabel9.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Diskon (Rp)");

        jLabel11.setBackground(new java.awt.Color(240, 224, 96));
        jLabel11.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(224, 79, 95));
        jLabel11.setText("Total Harga (Rp)");

        label_bayar.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        label_bayar.setForeground(new java.awt.Color(255, 255, 255));
        label_bayar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_bayar.setText("Bayar (Rp)");

        label_kembalian.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        label_kembalian.setForeground(new java.awt.Color(255, 255, 255));
        label_kembalian.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_kembalian.setText("Kembalian(Rp)");

        jButton1.setBackground(new java.awt.Color(240, 224, 96));
        jButton1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButton1.setText("BAYAR DAN PRINT STRUK");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton1MouseExited(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(240, 224, 96));
        jButton2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButton2.setText("HAPUS");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton2MouseExited(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(240, 224, 96));
        jButton3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButton3.setText("RESET");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton3MouseExited(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        kode_barang_dashboard.setBackground(new java.awt.Color(31, 29, 43));
        kode_barang_dashboard.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N
        kode_barang_dashboard.setForeground(new java.awt.Color(153, 153, 153));
        kode_barang_dashboard.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        kode_barang_dashboard.setText("Scan Atau Cari Kode Barang");
        kode_barang_dashboard.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        kode_barang_dashboard.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                kode_barang_dashboardFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                kode_barang_dashboardFocusLost(evt);
            }
        });
        kode_barang_dashboard.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kode_barang_dashboardKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                kode_barang_dashboardKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                kode_barang_dashboardKeyTyped(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(240, 224, 96));
        jButton4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButton4.setText("Tambah");
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton4MouseExited(evt);
            }
        });
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        bayar_textfield.setBackground(new java.awt.Color(31, 29, 43));
        bayar_textfield.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        bayar_textfield.setForeground(new java.awt.Color(255, 255, 255));
        bayar_textfield.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        bayar_textfield.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        bayar_textfield.setCaretColor(new java.awt.Color(255, 255, 255));
        bayar_textfield.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                bayar_textfieldFocusGained(evt);
            }
        });
        bayar_textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bayar_textfieldActionPerformed(evt);
            }
        });
        bayar_textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                bayar_textfieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                bayar_textfieldKeyTyped(evt);
            }
        });

        nama_barang_dashboard.setBackground(new java.awt.Color(31, 29, 43));
        nama_barang_dashboard.setForeground(new java.awt.Color(255, 255, 255));
        nama_barang_dashboard.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        nama_barang_dashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nama_barang_dashboardActionPerformed(evt);
            }
        });
        nama_barang_dashboard.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                nama_barang_dashboardKeyReleased(evt);
            }
        });

        quantity_dashboard.setBackground(new java.awt.Color(31, 29, 43));
        quantity_dashboard.setForeground(new java.awt.Color(255, 255, 255));
        quantity_dashboard.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        quantity_dashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quantity_dashboardActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Kode Barang");

        jLabel13.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Nama Barang");

        jLabel14.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Quantity");

        kode_transaksi.setBackground(new java.awt.Color(37, 40, 55));
        kode_transaksi.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        kode_transaksi.setForeground(new java.awt.Color(255, 255, 255));
        kode_transaksi.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        kode_transaksi.setText("Kode Transaksi");
        kode_transaksi.setBorder(null);
        kode_transaksi.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        kode_transaksi.setFocusable(false);
        kode_transaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kode_transaksiActionPerformed(evt);
            }
        });

        sub_total_sum.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        sub_total_sum.setForeground(new java.awt.Color(255, 255, 255));

        jLabel15.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("ID Member");

        id_member_text.setBackground(new java.awt.Color(31, 29, 43));
        id_member_text.setForeground(new java.awt.Color(255, 255, 255));
        id_member_text.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        id_member_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                id_member_textActionPerformed(evt);
            }
        });
        id_member_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                id_member_textKeyReleased(evt);
            }
        });

        total_harga_label.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        total_harga_label.setForeground(new java.awt.Color(255, 255, 255));

        kembalian_textfield.setBackground(new java.awt.Color(31, 29, 43));
        kembalian_textfield.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        kembalian_textfield.setForeground(new java.awt.Color(255, 255, 255));
        kembalian_textfield.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        kembalian_textfield.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        kembalian_textfield.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        kembalian_textfield.setFocusable(false);
        kembalian_textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kembalian_textfieldActionPerformed(evt);
            }
        });

        date_label.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        date_label.setForeground(new java.awt.Color(240, 224, 96));
        date_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        date_label.setText("Tanggal");

        kode_staf_label.setForeground(new java.awt.Color(37, 40, 55));
        kode_staf_label.setText("jLabel16");

        lihat_struk_button.setBackground(new java.awt.Color(240, 224, 96));
        lihat_struk_button.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        lihat_struk_button.setText("LIHAT STRUK");
        lihat_struk_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lihat_struk_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lihat_struk_buttonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lihat_struk_buttonMouseExited(evt);
            }
        });
        lihat_struk_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lihat_struk_buttonActionPerformed(evt);
            }
        });

        diskon_label.setBackground(new java.awt.Color(37, 40, 55));
        diskon_label.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        diskon_label.setForeground(new java.awt.Color(255, 255, 255));
        diskon_label.setBorder(null);
        diskon_label.setFocusable(false);
        diskon_label.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                diskon_labelFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                diskon_labelFocusLost(evt);
            }
        });
        diskon_label.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                diskon_labelKeyReleased(evt);
            }
        });

        test_label.setBackground(new java.awt.Color(37, 40, 55));
        test_label.setForeground(new java.awt.Color(37, 40, 55));
        test_label.setText("jLabel27");

        time_label.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        time_label.setForeground(new java.awt.Color(240, 224, 96));
        time_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        time_label.setText("Waktu");

        id_member_label.setForeground(new java.awt.Color(37, 40, 55));
        id_member_label.setText("text");

        javax.swing.GroupLayout Panel_GapLayout = new javax.swing.GroupLayout(Panel_Gap);
        Panel_Gap.setLayout(Panel_GapLayout);
        Panel_GapLayout.setHorizontalGroup(
            Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel_GapLayout.createSequentialGroup()
                .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Panel_GapLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(Panel_GapLayout.createSequentialGroup()
                                .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(32, 32, 32)
                                .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(quantity_dashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(nama_barang_dashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(kode_barang_dashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(id_member_text, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(Panel_GapLayout.createSequentialGroup()
                                        .addComponent(kode_staf_label)
                                        .addGap(101, 101, 101)
                                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(Panel_GapLayout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(test_label))
                                            .addGroup(Panel_GapLayout.createSequentialGroup()
                                                .addGap(92, 92, 92)
                                                .addComponent(id_member_label, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_GapLayout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 630, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(16, 16, 16))))
                    .addGroup(Panel_GapLayout.createSequentialGroup()
                        .addGap(182, 182, 182)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(125, 125, 125)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(175, 175, 175)))
                .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_GapLayout.createSequentialGroup()
                        .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(diskon_label, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(total_harga_label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, Panel_GapLayout.createSequentialGroup()
                                .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 40, Short.MAX_VALUE)))
                        .addGap(7, 7, 7))
                    .addComponent(sub_total_sum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(Panel_GapLayout.createSequentialGroup()
                        .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(label_kembalian, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(kembalian_textfield, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(bayar_textfield, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(label_bayar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(Panel_GapLayout.createSequentialGroup()
                                .addGap(81, 81, 81)
                                .addComponent(lihat_struk_button)))
                        .addGap(3, 3, 3))
                    .addGroup(Panel_GapLayout.createSequentialGroup()
                        .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel8)
                                .addComponent(kode_transaksi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                                .addComponent(time_label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(date_label, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        Panel_GapLayout.setVerticalGroup(
            Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_GapLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(Panel_GapLayout.createSequentialGroup()
                        .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(id_member_text, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(kode_barang_dashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nama_barang_dashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(quantity_dashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(Panel_GapLayout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(kode_staf_label)
                                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(Panel_GapLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(id_member_label)
                                .addGap(2, 2, 2)
                                .addComponent(test_label)))
                        .addGap(15, 15, 15)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(Panel_GapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(Panel_GapLayout.createSequentialGroup()
                        .addComponent(date_label, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(time_label, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(kode_transaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sub_total_sum, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(diskon_label, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(total_harga_label, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bayar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bayar_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_kembalian)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(kembalian_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lihat_struk_button)
                        .addGap(16, 16, 16))))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(Panel_Gap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel_Gap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        users_tab.addTab("Dashboard", jPanel4);

        jPanel5.setBackground(new java.awt.Color(37, 40, 55));

        transaksi_Report.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Transaksi", "Tanggal", "Waktu", "Kode Member", "Kode Staf", "Diskon (Rp)", "Laba (Rp)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        transaksi_Report.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                transaksi_ReportMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(transaksi_Report);

        detail_transaksi_report.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Barang", "Quantity", "Harga Jual (Rp)", "Harga Beli (Rp)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(detail_transaksi_report);

        jLabel25.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(240, 224, 96));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("DETAIL TRANSAKSI");

        jLabel26.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(240, 224, 96));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("TRANSAKSI");

        id_text.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        id_text.setForeground(new java.awt.Color(255, 255, 255));
        id_text.setText("ID-Transaksi");

        jLabel28.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("Dari Tanggal");

        txtDate.setBackground(new java.awt.Color(37, 40, 55));
        txtDate.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txtDate.setForeground(new java.awt.Color(255, 255, 255));
        txtDate.setBorder(null);
        txtDate.setFocusable(false);
        txtDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDateMouseClicked(evt);
            }
        });
        txtDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDateActionPerformed(evt);
            }
        });

        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/calendar.png"))); // NOI18N
        jLabel30.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel30.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel30MouseClicked(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setText("Sampai Tanggal");

        txtDate2.setBackground(new java.awt.Color(37, 40, 55));
        txtDate2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txtDate2.setForeground(new java.awt.Color(255, 255, 255));
        txtDate2.setBorder(null);
        txtDate2.setFocusable(false);
        txtDate2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDate2ActionPerformed(evt);
            }
        });

        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/calendar.png"))); // NOI18N
        jLabel32.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel32.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel32MouseClicked(evt);
            }
        });

        pendapatan_txt.setBackground(new java.awt.Color(31, 29, 43));
        pendapatan_txt.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        pendapatan_txt.setForeground(new java.awt.Color(255, 255, 255));
        pendapatan_txt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        pendapatan_txt.setText("99999999999999999999999999999");
        pendapatan_txt.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pendapatan_txt.setFocusable(false);
        pendapatan_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pendapatan_txtActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("Total Pendapatan");

        jButton6.setBackground(new java.awt.Color(240, 224, 96));
        jButton6.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jButton6.setText("Tanggal Sekarang");
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        pencarian_transaksi.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        pencarian_transaksi.setText("Pencarian");
        pencarian_transaksi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                pencarian_transaksiFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                pencarian_transaksiFocusLost(evt);
            }
        });
        pencarian_transaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pencarian_transaksiActionPerformed(evt);
            }
        });
        pencarian_transaksi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pencarian_transaksiKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(pencarian_transaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(id_text, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator17, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel28)
                                            .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel31)
                                            .addComponent(txtDate2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jSeparator19, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(56, 56, 56)
                                .addComponent(jButton6)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pendapatan_txt)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel28)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator17, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel31)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtDate2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator19, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pendapatan_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(id_text, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pencarian_transaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        users_tab.addTab("Report", jPanel5);

        jPanel6.setBackground(new java.awt.Color(37, 40, 55));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Kode Barang", "Nama Barang", "Jumlah Stok", "Harga Jual", "Harga Beli"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel3.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Kode Barang");

        kode_barang_text.setBackground(new java.awt.Color(37, 40, 55));
        kode_barang_text.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        kode_barang_text.setForeground(new java.awt.Color(255, 255, 255));
        kode_barang_text.setBorder(null);
        kode_barang_text.setSelectedTextColor(new java.awt.Color(37, 40, 55));
        kode_barang_text.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                kode_barang_textFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                kode_barang_textFocusLost(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Nama Barang");

        nama_barang_text.setBackground(new java.awt.Color(37, 40, 55));
        nama_barang_text.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        nama_barang_text.setForeground(new java.awt.Color(255, 255, 255));
        nama_barang_text.setBorder(null);
        nama_barang_text.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nama_barang_textFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                nama_barang_textFocusLost(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Jumlah Stok");

        stok_barang_text.setBackground(new java.awt.Color(37, 40, 55));
        stok_barang_text.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        stok_barang_text.setForeground(new java.awt.Color(255, 255, 255));
        stok_barang_text.setBorder(null);
        stok_barang_text.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                stok_barang_textFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                stok_barang_textFocusLost(evt);
            }
        });

        harga_satuan_barang_text.setBackground(new java.awt.Color(37, 40, 55));
        harga_satuan_barang_text.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        harga_satuan_barang_text.setForeground(new java.awt.Color(255, 255, 255));
        harga_satuan_barang_text.setBorder(null);
        harga_satuan_barang_text.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                harga_satuan_barang_textFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                harga_satuan_barang_textFocusLost(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Harga Jual");

        add_button_inventory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/add_icon.png"))); // NOI18N
        add_button_inventory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        add_button_inventory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                add_button_inventoryMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                add_button_inventoryMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                add_button_inventoryMouseExited(evt);
            }
        });

        delete_button_inventory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/delete_icon.png"))); // NOI18N
        delete_button_inventory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        delete_button_inventory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                delete_button_inventoryMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                delete_button_inventoryMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                delete_button_inventoryMouseExited(evt);
            }
        });

        edit_button_inventory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/edit_icon.png"))); // NOI18N
        edit_button_inventory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        edit_button_inventory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                edit_button_inventoryMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                edit_button_inventoryMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                edit_button_inventoryMouseExited(evt);
            }
        });

        clear_button_inventory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/clear_icon.png"))); // NOI18N
        clear_button_inventory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        clear_button_inventory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clear_button_inventoryMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                clear_button_inventoryMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                clear_button_inventoryMouseExited(evt);
            }
        });

        search_inventory.setBackground(new java.awt.Color(31, 29, 43));
        search_inventory.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N
        search_inventory.setForeground(new java.awt.Color(153, 153, 153));
        search_inventory.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        search_inventory.setText("Scan Atau Cari Barang");
        search_inventory.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        search_inventory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                search_inventoryFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                search_inventoryFocusLost(evt);
            }
        });
        search_inventory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                search_inventoryKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                search_inventoryKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                search_inventoryKeyTyped(evt);
            }
        });

        filter_inventory.setBackground(new java.awt.Color(255, 255, 0));
        filter_inventory.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        filter_inventory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Kode Barang", "Nama Barang", "Jumlah Stok", "Harga Jual", "Harga Beli" }));
        filter_inventory.setBorder(null);
        filter_inventory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filter_inventoryActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(240, 224, 96));
        jButton5.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jButton5.setText("Generate Barcode");
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        harga_beli_text.setBackground(new java.awt.Color(37, 40, 55));
        harga_beli_text.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        harga_beli_text.setForeground(new java.awt.Color(255, 255, 255));
        harga_beli_text.setBorder(null);
        harga_beli_text.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                harga_beli_textFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                harga_beli_textFocusLost(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Harga Beli");

        jButton12.setBackground(new java.awt.Color(102, 255, 51));
        jButton12.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jButton12.setText("Export .xlsx");
        jButton12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton12MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 684, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(search_inventory, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(100, 100, 100)
                        .addComponent(filter_inventory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton12)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(kode_barang_text, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nama_barang_text, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stok_barang_text, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(harga_satuan_barang_text, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(harga_beli_text, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator15, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                                        .addGap(50, 50, 50)
                                        .addComponent(image_preview_inventory, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(18, 18, 18))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(add_button_inventory, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(edit_button_inventory, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(delete_button_inventory, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(clear_button_inventory, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(20, 20, 20)))
                        .addContainerGap())))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(image_preview_inventory, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(search_inventory, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(filter_inventory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton12)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(kode_barang_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nama_barang_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(stok_barang_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(harga_satuan_barang_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(harga_beli_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jSeparator15, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(add_button_inventory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(edit_button_inventory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(delete_button_inventory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(clear_button_inventory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(26, 26, 26)
                        .addComponent(jButton5)
                        .addGap(30, 30, 30))))
        );

        users_tab.addTab("Inventory", jPanel6);

        jPanel12.setBackground(new java.awt.Color(37, 40, 55));

        users_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Staf", "Nama Lengkap", "Nama Panggilan", "Jabatan", "Alamat", "Username", "Password", "No Telepon", "Role"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        users_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                users_tableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(users_table);

        jLabel7.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Kode Staf");

        pencarian_users.setBackground(new java.awt.Color(31, 29, 43));
        pencarian_users.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N
        pencarian_users.setForeground(new java.awt.Color(153, 153, 153));
        pencarian_users.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        pencarian_users.setText("Search");
        pencarian_users.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pencarian_users.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                pencarian_usersFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                pencarian_usersFocusLost(evt);
            }
        });
        pencarian_users.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pencarian_usersKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pencarian_usersKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                pencarian_usersKeyTyped(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Jabatan");

        jLabel18.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Alamat");

        jLabel20.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Nama Lengkap");

        jLabel21.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Username");

        jLabel23.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Nama Panggilan");

        jLabel17.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Password");

        jLabel19.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("No Telepon");

        role_combobox.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        role_combobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "admin", "user" }));

        txt_kodestaf.setBackground(new java.awt.Color(37, 40, 55));
        txt_kodestaf.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_kodestaf.setForeground(new java.awt.Color(255, 255, 255));
        txt_kodestaf.setBorder(null);

        txt_jabatan.setBackground(new java.awt.Color(37, 40, 55));
        txt_jabatan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_jabatan.setForeground(new java.awt.Color(255, 255, 255));
        txt_jabatan.setBorder(null);

        txt_password.setBackground(new java.awt.Color(37, 40, 55));
        txt_password.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_password.setForeground(new java.awt.Color(255, 255, 255));
        txt_password.setBorder(null);

        jPanel15.setBackground(new java.awt.Color(31, 29, 43));
        jPanel15.setMinimumSize(new java.awt.Dimension(120, 120));
        jPanel15.setPreferredSize(new java.awt.Dimension(120, 120));

        foto_profil.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        foto_profil.setMinimumSize(new java.awt.Dimension(100, 100));

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(foto_profil, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(foto_profil, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );

        jButton7.setText("Change Photo");
        jButton7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(240, 224, 96));
        jButton8.setText("Add");
        jButton8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton8MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton8MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton8MouseExited(evt);
            }
        });

        jButton9.setBackground(new java.awt.Color(240, 224, 96));
        jButton9.setText("Edit");
        jButton9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton9MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton9MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton9MouseExited(evt);
            }
        });

        jButton10.setBackground(new java.awt.Color(240, 224, 96));
        jButton10.setText("Delete");
        jButton10.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton10MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton10MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton10MouseExited(evt);
            }
        });

        jButton11.setBackground(new java.awt.Color(240, 224, 96));
        jButton11.setText("Clear");
        jButton11.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton11MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton11MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton11MouseExited(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Role");

        txt_namalengkap.setBackground(new java.awt.Color(37, 40, 55));
        txt_namalengkap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_namalengkap.setForeground(new java.awt.Color(255, 255, 255));
        txt_namalengkap.setBorder(null);

        txt_namapanggilan.setBackground(new java.awt.Color(37, 40, 55));
        txt_namapanggilan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_namapanggilan.setForeground(new java.awt.Color(255, 255, 255));
        txt_namapanggilan.setBorder(null);

        txt_alamat.setBackground(new java.awt.Color(37, 40, 55));
        txt_alamat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_alamat.setForeground(new java.awt.Color(255, 255, 255));
        txt_alamat.setBorder(null);

        txt_telepon.setBackground(new java.awt.Color(37, 40, 55));
        txt_telepon.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_telepon.setForeground(new java.awt.Color(255, 255, 255));
        txt_telepon.setBorder(null);

        txt_username.setBackground(new java.awt.Color(37, 40, 55));
        txt_username.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_username.setForeground(new java.awt.Color(255, 255, 255));
        txt_username.setBorder(null);

        jLabel10.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("120px X 120px (Jpeg, Png)");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pencarian_users, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_password)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jSeparator8)
                                .addComponent(jLabel16)
                                .addComponent(jSeparator9)
                                .addComponent(jLabel7)
                                .addComponent(txt_kodestaf)
                                .addComponent(txt_jabatan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel17)
                            .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jSeparator12, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_telepon, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(role_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel20)
                                        .addComponent(jSeparator13, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel18)
                                        .addComponent(jSeparator11, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                .addGroup(jPanel12Layout.createSequentialGroup()
                                    .addComponent(jLabel19)
                                    .addGap(136, 136, 136)))
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel24)
                                .addComponent(jSeparator16, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jSeparator14, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel23)
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(txt_namalengkap, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_namapanggilan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(txt_alamat, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_username, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(32, 32, 32)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addGap(27, 27, 27)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(568, 568, 568))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 921, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pencarian_users, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel12Layout.createSequentialGroup()
                            .addComponent(jLabel16)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_jabatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_alamat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(4, 4, 4)
                            .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel17)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_telepon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(4, 4, 4)
                            .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel12Layout.createSequentialGroup()
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel12Layout.createSequentialGroup()
                                    .addComponent(jLabel23)
                                    .addGap(29, 29, 29)
                                    .addComponent(jSeparator16, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel21)
                                    .addGap(29, 29, 29)
                                    .addComponent(jSeparator14, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel12Layout.createSequentialGroup()
                                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel12Layout.createSequentialGroup()
                                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(txt_kodestaf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txt_namalengkap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txt_namapanggilan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(3, 3, 3)
                                            .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel12Layout.createSequentialGroup()
                                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel20)
                                                .addComponent(jLabel7))
                                            .addGap(29, 29, 29)
                                            .addComponent(jSeparator13, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel18)
                                    .addGap(29, 29, 29)
                                    .addComponent(jSeparator11, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel12Layout.createSequentialGroup()
                                    .addComponent(jLabel19)
                                    .addGap(29, 29, 29)
                                    .addComponent(jSeparator12, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel12Layout.createSequentialGroup()
                                    .addComponent(jLabel24)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(role_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jButton8)
                        .addGap(18, 18, 18)
                        .addComponent(jButton9)
                        .addGap(18, 18, 18)
                        .addComponent(jButton10)
                        .addGap(18, 18, 18)
                        .addComponent(jButton11)))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 935, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        users_tab.addTab("Users", jPanel9);

        jPanel11.setBackground(new java.awt.Color(37, 40, 55));

        about_PCS.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        about_PCS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/about.png"))); // NOI18N

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(about_PCS, javax.swing.GroupLayout.DEFAULT_SIZE, 915, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(about_PCS, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        users_tab.addTab("About", jPanel10);

        jPanel13.setBackground(new java.awt.Color(37, 40, 55));

        txtnamamember.setBackground(new java.awt.Color(37, 40, 55));
        txtnamamember.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtnamamember.setForeground(new java.awt.Color(255, 255, 255));
        txtnamamember.setBorder(null);
        txtnamamember.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtnamamemberFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtnamamemberFocusLost(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("Nomor Telepon");

        txttelpmember.setBackground(new java.awt.Color(37, 40, 55));
        txttelpmember.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txttelpmember.setForeground(new java.awt.Color(255, 255, 255));
        txttelpmember.setBorder(null);
        txttelpmember.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txttelpmemberFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txttelpmemberFocusLost(evt);
            }
        });

        members_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID-Member", "Nama Member", "Nomor Telepon"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        members_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                members_tableMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(members_table);

        jLabel35.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setText("ID-Member");

        txtidmember.setBackground(new java.awt.Color(37, 40, 55));
        txtidmember.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        txtidmember.setForeground(new java.awt.Color(255, 255, 255));
        txtidmember.setBorder(null);
        txtidmember.setSelectedTextColor(new java.awt.Color(37, 40, 55));
        txtidmember.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtidmemberFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtidmemberFocusLost(evt);
            }
        });

        jLabel36.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setText("Nama Member");

        addmember_btn.setBackground(new java.awt.Color(240, 224, 96));
        addmember_btn.setText("Tambah");
        addmember_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addmember_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addmember_btnMouseClicked(evt);
            }
        });

        deletemember_btn.setBackground(new java.awt.Color(240, 224, 96));
        deletemember_btn.setText("Hapus");
        deletemember_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        deletemember_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                deletemember_btnMouseClicked(evt);
            }
        });

        editmember_btn.setBackground(new java.awt.Color(240, 224, 96));
        editmember_btn.setText("Edit");
        editmember_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        editmember_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editmember_btnMouseClicked(evt);
            }
        });

        clearmember_btn.setBackground(new java.awt.Color(240, 224, 96));
        clearmember_btn.setText("Clear");
        clearmember_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        clearmember_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearmember_btnMouseClicked(evt);
            }
        });

        pencarian_member.setText("Pencarian");
        pencarian_member.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                pencarian_memberFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                pencarian_memberFocusLost(evt);
            }
        });
        pencarian_member.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pencarian_memberKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(pencarian_member, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 674, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(editmember_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(addmember_btn))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(clearmember_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(deletemember_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(48, 48, 48))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel13Layout.createSequentialGroup()
                                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.LEADING))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(jSeparator23)
                                    .addComponent(txtidmember)
                                    .addComponent(jSeparator18)
                                    .addComponent(txtnamamember)
                                    .addComponent(txttelpmember)
                                    .addComponent(jSeparator20))
                                .addContainerGap())))))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(118, Short.MAX_VALUE)
                .addComponent(pencarian_member, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtidmember, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jSeparator23, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtnamamember, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jSeparator18, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txttelpmember, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jSeparator20, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(addmember_btn)
                            .addComponent(deletemember_btn))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(editmember_btn)
                            .addComponent(clearmember_btn))))
                .addGap(11, 11, 11))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        users_tab.addTab("Members", jPanel8);

        getContentPane().add(users_tab, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 20, 940, 610));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void close_buttonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_close_buttonMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            reset_dashboard();
            System.exit(0);
        }
    }//GEN-LAST:event_close_buttonMouseClicked

    private void minimize_buttonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimize_buttonMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            this.setState(JFrame.ICONIFIED);
        }
    }//GEN-LAST:event_minimize_buttonMouseClicked

    private void maximize_buttonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_maximize_buttonMouseClicked
        // TODO add your handling code here:
//        if (this.getExtendedState()!=Dashboard.MAXIMIZED_BOTH) {
//            this.setExtendedState(Dashboard.MAXIMIZED_BOTH);
//        } else {
//            this.setExtendedState(Dashboard.NORMAL);
//        }
    }//GEN-LAST:event_maximize_buttonMouseClicked

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseDragged

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_formMousePressed

    private void minimize_buttonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimize_buttonMouseEntered
        // TODO add your handling code here:
        minimize_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Minimize_Icon_Hover.png")));
    }//GEN-LAST:event_minimize_buttonMouseEntered

    private void minimize_buttonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimize_buttonMouseExited
        // TODO add your handling code here:
        minimize_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Minimize_Button.png")));
    }//GEN-LAST:event_minimize_buttonMouseExited

    private void close_buttonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_close_buttonMouseEntered
        // TODO add your handling code here:
        close_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Close_Icon_Hover.png")));
    }//GEN-LAST:event_close_buttonMouseEntered

    private void close_buttonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_close_buttonMouseExited
        // TODO add your handling code here:
        close_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Close_Button.png")));
    }//GEN-LAST:event_close_buttonMouseExited

    private void HeaderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HeaderMousePressed
        // TODO add your handling code here:
        xmouse = evt.getX();
        ymouse = evt.getY();
    }//GEN-LAST:event_HeaderMousePressed

    private void HeaderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HeaderMouseDragged
        // TODO add your handling code here:
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();

        this.setLocation(x - xmouse, y - ymouse);
        System.out.println(x + "," + y);
    }//GEN-LAST:event_HeaderMouseDragged

    private void users_tabMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_users_tabMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_users_tabMouseDragged

    private void users_tabMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_users_tabMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_users_tabMousePressed

    private void Left_PanelMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Left_PanelMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_Left_PanelMouseDragged

    private void Left_PanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Left_PanelMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_Left_PanelMouseClicked

    private void Left_PanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Left_PanelMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Left_PanelMousePressed

    private void logout_buttonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logout_buttonMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_logout_buttonMouseEntered

    private void logout_buttonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logout_buttonMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_logout_buttonMouseExited

    private void logout_buttonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logout_buttonMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            logout_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Logout_Icon_Hover.png")));
            logout_popup.setVisible(true);
        }
    }//GEN-LAST:event_logout_buttonMouseClicked

    private void no_popupMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_no_popupMouseClicked
        // TODO add your handling code here:
        logout_popup.setVisible(false);
        logout_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Logout_Icon.png")));
    }//GEN-LAST:event_no_popupMouseClicked

    private void yes_popupMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_yes_popupMouseClicked
        // TODO add your handling code here:
        this.dispose();
        new Login_Form().setVisible(true);
    }//GEN-LAST:event_yes_popupMouseClicked

    private void no_popupMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_no_popupMouseEntered
        // TODO add your handling code here:
        no_popup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/no_logout_hover.png")));
    }//GEN-LAST:event_no_popupMouseEntered

    private void no_popupMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_no_popupMouseExited
        // TODO add your handling code here:
        no_popup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/no_logout.png")));
    }//GEN-LAST:event_no_popupMouseExited

    private void yes_popupMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_yes_popupMouseEntered
        // TODO add your handling code here:
        yes_popup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/yes_logout_hover.png")));
    }//GEN-LAST:event_yes_popupMouseEntered

    private void yes_popupMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_yes_popupMouseExited
        // TODO add your handling code here:
        yes_popup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/yes_logout.png")));
    }//GEN-LAST:event_yes_popupMouseExited

    private void kode_barang_textFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_kode_barang_textFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_kode_barang_textFocusGained

    private void kode_barang_textFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_kode_barang_textFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_kode_barang_textFocusLost

    private void nama_barang_textFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nama_barang_textFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_nama_barang_textFocusGained

    private void nama_barang_textFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nama_barang_textFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_nama_barang_textFocusLost

    private void stok_barang_textFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_stok_barang_textFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_stok_barang_textFocusGained

    private void stok_barang_textFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_stok_barang_textFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_stok_barang_textFocusLost

    private void harga_satuan_barang_textFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_harga_satuan_barang_textFocusGained
        // TODO add your handling code here:}
    }//GEN-LAST:event_harga_satuan_barang_textFocusGained

    private void harga_satuan_barang_textFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_harga_satuan_barang_textFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_harga_satuan_barang_textFocusLost

    private void add_button_inventoryMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_add_button_inventoryMouseEntered
        // TODO add your handling code here:
        add_button_inventory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/add_icon_hover.png")));
    }//GEN-LAST:event_add_button_inventoryMouseEntered

    private void add_button_inventoryMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_add_button_inventoryMouseExited
        // TODO add your handling code here:
        add_button_inventory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/add_icon.png")));
    }//GEN-LAST:event_add_button_inventoryMouseExited

    private void delete_button_inventoryMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_delete_button_inventoryMouseEntered
        // TODO add your handling code here:
        delete_button_inventory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/delete_icon_hover.png")));
    }//GEN-LAST:event_delete_button_inventoryMouseEntered

    private void delete_button_inventoryMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_delete_button_inventoryMouseExited
        // TODO add your handling code here:
        delete_button_inventory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/delete_icon.png")));
    }//GEN-LAST:event_delete_button_inventoryMouseExited

    private void edit_button_inventoryMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_edit_button_inventoryMouseEntered
        // TODO add your handling code here:
        edit_button_inventory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/edit_icon_hover.png")));
    }//GEN-LAST:event_edit_button_inventoryMouseEntered

    private void edit_button_inventoryMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_edit_button_inventoryMouseExited
        // TODO add your handling code here:
        edit_button_inventory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/edit_icon.png")));
    }//GEN-LAST:event_edit_button_inventoryMouseExited

    private void clear_button_inventoryMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clear_button_inventoryMouseEntered
        // TODO add your handling code here:
        clear_button_inventory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/clear_icon_hover.png")));
    }//GEN-LAST:event_clear_button_inventoryMouseEntered

    private void clear_button_inventoryMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clear_button_inventoryMouseExited
        // TODO add your handling code here:
        clear_button_inventory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/clear_icon.png")));
    }//GEN-LAST:event_clear_button_inventoryMouseExited

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        int baris = jTable1.rowAtPoint(evt.getPoint());
        String kode_barang = jTable1.getValueAt(baris, 1).toString();
        kode_barang_text.setText(kode_barang);
        if (jTable1.getValueAt(baris, 1) == null) {
            kode_barang_text.setFocusable(true);
        } else {
            kode_barang_text.setFocusable(false);
        }
        if (jTable1.getValueAt(baris, 2) == null) {
            nama_barang_text.setText("");
        } else {
            nama_barang_text.setText(jTable1.getValueAt(baris, 2).toString());
        }
        if (jTable1.getValueAt(baris, 3) == null) {
            stok_barang_text.setText("");
        } else {
            stok_barang_text.setText(jTable1.getValueAt(baris, 3).toString());
        }
        if (jTable1.getValueAt(baris, 4) == null) {
            harga_satuan_barang_text.setText("");
        } else {
            harga_satuan_barang_text.setText(jTable1.getValueAt(baris, 4).toString());
        }
        if (jTable1.getValueAt(baris, 5) == null) {
            harga_beli_text.setText("");
        } else {
            harga_beli_text.setText(jTable1.getValueAt(baris, 5).toString());
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void add_button_inventoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_add_button_inventoryMouseClicked
        // TODO add your handling code here:
        if (kode_barang_text.getText().length() == 0 || nama_barang_text.getText().length() == 0 || stok_barang_text.getText().length() == 0 || harga_satuan_barang_text.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "Mohon Masukkan Semua Data");
        } else {
            try {
                String sql = "INSERT INTO  `barang`(`kode_barang`, `nama_barang`, `stok_barang`, `harga_satuan`,`harga_beli`) VALUES ('" + kode_barang_text.getText() + "','"
                        + nama_barang_text.getText() + "','" + stok_barang_text.getText() + "','"
                        + harga_satuan_barang_text.getText() + "','"
                        + harga_beli_text.getText() + "')";
                java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
                java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                pst.execute();
                JOptionPane.showMessageDialog(null, "Penyimpanan Data Berhasil");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Penambahan Data Gagal");
            }
            load_table();
            kosong();
        }
    }//GEN-LAST:event_add_button_inventoryMouseClicked

    private void clear_button_inventoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clear_button_inventoryMouseClicked
        // TODO add your handling code here:
        kosong();
    }//GEN-LAST:event_clear_button_inventoryMouseClicked

    private void edit_button_inventoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_edit_button_inventoryMouseClicked
        // TODO add your handling code here:
        if (kode_barang_text.getText().length() == 0 || nama_barang_text.getText().length() == 0 || stok_barang_text.getText().length() == 0 || harga_satuan_barang_text.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "Mohon Isi Semua Form");
        } else {
            try {
                String sql = "UPDATE `barang`"
                        + "SET `kode_barang`='" + kode_barang_text.getText() + "', nama_barang = '"
                        + nama_barang_text.getText() + "',stok_barang= '"
                        + stok_barang_text.getText() + "',harga_satuan= '" + harga_satuan_barang_text.getText()
                        + "',harga_beli= '" + harga_beli_text.getText()
                        + "'WHERE kode_barang = '" + kode_barang_text.getText() + "'";
                java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
                java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                pst.execute();
                JOptionPane.showMessageDialog(null, "Data Berhasil Diubah");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Perubahan Data Gagal"
                        + e.getMessage());
            }
        }
        load_table();
        kosong();
    }//GEN-LAST:event_edit_button_inventoryMouseClicked

    private void delete_button_inventoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_delete_button_inventoryMouseClicked
        // TODO add your handling code here:
        if (kode_barang_text.getText().length() == 0 || nama_barang_text.getText().length() == 0 || stok_barang_text.getText().length() == 0 || harga_satuan_barang_text.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "Mohon Pilih Data Yang Ingin Dihapus");
        } else {
            try {
                String sql = "DELETE FROM barang where kode_barang='" + kode_barang_text.getText() + "'";
                java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
                java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                pst.execute();
                JOptionPane.showMessageDialog(this, "Data Berhasil Dihapus");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
        load_table();
        kosong();
    }//GEN-LAST:event_delete_button_inventoryMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if (cart.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tabel Belanjaan Kosong");
        } else if (bayar_textfield.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "Tolong Masukkan Nominal Bayar");
        } else {
            try {
                DefaultTableModel model = (DefaultTableModel) cart.getModel();
                DefaultTableModel model2 = (DefaultTableModel) tabel_total.getModel();
                String sql = "INSERT INTO `transaksi`(`kode_transaksi`,`tanggal_transaksi`,`waktu_transaksi`,`id_member`,`kode_staf`,`diskon`,`laba`) VALUES (?,?,?,?,?,?,?)";
                java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
                java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, kode_transaksi.getText());
                pst.setString(2, date_label.getText());
                pst.setString(3, time_label.getText());
                if (id_member_label.getText().equals("null")) {
                    pst.setString(4, null);
                } else {
                    pst.setString(4, id_member_label.getText());
                }
                pst.setString(5, kode_staf_label.getText());
                int diskon = Integer.parseInt(diskon_label.getText());
                int laba = Integer.parseInt(total_harga_label.getText()) - Integer.parseInt(test_label.getText());
                pst.setInt(6, diskon);
                pst.setInt(7, laba);
                pst.execute();
                for (int i = 0; i < model.getRowCount(); i++) {
                    int hargajual = Integer.parseInt(model.getValueAt(i, 3).toString());
                    String kodebr = model.getValueAt(i, 0).toString();
                    int qty = Integer.parseInt(model.getValueAt(i, 2).toString());
                    String sqll = "INSERT INTO `detail_transaksi`(`kode_transaksi`,`kode_barang`,`quantity`,`harga_jual`,`harga_beli`) VALUES (?,?,?,?,?)";
                    java.sql.PreparedStatement pstl = conn.prepareStatement(sqll);
                    pstl.setString(1, kode_transaksi.getText());
                    pstl.setString(2, kodebr);
                    pstl.setInt(3, qty);
                    pstl.setInt(4, hargajual);
                    int hargabeli = Integer.parseInt(model2.getValueAt(i, 0).toString());
                    pstl.setInt(5, hargabeli);
                    pstl.execute();
                }
                invoiceNo();
            } catch (Exception e) {
            }

            sub_menu.Print_Struk.txtprint.setText(null);
            struk();

            try {
                Print_Struk.txtprint.print();
            } catch (java.awt.print.PrinterException e) {
                System.err.format("Tidak Ada Printer Yang Ditemukan", e.getMessage());
            }

            id_member_text.setText(null);
            id_member_label.setText("null");
            kode_barang_dashboard.setText("Scan Atau Cari Kode Barang");
            kode_barang_dashboard.setForeground(new Color(153, 153, 153));
            kode_barang_dashboard.setHorizontalAlignment(javax.swing.JLabel.CENTER);
            nama_barang_dashboard.setText(null);
            quantity_dashboard.setText(null);
            diskon_label.setFocusable(false);
            DefaultTableModel model = (DefaultTableModel) cart.getModel();
            DefaultTableModel model2 = (DefaultTableModel) tabel_total.getModel();
            DefaultTableModel model3 = (DefaultTableModel) transaksi_Report.getModel();
            model.setRowCount(0);
            model2.setRowCount(0);
            model3.setRowCount(0);
            test_label.setText("0");
            report_transaksi_table();
            pendapatan();
            sub_total_sum.setText("0");
            diskon_label.setText("0");
            total_harga_label.setText("0");
            bayar_textfield.setText(null);
            kembalian_textfield.setText(null);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseEntered
        // TODO add your handling code here:
        jButton1.setBackground(Color.green);
    }//GEN-LAST:event_jButton1MouseEntered

    private void jButton1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseExited
        // TODO add your handling code here:
        jButton1.setBackground(new Color(240, 224, 96));
    }//GEN-LAST:event_jButton1MouseExited

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseEntered
        // TODO add your handling code here:
        jButton2.setBackground(new Color(224, 79, 95));
    }//GEN-LAST:event_jButton2MouseEntered

    private void jButton2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseExited
        // TODO add your handling code here:
        jButton2.setBackground(new Color(240, 224, 96));
    }//GEN-LAST:event_jButton2MouseExited

    private void jButton3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseExited
        // TODO add your handling code here:
        jButton3.setBackground(new Color(240, 224, 96));
    }//GEN-LAST:event_jButton3MouseExited

    private void jButton3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseEntered
        // TODO add your handling code here:
        jButton3.setBackground(new Color(224, 79, 95));
    }//GEN-LAST:event_jButton3MouseEntered

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseExited
        // TODO add your handling code here:
        jButton4.setBackground(new Color(240, 224, 96));
    }//GEN-LAST:event_jButton4MouseExited

    private void jButton4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseEntered
        // TODO add your handling code here:
        jButton4.setBackground(Color.green);
    }//GEN-LAST:event_jButton4MouseEntered

    private void kode_barang_dashboardFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_kode_barang_dashboardFocusGained
        // TODO add your handling code here:
        if (kode_barang_dashboard.getText().equals("Scan Atau Cari Kode Barang")) {
            kode_barang_dashboard.setText(null);
            removePlaceHolderStyle(kode_barang_dashboard);
            kode_barang_dashboard.setHorizontalAlignment(javax.swing.JLabel.LEFT);
        }
    }//GEN-LAST:event_kode_barang_dashboardFocusGained

    private void kode_barang_dashboardFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_kode_barang_dashboardFocusLost
        // TODO add your handling code here:
        if (kode_barang_dashboard.getText().length() == 0) {
            kode_barang_dashboard.setText("Scan Atau Cari Kode Barang");
            addPlaceHolderStyle(kode_barang_dashboard);
            kode_barang_dashboard.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        }
    }//GEN-LAST:event_kode_barang_dashboardFocusLost

    private void kode_barang_dashboardKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kode_barang_dashboardKeyReleased
        // TODO add your handling code here:
        try {

            String sql = "select * from barang where kode_barang='" + kode_barang_dashboard.getText() + "'";
            java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
            java.sql.Statement stm = conn.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()) {
                nama_barang_dashboard.setText(res.getString("nama_barang"));
            }
        } catch (Exception e) {
        }
        if (kode_barang_dashboard.getText().equals("")) {
            nama_barang_dashboard.setText(null);
        }
    }//GEN-LAST:event_kode_barang_dashboardKeyReleased

    private void kode_barang_dashboardKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kode_barang_dashboardKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_kode_barang_dashboardKeyTyped

    private void kode_barang_dashboardKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kode_barang_dashboardKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_kode_barang_dashboardKeyPressed

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
        // TODO add your handling code here:  
        cart_table();
        sub_total_summmary();
        cart_table_beli();
        sub_total_summmary_beli();
        diskon();
        grand_total();
        bayar();
        if (id_member_text.getText().length() > 0) {
            id_member_label.setText(id_member_text.getText());
        } else if (id_member_text.getText().length() == 0) {
            id_member_label.setText("null");
        }
        pengurangan_tabel();
        load_table();
        sub_menu.Print_Struk.txtprint.setText(null);
        struk();
    }//GEN-LAST:event_jButton4MouseClicked

    private void search_inventoryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_search_inventoryFocusGained
        // TODO add your handling code here:
        if (search_inventory.getText().equals("Scan Atau Cari Barang")) {
            search_inventory.setText(null);
            removePlaceHolderStyle(search_inventory);
            search_inventory.setHorizontalAlignment(javax.swing.JLabel.LEFT);
        }
    }//GEN-LAST:event_search_inventoryFocusGained

    private void search_inventoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_search_inventoryFocusLost
        // TODO add your handling code here:
        if (search_inventory.getText().length() == 0) {
            search_inventory.setText("Scan Atau Cari Barang");
            addPlaceHolderStyle(search_inventory);
            search_inventory.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        }
    }//GEN-LAST:event_search_inventoryFocusLost

    private void search_inventoryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_search_inventoryKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_search_inventoryKeyPressed

    private void search_inventoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_search_inventoryKeyReleased
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) jTable1.getModel();
        String search = search_inventory.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> tr = new TableRowSorter<DefaultTableModel>(table);
        jTable1.setRowSorter(tr);
        tr.setRowFilter(RowFilter.regexFilter(search));
    }//GEN-LAST:event_search_inventoryKeyReleased

    private void search_inventoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_search_inventoryKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_search_inventoryKeyTyped

    private void nama_barang_dashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nama_barang_dashboardActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nama_barang_dashboardActionPerformed

    private void quantity_dashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quantity_dashboardActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_quantity_dashboardActionPerformed

    private void kode_transaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kode_transaksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kode_transaksiActionPerformed

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) cart.getModel();
        DefaultTableModel model2 = (DefaultTableModel) tabel_total.getModel();
        try {

            String sql = "select * from barang where nama_barang ='" + model.getValueAt(cart.getSelectedRow(), 1).toString() + "'";
            java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
            java.sql.Statement stm = conn.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()) {
                int oldqty = Integer.parseInt(res.getString(3));
                int newqty = oldqty + Integer.parseInt(model.getValueAt(cart.getSelectedRow(), 2).toString());
                String sqll = "UPDATE `barang`" + "SET `stok_barang`='" + newqty + "' WHERE `nama_barang` = '" + model.getValueAt(cart.getSelectedRow(), 1).toString() + "'";
                java.sql.PreparedStatement pstl = conn.prepareStatement(sqll);
                pstl.execute();

            }
        } catch (Exception e) {

        }
        load_table();
        if (cart.getSelectedRowCount() >= 1) {
            model.removeRow(cart.getSelectedRow());
            model2.setRowCount(0);
            cart_table_beli();
            sub_total_summmary_beli();
        }

        sub_total_summmary();
        diskon();
        grand_total();

        if (cart.getRowCount() == 0) {
            sub_total_sum.setText("0");
            diskon_label.setText("0");
            total_harga_label.setText("0");
            bayar_textfield.setText(null);
            kembalian_textfield.setText(null);
        }

        if (id_member_text.getText().length() > 0) {
            id_member_label.setText(id_member_text.getText());
        } else if (id_member_text.getText().length() == 0) {
            id_member_label.setText("null");
        }

        bayar();

        sub_menu.Print_Struk.txtprint.setText(null);
        struk();
    }//GEN-LAST:event_jButton2MouseClicked

    private void bayar_textfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bayar_textfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bayar_textfieldActionPerformed

    private void id_member_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_id_member_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_id_member_textActionPerformed

    private void kembalian_textfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kembalian_textfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kembalian_textfieldActionPerformed

    private void cartMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cartMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_cartMouseClicked

    private void bayar_textfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bayar_textfieldKeyReleased
        // TODO add your handling code here:
        if (bayar_textfield.getText().length() == 0) {
            kembalian_textfield.setText("");
        }
        bayar();
    }//GEN-LAST:event_bayar_textfieldKeyReleased

    private void bayar_textfieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bayar_textfieldFocusGained
        // TODO add your handling code here
        if (bayar_textfield.getText().equals("0")) {
            bayar_textfield.setText("");
            kembalian_textfield.setText("");
        }
    }//GEN-LAST:event_bayar_textfieldFocusGained

    private void bayar_textfieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bayar_textfieldKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_bayar_textfieldKeyTyped

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1MouseClicked

    private void id_member_textKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_id_member_textKeyReleased
        // TODO add your handling code here:
        if (id_member_text.getText().length() == 0) {
            id_member_label.setText("null");
        } else if (id_member_text.getText().length() > 0) {
            id_member_label.setText(id_member_text.getText());
        }
        diskon();
    }//GEN-LAST:event_id_member_textKeyReleased

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
        // TODO add your handling code here:
        reset_dashboard();
    }//GEN-LAST:event_jButton3MouseClicked

    private void filter_inventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filter_inventoryActionPerformed
        // TODO add your handling code here:
        load_table();
    }//GEN-LAST:event_filter_inventoryActionPerformed

    private void lihat_struk_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lihat_struk_buttonActionPerformed
        // TODO add your handling code here:
        new Print_Struk().setVisible(true);
        sub_menu.Print_Struk.txtprint.setText(null);
        struk();
    }//GEN-LAST:event_lihat_struk_buttonActionPerformed

    private void lihat_struk_buttonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lihat_struk_buttonMouseEntered
        // TODO add your handling code here:
        lihat_struk_button.setBackground(Color.GREEN);
    }//GEN-LAST:event_lihat_struk_buttonMouseEntered

    private void lihat_struk_buttonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lihat_struk_buttonMouseExited
        // TODO add your handling code here:
        lihat_struk_button.setBackground(new Color(240, 224, 96));
    }//GEN-LAST:event_lihat_struk_buttonMouseExited

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        try {
            if (kode_barang_text.getText().length() == 0) {
                JOptionPane.showMessageDialog(this, "Tolong Masukkan Kode Barang");
            } else {

                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                JFileChooser chooser = new JFileChooser();
                chooser.setSelectedFile(new File(kode_barang_text.getText()));
                chooser.showSaveDialog(this);
                File fr = chooser.getCurrentDirectory();
                String filename = fr.getAbsolutePath();
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

                Linear barcode = new Linear();
                barcode.setType(Linear.CODE128B);
                barcode.setData(kode_barang_text.getText());
                barcode.setI(11.0f);
                String fname = kode_barang_text.getText();
                barcode.renderBarcode(filename + fname + ".png");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
//        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void pencarian_usersFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pencarian_usersFocusGained
        // TODO add your handling code here:
        if (pencarian_users.getText().equals("Search")) {
            pencarian_users.setText(null);
            removePlaceHolderStyle(pencarian_users);
            pencarian_users.setHorizontalAlignment(javax.swing.JLabel.LEFT);
        }
    }//GEN-LAST:event_pencarian_usersFocusGained

    private void pencarian_usersFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pencarian_usersFocusLost
        // TODO add your handling code here:
        if (pencarian_users.getText().length() == 0) {
            pencarian_users.setText("Search");
            addPlaceHolderStyle(pencarian_users);
            pencarian_users.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        }
    }//GEN-LAST:event_pencarian_usersFocusLost

    private void pencarian_usersKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pencarian_usersKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_pencarian_usersKeyPressed

    private void pencarian_usersKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pencarian_usersKeyReleased
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) users_table.getModel();
        String search = pencarian_users.getText().toString();
        TableRowSorter<DefaultTableModel> tr = new TableRowSorter<DefaultTableModel>(table);
        users_table.setRowSorter(tr);
        tr.setRowFilter(RowFilter.regexFilter(search));
    }//GEN-LAST:event_pencarian_usersKeyReleased

    private void pencarian_usersKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pencarian_usersKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_pencarian_usersKeyTyped

    private void users_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_users_tableMouseClicked
        // TODO add your handling code here:

        int baris = users_table.rowAtPoint(evt.getPoint());
        String kode_staf = users_table.getValueAt(baris, 0).toString();
        txt_kodestaf.setText(kode_staf);
        if (users_table.getValueAt(baris, 0) == null) {
            txt_kodestaf.setFocusable(true);
        } else {
            txt_kodestaf.setFocusable(false);
        }
        if (users_table.getValueAt(baris, 1) == null) {
            txt_namalengkap.setText("");
        } else {
            txt_namalengkap.setText(users_table.getValueAt(baris, 1).toString());
        }
        if (users_table.getValueAt(baris, 2) == null) {
            txt_namapanggilan.setText("");
        } else {
            txt_namapanggilan.setText(users_table.getValueAt(baris, 2).toString());
        }
        if (users_table.getValueAt(baris, 3) == null) {
            txt_jabatan.setText("");
        } else {
            txt_jabatan.setText(users_table.getValueAt(baris, 3).toString());
        }
        if (users_table.getValueAt(baris, 4) == null) {
            txt_alamat.setText("");
        } else {
            txt_alamat.setText(users_table.getValueAt(baris, 4).toString());
        }
        if (users_table.getValueAt(baris, 5) == null) {
            txt_username.setText("");
        } else {
            txt_username.setText(users_table.getValueAt(baris, 5).toString());
        }
        if (users_table.getValueAt(baris, 6) == null) {
            txt_password.setText("");
        } else {
            txt_password.setText(users_table.getValueAt(baris, 6).toString());
        }
        if (users_table.getValueAt(baris, 7) == null) {
            txt_telepon.setText("");
        } else {
            txt_telepon.setText(users_table.getValueAt(baris, 7).toString());
        }
        if (users_table.getValueAt(baris, 8) == null) {
            role_combobox.setSelectedItem(this);
        } else {
            role_combobox.setSelectedItem(users_table.getValueAt(baris, 8).toString());
        }
        try {
            String sql = "SELECT * FROM staf WHERE kode_staf='" + txt_kodestaf.getText()
                    + "'";
            java.sql.Connection conn = (Connection) StationeryPro.configDB();
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery(sql);
            while (rs.next()) {
                if (rs.getString("foto_profil") == null) {
                    foto_profil.setVisible(false);
                }
                if (rs.getString("foto_profil") != null) {
                    foto_profil.setVisible(true);
                    BufferedImage im = ImageIO.read(rs.getBinaryStream("foto_profil"));
                    ImageIcon myImage = new ImageIcon(im);
                    Image img = myImage.getImage();
                    Image newImage = img.getScaledInstance(foto_profil.getWidth(), foto_profil.getHeight(), Image.SCALE_SMOOTH);
                    foto_profil.setIcon(new ImageIcon(newImage));
                }
            }
        } catch (Exception e) {

        }
    }//GEN-LAST:event_users_tableMouseClicked

    private void jButton8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton8MouseClicked
        // TODO add your handling code here:
        try {
            String sqll = "INSERT INTO  `staf`(`kode_staf`, `nama_lengkap_staf`, `nama_staf`, `jabatan_staf`,`alamat_staf`,`telp_staf`) VALUES ('"
                    + txt_kodestaf.getText() + "','"
                    + txt_namalengkap.getText() + "','"
                    + txt_namapanggilan.getText() + "','"
                    + txt_jabatan.getText() + "','"
                    + txt_alamat.getText() + "','"
                    + txt_telepon.getText() + "')";
            String sql = "INSERT INTO  `akun`(`kode_staf`, `username`, `password`, `roles`) VALUES ('"
                    + txt_kodestaf.getText() + "','"
                    + txt_username.getText() + "','"
                    + txt_password.getText() + "','"
                    + role_combobox.getSelectedItem() + "')";
            java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            java.sql.PreparedStatement pstl = conn.prepareStatement(sqll);
            pst.execute();
            pstl.execute();
            JOptionPane.showMessageDialog(null, "Penyimpanan Data Berhasil");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        DefaultTableModel model = (DefaultTableModel) users_table.getModel();
        model.setRowCount(0);
        load_user();
    }//GEN-LAST:event_jButton8MouseClicked

    private void jButton9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton9MouseClicked
        // TODO add your handling code here:
        try {
            String sql = "UPDATE `akun`"
                    + "SET username ='" + txt_username.getText()
                    + "', password= '" + txt_password.getText()
                    + "', roles= '" + role_combobox.getSelectedItem()
                    + "'WHERE kode_staf = '" + txt_kodestaf.getText() + "'";
            String sqll = "UPDATE `staf`"
                    + "SET `nama_lengkap_staf` ='" + txt_namalengkap.getText()
                    + "', `nama_staf` = '" + txt_namapanggilan.getText()
                    + "', `jabatan_staf`= '" + txt_jabatan.getText()
                    + "', `alamat_staf`= '" + txt_alamat.getText()
                    + "', `telp_staf`= '" + txt_telepon.getText()
                    + "'WHERE kode_staf = '" + txt_kodestaf.getText() + "'";
            java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            java.sql.PreparedStatement pstl = conn.prepareStatement(sqll);
            pst.execute();
            pstl.execute();
            JOptionPane.showMessageDialog(null, "Data Berhasil Diubah");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Perubahan Data Gagal"
                    + e.getMessage());
        }
        DefaultTableModel model = (DefaultTableModel) users_table.getModel();
        model.setRowCount(0);
        load_user();
    }//GEN-LAST:event_jButton9MouseClicked

    private void jButton10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton10MouseClicked
        // TODO add your handling code here:
        try {
            String sqll = "DELETE FROM akun where kode_staf='" + txt_kodestaf.getText() + "'";
            String sql = "DELETE FROM staf where kode_staf='" + txt_kodestaf.getText() + "'";
            java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            java.sql.PreparedStatement pstl = conn.prepareStatement(sqll);
            pst.execute();
            pstl.execute();
            JOptionPane.showMessageDialog(this, "Data Berhasil Dihapus");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        DefaultTableModel model = (DefaultTableModel) users_table.getModel();
        model.setRowCount(0);
        load_user();
    }//GEN-LAST:event_jButton10MouseClicked

    private void jButton11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton11MouseClicked
        // TODO add your handling code here:
        txt_kodestaf.setText(null);
        txt_kodestaf.setFocusable(true);
        txt_namalengkap.setText(null);
        txt_namapanggilan.setText(null);
        txt_jabatan.setText(null);
        txt_alamat.setText(null);
        txt_username.setText(null);
        txt_password.setText(null);
        txt_telepon.setText(null);
        foto_profil.setVisible(false);
    }//GEN-LAST:event_jButton11MouseClicked

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFileChooser filechooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("*.IMAGE", "jpg", "png");
            filechooser.addChoosableFileFilter(filter);
            int result = filechooser.showOpenDialog(this);
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedImage = filechooser.getSelectedFile();
                String imagePath = selectedImage.getAbsolutePath();
                try {
//                foto_profil.setVisible(true);
                    InputStream is = new FileInputStream(new File(imagePath));
                    String sqll = "UPDATE staf SET foto_profil = ? where kode_staf= '" + txt_kodestaf.getText() + "'";
                    java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
                    java.sql.PreparedStatement pstl = conn.prepareStatement(sqll);
                    pstl.setBinaryStream(1, is);
                    pstl.execute();
                    foto_profil.setIcon(ResizeImage(imagePath));
                    imagePathStr = imagePath;

                    String sql = "SELECT foto_profil FROM staf where kode_staf ='" + kode_staf_label.getText() + "'";
                    java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                    java.sql.ResultSet rs = pst.executeQuery(sql);
                    while (rs.next()) {
                        BufferedImage im = ImageIO.read(rs.getBinaryStream("foto_profil"));
                        ImageIcon myImage = new ImageIcon(im);
                        Image img = myImage.getImage();
                        Image newImage = img.getScaledInstance(Dashboard.display_picture.getWidth(), Dashboard.display_picture.getHeight(), Image.SCALE_SMOOTH);
                        display_picture.setIcon(new ImageIcon(newImage));
                    }
                } catch (Exception e) {
                }

            }
        } catch (Exception e) {

        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton8MouseEntered
        // TODO add your handling code here:
        jButton8.setBackground(new Color(16, 204, 0));
    }//GEN-LAST:event_jButton8MouseEntered

    private void jButton8MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton8MouseExited
        // TODO add your handling code here:
        jButton8.setBackground(new Color(240, 224, 96));
    }//GEN-LAST:event_jButton8MouseExited

    private void jButton9MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton9MouseExited
        // TODO add your handling code here:
        jButton9.setBackground(new Color(240, 224, 96));
    }//GEN-LAST:event_jButton9MouseExited

    private void jButton10MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton10MouseExited
        // TODO add your handling code here:
        jButton10.setBackground(new Color(240, 224, 96));
    }//GEN-LAST:event_jButton10MouseExited

    private void jButton11MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton11MouseExited
        // TODO add your handling code here:
        jButton11.setBackground(new Color(240, 224, 96));
    }//GEN-LAST:event_jButton11MouseExited

    private void jButton9MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton9MouseEntered
        // TODO add your handling code here:
        jButton9.setBackground(new Color(170, 201, 245));
    }//GEN-LAST:event_jButton9MouseEntered

    private void jButton10MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton10MouseEntered
        // TODO add your handling code here:
        jButton10.setBackground(Color.PINK);
    }//GEN-LAST:event_jButton10MouseEntered

    private void jButton11MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton11MouseEntered
        // TODO add your handling code here:
        jButton11.setBackground(Color.orange);
    }//GEN-LAST:event_jButton11MouseEntered

    private void nama_barang_dashboardKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nama_barang_dashboardKeyReleased
        // TODO add your handling code here:
        try {

            String sql = "select * from barang where nama_barang='" + nama_barang_dashboard.getText() + "'";
            java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
            java.sql.Statement stm = conn.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()) {
                kode_barang_dashboard.setText(res.getString("kode_barang"));
                removePlaceHolderStyle(kode_barang_dashboard);
                kode_barang_dashboard.setHorizontalAlignment(javax.swing.JLabel.LEADING);
            }
        } catch (Exception e) {
        }
        if (nama_barang_dashboard.getText().equals("")) {
            kode_barang_dashboard.setText(null);
        }
    }//GEN-LAST:event_nama_barang_dashboardKeyReleased

    private void harga_beli_textFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_harga_beli_textFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_harga_beli_textFocusGained

    private void harga_beli_textFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_harga_beli_textFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_harga_beli_textFocusLost

    private void txtDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateActionPerformed

    private void txtDate2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDate2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDate2ActionPerformed

    private void pendapatan_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pendapatan_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pendapatan_txtActionPerformed

    private void jLabel32MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel32MouseClicked
        // TODO add your handling code here:
        date2.showPopup();
    }//GEN-LAST:event_jLabel32MouseClicked

    private void jLabel30MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel30MouseClicked
        // TODO add your handling code here:
        date.showPopup();
    }//GEN-LAST:event_jLabel30MouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        date.toDay();
        date2.toDay();
        DefaultTableModel model = (DefaultTableModel) transaksi_Report.getModel();
        model.setRowCount(0);
        report_transaksi_table();
        pendapatan();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void pencarian_transaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pencarian_transaksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pencarian_transaksiActionPerformed

    private void diskon_labelKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_diskon_labelKeyReleased
        // TODO add your handling code here:
        try {
            grand_total();
            bayar();

        } catch (Exception e) {

        }
    }//GEN-LAST:event_diskon_labelKeyReleased

    private void diskon_labelFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_diskon_labelFocusLost
        // TODO add your handling code here:
        if (diskon_label.getText().length() == 0) {
            diskon_label.setText(Integer.toString(0));
            int diskon = Integer.parseInt(diskon_label.getText());
            diskon_label.setText(Integer.toString(diskon));
            grand_total();
        }
    }//GEN-LAST:event_diskon_labelFocusLost

    private void diskon_labelFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_diskon_labelFocusGained
        // TODO add your handling code here:
        if (diskon_label.getText().equals("0")) {
            diskon_label.setText(null);
        }
    }//GEN-LAST:event_diskon_labelFocusGained

    private void transaksi_ReportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_transaksi_ReportMouseClicked
        // TODO add your handling code here:
        try {
            int baris = transaksi_Report.rowAtPoint(evt.getPoint());
            DefaultTableModel model2 = (DefaultTableModel) detail_transaksi_report.getModel();
            id_text.setText(transaksi_Report.getValueAt(baris, 0).toString());
            java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
            String sql = "SELECT * FROM detail_transaksi WHERE kode_transaksi='" + id_text.getText() + "'";
            java.sql.Statement stm = conn.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            model2.setRowCount(0);
            int i = 0;
            while (res.next()) {
                model2.addRow(new Object[]{res.getString(2),
                    res.getString(3), res.getString(4), res.getString(5)});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_transaksi_ReportMouseClicked

    private void dateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_dateMouseClicked

    private void txtDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateMouseClicked

    private void pencarian_transaksiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pencarian_transaksiKeyReleased
        // TODO add your handling code here:
        try {
            DefaultTableModel table = (DefaultTableModel) transaksi_Report.getModel();
            String search = pencarian_transaksi.getText().toString();
            TableRowSorter<DefaultTableModel> tr = new TableRowSorter<DefaultTableModel>(table);
            transaksi_Report.setRowSorter(tr);
            tr.setRowFilter(RowFilter.regexFilter(search));
        } catch (Exception e) {

        }
    }//GEN-LAST:event_pencarian_transaksiKeyReleased

    private void pencarian_transaksiFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pencarian_transaksiFocusGained
        // TODO add your handling code here:
        if (pencarian_transaksi.getText().equals("Pencarian")) {
            pencarian_transaksi.setText(null);
            pencarian_transaksi.setHorizontalAlignment(javax.swing.JLabel.LEFT);
        }
    }//GEN-LAST:event_pencarian_transaksiFocusGained

    private void pencarian_transaksiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pencarian_transaksiFocusLost
        // TODO add your handling code here:
        if (pencarian_transaksi.getText().length() == 0) {
            pencarian_transaksi.setText("Pencarian");
            pencarian_transaksi.setHorizontalAlignment(javax.swing.JLabel.LEFT);
        }
    }//GEN-LAST:event_pencarian_transaksiFocusLost

    private void txtnamamemberFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtnamamemberFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtnamamemberFocusGained

    private void txtnamamemberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtnamamemberFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtnamamemberFocusLost

    private void txttelpmemberFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txttelpmemberFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txttelpmemberFocusGained

    private void txttelpmemberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txttelpmemberFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txttelpmemberFocusLost

    private void members_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_members_tableMouseClicked
        // TODO add your handling code here:
        int baris = members_table.rowAtPoint(evt.getPoint());
        String id_member = members_table.getValueAt(baris, 1).toString();
        txtidmember.setText(id_member);
        if (members_table.getValueAt(baris, 1) == null) {
            txtidmember.setFocusable(true);
        } else {
            txtidmember.setFocusable(false);
        }
        if (members_table.getValueAt(baris, 2) == null) {
            txtnamamember.setText("");
        } else {
            txtnamamember.setText(members_table.getValueAt(baris, 2).toString());
        }
        if (members_table.getValueAt(baris, 3) == null) {
            txttelpmember.setText("");
        } else {
            txttelpmember.setText(members_table.getValueAt(baris, 3).toString());
        }
    }//GEN-LAST:event_members_tableMouseClicked

    private void txtidmemberFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtidmemberFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtidmemberFocusGained

    private void txtidmemberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtidmemberFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtidmemberFocusLost

    private void pencarian_memberKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pencarian_memberKeyReleased
        // TODO add your handling code here:
        try {
            DefaultTableModel table = (DefaultTableModel) members_table.getModel();
            String search = pencarian_member.getText().toString();
            TableRowSorter<DefaultTableModel> tr = new TableRowSorter<DefaultTableModel>(table);
            members_table.setRowSorter(tr);
            tr.setRowFilter(RowFilter.regexFilter(search));
        } catch (Exception e) {

        }
    }//GEN-LAST:event_pencarian_memberKeyReleased

    private void pencarian_memberFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pencarian_memberFocusGained
        // TODO add your handling code here:
        if (pencarian_member.getText().equals("Pencarian")) {
            pencarian_member.setText(null);
            pencarian_member.setHorizontalAlignment(javax.swing.JLabel.LEFT);
        }
    }//GEN-LAST:event_pencarian_memberFocusGained

    private void pencarian_memberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pencarian_memberFocusLost
        // TODO add your handling code here:
        if (pencarian_member.getText().length() == 0) {
            pencarian_member.setText("Pencarian");
            pencarian_member.setHorizontalAlignment(javax.swing.JLabel.LEFT);
        }

    }//GEN-LAST:event_pencarian_memberFocusLost

    private void jButton12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton12MouseClicked
        // TODO add your handling code here:
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            FileOutputStream excelFOU = null;
            BufferedOutputStream excelBOU = null;
            XSSFWorkbook excelJTableExporter = null;
            JFileChooser excelFileChooser = new JFileChooser();
            excelFileChooser.setDialogTitle("Save As");
            FileNameExtensionFilter fnef = new FileNameExtensionFilter("EXCEL FILES", "xls", "xlsx", "xlsm");
            excelFileChooser.setFileFilter(fnef);
            int excelChooser = excelFileChooser.showSaveDialog(this);
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

            if (excelChooser == JFileChooser.APPROVE_OPTION) {
                try {
                    excelJTableExporter = new XSSFWorkbook();
                    XSSFSheet excelSheet = excelJTableExporter.createSheet("JTable Sheet");
                    for (int i = 0; i < model.getRowCount(); i++) {
                        XSSFRow excelRow = excelSheet.createRow(i);
                        for (int j = 0; j < model.getColumnCount(); j++) {
                            XSSFCell excelCell = excelRow.createCell(j);

                            excelCell.setCellValue(model.getValueAt(i, j).toString());
                        }
                    }
                    excelFOU = new FileOutputStream(excelFileChooser.getSelectedFile() + ".xlsx");
                    excelBOU = new BufferedOutputStream(excelFOU);
                    excelJTableExporter.write(excelBOU);
                    JOptionPane.showMessageDialog(null, "Export Berhasil");
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if (excelBOU != null) {
                            excelBOU.close();
                        }
                        if (excelFOU != null) {
                            excelFOU.close();
                        }
                        if (excelJTableExporter != null) {
                            excelJTableExporter.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_jButton12MouseClicked

    private void addmember_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addmember_btnMouseClicked
        // TODO add your handling code here:
        if (txtidmember.getText().length() == 0 || txtnamamember.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "Mohon Masukkan id-member dan nama member");
        } else {
            try {
                String sql = "INSERT INTO  `membership`(`id_member`, `nama_member`, `nomor_telepon`) VALUES ('" + txtidmember.getText() + "','"
                        + txtnamamember.getText() + "','" + txttelpmember.getText() + "')";
                java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
                java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                pst.execute();
                JOptionPane.showMessageDialog(null, "Penyimpanan Data Berhasil");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Penambahan Data Gagal");
            }
            load_members();
            txtnamamember.setText(null);
            txtidmember.setText(null);
            txttelpmember.setText(null);
            txtidmember.setFocusable(true);
        }
    }//GEN-LAST:event_addmember_btnMouseClicked

    private void deletemember_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deletemember_btnMouseClicked
        // TODO add your handling code here:
        if (txtidmember.getText().length() == 0 && txtnamamember.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "Mohon Pilih Data Yang Ingin Dihapus");
        } else {
            try {
                String sql = "DELETE FROM membership where id_member='" + txtidmember.getText() + "'";
                java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
                java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                pst.execute();
                JOptionPane.showMessageDialog(this, "Data Berhasil Dihapus");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
        load_members();
        txtnamamember.setText(null);
        txtidmember.setText(null);
        txttelpmember.setText(null);
        txtidmember.setFocusable(true);
    }//GEN-LAST:event_deletemember_btnMouseClicked

    private void editmember_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editmember_btnMouseClicked
        // TODO add your handling code here:
        if (txtidmember.getText().length() == 0 || txtnamamember.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "Mohon Isi id-member dan nama member");
        } else {
            try {
                String sql = "UPDATE `membership`"
                        + "SET nama_member = '"
                        + txtnamamember.getText() + "',nomor_telepon= '"
                        + txttelpmember.getText()
                        + "'WHERE id_member = '" + txtidmember.getText() + "'";
                java.sql.Connection conn = (Connection) stationerypro.StationeryPro.configDB();
                java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                pst.execute();
                JOptionPane.showMessageDialog(null, "Data Berhasil Diubah");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Perubahan Data Gagal"
                        + e.getMessage());
            }
        }
        load_members();
        txtnamamember.setText(null);
        txtidmember.setText(null);
        txttelpmember.setText(null);
        txtidmember.setFocusable(true);
    }//GEN-LAST:event_editmember_btnMouseClicked

    private void clearmember_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearmember_btnMouseClicked
        // TODO add your handling code here:
        txtnamamember.setText(null);
        txtidmember.setText(null);
        txttelpmember.setText(null);
        txtidmember.setFocusable(true);
    }//GEN-LAST:event_clearmember_btnMouseClicked

    private void dashboard_icon_menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboard_icon_menuMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            setTitle("Dashboard - Stationery Pro");
            header_name.setText("DASHBOARD");
            users_tab.setSelectedIndex(0);
            dashboard_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Dashboard_Icon_Hover.png")));
            inventory_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Inventory_Icon.png")));
            member_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/member_icon.png")));
            about_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/about_icon.png")));
            report_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Report_Icon.png")));
            users_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/users_menu.png")));
        }
    }//GEN-LAST:event_dashboard_icon_menuMouseClicked

    private void inventory_icon_menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inventory_icon_menuMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            setTitle("Inventory - Stationery Pro");
            header_name.setText("INVENTORY");
            users_tab.setSelectedIndex(2);
            dashboard_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Dashboard_Icon.png")));
            inventory_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Inventory_Icon_Hover.png")));
            member_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/member_icon.png")));
            about_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/about_icon.png")));
            report_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Report_Icon.png")));
            users_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/users_menu.png")));
        }
    }//GEN-LAST:event_inventory_icon_menuMouseClicked

    private void member_menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_member_menuMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            setTitle("Members - Stationery Pro");
            users_tab.setSelectedIndex(5);
            header_name.setText("MEMBERS");
            dashboard_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Dashboard_Icon.png")));
            inventory_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Inventory_Icon.png")));
            member_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/member_icon_hover.png")));
            about_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/about_icon.png")));
            report_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Report_Icon.png")));
            users_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/users_menu.png")));
        }
    }//GEN-LAST:event_member_menuMouseClicked

    private void about_icon_menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_about_icon_menuMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            setTitle("About - Stationery Pro");
            users_tab.setSelectedIndex(4);
            header_name.setText("ABOUT");
            dashboard_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Dashboard_Icon.png")));
            inventory_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Inventory_Icon.png")));
            member_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/member_icon.png")));
            about_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/about_icon_hover.png")));
            report_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Report_Icon.png")));
            users_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/users_menu.png")));
        }
    }//GEN-LAST:event_about_icon_menuMouseClicked

    private void report_icon_menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_report_icon_menuMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            setTitle("Report - Stationery Pro");
            header_name.setText("REPORT");
            users_tab.setSelectedIndex(1);
            about_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/about_icon.png")));
            report_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Report_Icon_Hover.png")));
            users_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/users_menu.png")));
            dashboard_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Dashboard_Icon.png")));
            inventory_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Inventory_Icon.png")));
            member_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/member_icon.png")));
        }
    }//GEN-LAST:event_report_icon_menuMouseClicked

    private void users_icon_menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_users_icon_menuMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            setTitle("Users - Stationery Pro");
            users_tab.setSelectedIndex(3);
            header_name.setText("USERS");
            about_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/about_icon.png")));
            report_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Report_Icon.png")));
            users_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/users_menu_hover.png")));
            dashboard_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Dashboard_Icon.png")));
            inventory_icon_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Inventory_Icon.png")));
            member_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/member_icon.png")));
        }
    }//GEN-LAST:event_users_icon_menuMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Dashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Header;
    private javax.swing.JPanel Left_Panel;
    private javax.swing.JPanel Min_Max_Close_Panel;
    private javax.swing.JPanel Panel_Gap;
    private javax.swing.JPanel Sr_Icon_Panel;
    private javax.swing.JLabel about_PCS;
    private javax.swing.JLabel about_icon_menu;
    private javax.swing.JLabel add_button_inventory;
    private javax.swing.JButton addmember_btn;
    public static final javax.swing.JTextField bayar_textfield = new javax.swing.JTextField();
    public static final javax.swing.JTable cart = new javax.swing.JTable();
    private javax.swing.JLabel clear_button_inventory;
    private javax.swing.JButton clearmember_btn;
    private javax.swing.JLabel close_button;
    private javax.swing.JLabel dashboard_icon_menu;
    private com.raven.datechooser.DateChooser date;
    private com.raven.datechooser.DateChooser date2;
    private javax.swing.JLabel date_label;
    private javax.swing.JLabel delete_button_inventory;
    private javax.swing.JButton deletemember_btn;
    private javax.swing.JTable detail_transaksi_report;
    private javax.swing.JTextField diskon_label;
    public static final javax.swing.JLabel display_name = new javax.swing.JLabel();
    public static final javax.swing.JLabel display_picture = new javax.swing.JLabel();
    public static final javax.swing.JLabel display_position = new javax.swing.JLabel();
    private javax.swing.JLabel edit_button_inventory;
    private javax.swing.JButton editmember_btn;
    private javax.swing.JComboBox<String> filter_inventory;
    private javax.swing.JLabel foto_profil;
    private javax.swing.JTextField harga_beli_text;
    private javax.swing.JTextField harga_satuan_barang_text;
    private javax.swing.JLabel header_name;
    private javax.swing.JLabel id_member_label;
    private javax.swing.JTextField id_member_text;
    private javax.swing.JLabel id_text;
    private javax.swing.JLabel image_preview_inventory;
    private javax.swing.JLabel inventory_icon_menu;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel15;
    public static final javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator18;
    private javax.swing.JSeparator jSeparator19;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator20;
    private javax.swing.JSeparator jSeparator23;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTable jTable1;
    public static final javax.swing.JTextField kembalian_textfield = new javax.swing.JTextField();
    private javax.swing.JTextField kode_barang_dashboard;
    private javax.swing.JTextField kode_barang_text;
    public static final javax.swing.JLabel kode_staf_label = new javax.swing.JLabel();
    private javax.swing.JTextField kode_transaksi;
    private javax.swing.JLabel label_bayar;
    private javax.swing.JLabel label_kembalian;
    private javax.swing.JButton lihat_struk_button;
    public static final javax.swing.JLabel logout_button = new javax.swing.JLabel();
    private javax.swing.JPanel logout_popup;
    private javax.swing.JLabel maximize_button;
    private javax.swing.JLabel member_menu;
    private javax.swing.JTable members_table;
    private javax.swing.JLabel minimize_button;
    private javax.swing.JTextField nama_barang_dashboard;
    private javax.swing.JTextField nama_barang_text;
    private javax.swing.JLabel no_popup;
    private javax.swing.JTextField pencarian_member;
    private javax.swing.JTextField pencarian_transaksi;
    private javax.swing.JTextField pencarian_users;
    public static final javax.swing.JTextField pendapatan_txt = new javax.swing.JTextField();
    private javax.swing.JTextField quantity_dashboard;
    public static final javax.swing.JLabel report_icon_menu = new javax.swing.JLabel();
    private javax.swing.JComboBox<String> role_combobox;
    public static final javax.swing.JLabel role_label = new javax.swing.JLabel();
    private javax.swing.JTextField search_inventory;
    private javax.swing.JLabel sr_icon_label;
    private javax.swing.JTextField stok_barang_text;
    public static final javax.swing.JLabel sub_total_sum = new javax.swing.JLabel();
    private javax.swing.JTable tabel_total;
    private javax.swing.JLabel test_label;
    private javax.swing.JLabel time_label;
    public static final javax.swing.JLabel total_harga_label = new javax.swing.JLabel();
    public static final javax.swing.JTable transaksi_Report = new javax.swing.JTable();
    public static final javax.swing.JTextField txtDate = new javax.swing.JTextField();
    public static final javax.swing.JTextField txtDate2 = new javax.swing.JTextField();
    private javax.swing.JTextField txt_alamat;
    private javax.swing.JTextField txt_jabatan;
    private javax.swing.JTextField txt_kodestaf;
    private javax.swing.JTextField txt_namalengkap;
    private javax.swing.JTextField txt_namapanggilan;
    private javax.swing.JTextField txt_password;
    private javax.swing.JTextField txt_telepon;
    private javax.swing.JTextField txt_username;
    private javax.swing.JTextField txtidmember;
    private javax.swing.JTextField txtnamamember;
    private javax.swing.JTextField txttelpmember;
    public static final javax.swing.JLabel users_icon_menu = new javax.swing.JLabel();
    private javax.swing.JTabbedPane users_tab;
    private javax.swing.JTable users_table;
    private javax.swing.JLabel yes_popup;
    // End of variables declaration//GEN-END:variables
    private String imagePathStr;

    private ImageIcon ResizeImage(String imgPath) {
        ImageIcon myImage = new ImageIcon(imgPath);
        Image img = myImage.getImage();
        Image newImage = img.getScaledInstance(foto_profil.getWidth(), foto_profil.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImage);
        return image;
    }
}
