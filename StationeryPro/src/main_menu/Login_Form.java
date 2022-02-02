/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main_menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTextField;
import stationerypro.StationeryPro;

/**
 *
 * @author anonim
 */
public class Login_Form extends javax.swing.JFrame {
int xmouse;
int ymouse;
    /**
     * Creates new form Login
     */
    public Login_Form() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setBackground(new Color(0,0,0,0));
        seticon();
        setTitle("Login - Stationery Pro");
        addPlaceHolderStyle(Username_Field);
        addPlaceHolderStyle(Password_Field);
        Peringatan_Warning.setVisible(false);
    }
    
    private void seticon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/Taskbar_Icon.png")));
    }
    
    private void setwarning() {
        if(Username_Field.getText().length()==0) {
            Username_Field.setText("Tolong Masukkan Username");
            Username_Field.setFocusable(false);
            addPlaceHolderStyle(Username_Field);
            Username_Field.setForeground(new Color(224,79,95));
        } if (Username_Field.getText().equals("Tolong Masukkan Username")) {
            Username_Field.setFocusable(true);
        } 
        if(Password_Field.getText().length()==0) {
            Password_Field.setText("Tolong Masukkan Password");
            Password_Field.setFocusable(false);
            addPlaceHolderStyle(Password_Field);
            Password_Field.setForeground(new Color(224,79,95));
        } if (Password_Field.getText().equals("Tolong Masukkan Password")) {
            Password_Field.setFocusable(true);
            Password_Field.setEchoChar('\u0000');
        }
    }
    
    public void login_database() {
        try {
            String sql = "SELECT * FROM akun JOIN staf ON akun.kode_staf=staf.kode_staf WHERE akun.username='"+Username_Field.getText()
                    +"'AND akun.password='"+Password_Field.getText()+"'AND akun.roles='"+"admin"+"'";
            java.sql.Connection conn=(Connection)StationeryPro.configDB();
            java.sql.PreparedStatement pst=conn.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery(sql);
            if (rs.next()) {
                if (Username_Field.getText().equals(rs.getString("username")) 
                    && Password_Field.getText().equals(rs.getString("password"))) {
                    Dashboard.report_icon_menu.setVisible(true);
                    Dashboard.users_icon_menu.setVisible(true);
                    new Dashboard().setVisible(true);
                    this.dispose();
                    Dashboard.display_name.setText(rs.getString("nama_staf"));
                    Dashboard.kode_staf_label.setText(rs.getString("kode_staf"));
                    Dashboard.display_position.setText(rs.getString("jabatan_staf"));
                    BufferedImage im = ImageIO.read(rs.getBinaryStream("foto_profil"));
//                    Dashboard.display_picture.setIcon(new ImageIcon(im));
                            ImageIcon myImage = new ImageIcon(im);
        Image img = myImage.getImage();
        Image newImage = img.getScaledInstance(Dashboard.display_picture.getWidth(), Dashboard.display_picture.getHeight(), Image.SCALE_SMOOTH);
//        ImageIcon image = new ImageIcon(newImage);
                    Dashboard.display_picture.setIcon(new ImageIcon(newImage));
//                    foto_profil.setHorizontalAlignment(javax.swing.JLabel.CENTER);
//                    foto_profil.setVerticalAlignment(javax.swing.JLabel.CENTER);
//                    Dashboard.display_picture.setIcon(new javax.swing.ImageIcon(getClass().getResource(rs.getString("foto_profil"))));
                } 
            } else if (Username_Field.getText().length()==0 || Username_Field.getText().equals("Tolong Masukkan Username")) {
                Peringatan_Warning.setVisible(false);
            } else if (Password_Field.getText().length()==0 || Password_Field.getText().equals("Tolong Masukkan Password")) {
                Peringatan_Warning.setVisible(false);
            } else {
                Peringatan_Warning.setVisible(true);
            }
        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    public void login_database_user() {
        try {
            String sql = "SELECT * FROM akun JOIN staf ON akun.kode_staf=staf.kode_staf WHERE akun.username='"+Username_Field.getText()
                    +"'AND akun.password='"+Password_Field.getText()+"'AND akun.roles='"+"user"+"'";
            java.sql.Connection conn=(Connection)StationeryPro.configDB();
            java.sql.PreparedStatement pst=conn.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery(sql);
            if (rs.next()) {
                if (Username_Field.getText().equals(rs.getString("username")) 
                    && Password_Field.getText().equals(rs.getString("password"))) {
                    new Dashboard().setVisible(true);
                    this.dispose();
                    Dashboard.role_label.setText("USER");
                    Dashboard.report_icon_menu.setVisible(false);
                    Dashboard.users_icon_menu.setVisible(false);
                    Dashboard.display_name.setText(rs.getString("nama_staf"));
                    Dashboard.kode_staf_label.setText(rs.getString("kode_staf"));
                    Dashboard.display_position.setText(rs.getString("jabatan_staf"));
                    BufferedImage im = ImageIO.read(rs.getBinaryStream("foto_profil"));
                    
                                                ImageIcon myImage = new ImageIcon(im);
        Image img = myImage.getImage();
        Image newImage = img.getScaledInstance(Dashboard.display_picture.getWidth(), Dashboard.display_picture.getHeight(), Image.SCALE_SMOOTH);
//        ImageIcon image = new ImageIcon(newImage);
                    Dashboard.display_picture.setIcon(new ImageIcon(newImage));
//                    Dashboard.display_picture.setIcon(new ImageIcon(im));
//                    Dashboard.display_picture.setIcon(new javax.swing.ImageIcon(getClass().getResource(rs.getString("foto_profil"))));
                } 
            } else if (Username_Field.getText().length()==0 || Username_Field.getText().equals("Tolong Masukkan Username")) {
                Peringatan_Warning.setVisible(false);
            } else if (Password_Field.getText().length()==0 || Password_Field.getText().equals("Tolong Masukkan Password")) {
                Peringatan_Warning.setVisible(false);
            } else {
                Peringatan_Warning.setVisible(true);
            }
        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    public void addPlaceHolderStyle(JTextField textField) {
        Font font = textField.getFont();
        font = font.deriveFont(font.ITALIC);
        textField.setFont(font);
        textField.setForeground(new Color(153,153,153));
    }
    
    public void removePlaceHolderStyle(JTextField textField) {
        Font font = textField.getFont();
        font = font.deriveFont(font.PLAIN|Font.BOLD);
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

        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        Password_Field = new javax.swing.JPasswordField();
        Username_Field = new javax.swing.JTextField();
        Minimize_Button = new javax.swing.JLabel();
        Close_Button = new javax.swing.JLabel();
        Hide_Unhide = new javax.swing.JLabel();
        Login_Button = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        Peringatan_Warning = new javax.swing.JLabel();
        Background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAutoRequestFocus(false);
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

        jLabel5.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Lupa Password ?");
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel5MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel5MouseExited(evt);
            }
        });
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 510, 100, -1));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("LOGIN");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 5, 40, 30));

        jLabel2.setFont(new java.awt.Font("Impact", 1, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 224, 96));
        jLabel2.setText("Sr");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 410, 330, 10));
        getContentPane().add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 300, 330, 10));

        Password_Field.setBackground(new java.awt.Color(37, 40, 55));
        Password_Field.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        Password_Field.setForeground(new java.awt.Color(153, 153, 153));
        Password_Field.setText("Masukkan Password");
        Password_Field.setBorder(null);
        Password_Field.setEchoChar('\u0000');
        Password_Field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                Password_FieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                Password_FieldFocusLost(evt);
            }
        });
        Password_Field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Password_FieldKeyPressed(evt);
            }
        });
        getContentPane().add(Password_Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 390, 330, 20));

        Username_Field.setBackground(new java.awt.Color(37, 40, 55));
        Username_Field.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        Username_Field.setForeground(new java.awt.Color(153, 153, 153));
        Username_Field.setText("Masukkan Username");
        Username_Field.setBorder(null);
        Username_Field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                Username_FieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                Username_FieldFocusLost(evt);
            }
        });
        Username_Field.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Username_FieldMouseClicked(evt);
            }
        });
        Username_Field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Username_FieldActionPerformed(evt);
            }
        });
        Username_Field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Username_FieldKeyPressed(evt);
            }
        });
        getContentPane().add(Username_Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 280, 330, 20));

        Minimize_Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Minimize_Button.png"))); // NOI18N
        Minimize_Button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Minimize_Button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Minimize_ButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Minimize_ButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                Minimize_ButtonMouseExited(evt);
            }
        });
        getContentPane().add(Minimize_Button, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 0, -1, 40));

        Close_Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Close_Button.png"))); // NOI18N
        Close_Button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Close_Button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Close_ButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Close_ButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                Close_ButtonMouseExited(evt);
            }
        });
        getContentPane().add(Close_Button, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 0, -1, 40));

        Hide_Unhide.setForeground(new java.awt.Color(255, 255, 255));
        Hide_Unhide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Hide_Button.png"))); // NOI18N
        Hide_Unhide.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Hide_Unhide.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Hide_UnhideMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                Hide_UnhideMouseReleased(evt);
            }
        });
        getContentPane().add(Hide_Unhide, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 390, 20, 20));

        Login_Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Login_Button.png"))); // NOI18N
        Login_Button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Login_Button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Login_ButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Login_ButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                Login_ButtonMouseExited(evt);
            }
        });
        Login_Button.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Login_ButtonKeyPressed(evt);
            }
        });
        getContentPane().add(Login_Button, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 460, -1, 30));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Password");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 360, -1, -1));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Username");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 250, -1, -1));

        Peringatan_Warning.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        Peringatan_Warning.setForeground(new java.awt.Color(224, 79, 95));
        Peringatan_Warning.setText("Username Atau Password Salah !");
        getContentPane().add(Peringatan_Warning, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 430, 330, -1));

        Background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Login_Background.png"))); // NOI18N
        getContentPane().add(Background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 460, 540));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Close_ButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Close_ButtonMouseClicked
        // TODO add your handling code here:
        if (evt.getButton()==MouseEvent.BUTTON1) {
        System.exit(0);
        }
    }//GEN-LAST:event_Close_ButtonMouseClicked

    private void Close_ButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Close_ButtonMouseEntered
        // TODO add your handling code here:
        Close_Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Close_Icon_Hover.png")));
    }//GEN-LAST:event_Close_ButtonMouseEntered

    private void Close_ButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Close_ButtonMouseExited
        // TODO add your handling code here:
        Close_Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Close_Button.png")));
    }//GEN-LAST:event_Close_ButtonMouseExited

    private void Minimize_ButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Minimize_ButtonMouseClicked
        // TODO add your handling code here:
        if (evt.getButton()==MouseEvent.BUTTON1) {
        this.setState(JFrame.ICONIFIED);
        }
    }//GEN-LAST:event_Minimize_ButtonMouseClicked

    private void Minimize_ButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Minimize_ButtonMouseEntered
        // TODO add your handling code here:
        Minimize_Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Minimize_Icon_Hover.png")));
    }//GEN-LAST:event_Minimize_ButtonMouseEntered

    private void Minimize_ButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Minimize_ButtonMouseExited
        // TODO add your handling code here:
        Minimize_Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Minimize_Button.png")));
    }//GEN-LAST:event_Minimize_ButtonMouseExited

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        // TODO add your handling code here:
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        
        this.setLocation(x - xmouse, y - ymouse);
        System.out.println(x + "," + y);
    }//GEN-LAST:event_formMouseDragged

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        // TODO add your handling code here:
        xmouse = evt.getX();
        ymouse = evt.getY();        
    }//GEN-LAST:event_formMousePressed

    private void Username_FieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Username_FieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Username_FieldActionPerformed

    private void Username_FieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_Username_FieldFocusGained
        // TODO add your handling code here:
        if(Username_Field.getText().equals("Masukkan Username")||Username_Field.getText().equals("Tolong Masukkan Username")) {
            Username_Field.setText(null);
//            Username_Field.setFocusable(true);
//            Username_Field.requestFocus();
            removePlaceHolderStyle(Username_Field);
        }
    }//GEN-LAST:event_Username_FieldFocusGained

    private void Username_FieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_Username_FieldFocusLost
        // TODO add your handling code here:
        if(Username_Field.getText().length()==0) {
            Username_Field.setText("Masukkan Username");
            addPlaceHolderStyle(Username_Field);
        }
    }//GEN-LAST:event_Username_FieldFocusLost

    private void Password_FieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_Password_FieldFocusGained
        // TODO add your handling code here:
        if(Password_Field.getText().equals("Masukkan Password")||Password_Field.getText().equals("Tolong Masukkan Password")) {
            Password_Field.setText(null);
//            Password_Field.requestFocus();
            Password_Field.setEchoChar('*');
            removePlaceHolderStyle(Password_Field);
        }
    }//GEN-LAST:event_Password_FieldFocusGained

    private void Password_FieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_Password_FieldFocusLost
        // TODO add your handling code here:
        if(Password_Field.getText().length()==0) {
            Password_Field.setText("Masukkan Password");
            addPlaceHolderStyle(Password_Field);
            Password_Field.setEchoChar('\u0000');
        }
    }//GEN-LAST:event_Password_FieldFocusLost

    private void Hide_UnhideMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Hide_UnhideMousePressed
        // TODO add your handling code here:
        if(evt.getButton()==MouseEvent.BUTTON1) {
        Hide_Unhide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Unhide_Button.png")));
        if(Password_Field.getText().length()>0) {
            Password_Field.requestFocus();
            Password_Field.setEchoChar('\u0000');
            removePlaceHolderStyle(Password_Field);
        }            
        }
    }//GEN-LAST:event_Hide_UnhideMousePressed

    private void Hide_UnhideMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Hide_UnhideMouseReleased
        // TODO add your handling code here:
        if(evt.getButton()==MouseEvent.BUTTON1) {
        Hide_Unhide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Hide_Button.png")));
        if(Password_Field.getText().length()>0) {
        Password_Field.setEchoChar('*');
        removePlaceHolderStyle(Password_Field);            
        }
        }
    }//GEN-LAST:event_Hide_UnhideMouseReleased

    private void Login_ButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Login_ButtonMouseEntered
        // TODO add your handling code here:
        Login_Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Login_Button_Hover.png")));
    }//GEN-LAST:event_Login_ButtonMouseEntered

    private void Login_ButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Login_ButtonMouseExited
        // TODO add your handling code here:
        Login_Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Login_Button.png")));
    }//GEN-LAST:event_Login_ButtonMouseExited

    private void Login_ButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Login_ButtonMouseClicked
        // TODO add your handling code here:
        if (evt.getButton()==MouseEvent.BUTTON1) { 
            login_database();
            setwarning();
            login_database_user();
        }
    }//GEN-LAST:event_Login_ButtonMouseClicked

    private void Login_ButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Login_ButtonKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Login_ButtonKeyPressed

    private void Password_FieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Password_FieldKeyPressed
        // TODO add your handling code here:
//        if(Username_Field.getText().length()>0 && Password_Field.getText().length()>0) {
            if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
                login_database();
                setwarning();
                login_database_user();
            }
//        }
    }//GEN-LAST:event_Password_FieldKeyPressed

    private void Username_FieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Username_FieldKeyPressed
        // TODO add your handling code here:
//        if(Username_Field.getText().length()>0 && Password_Field.getText().length()>0) {
            if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
                login_database();
                setwarning();
                login_database_user();
            }
//        }
    }//GEN-LAST:event_Username_FieldKeyPressed

    private void Username_FieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Username_FieldMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_Username_FieldMouseClicked

    private void jLabel5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseEntered
        // TODO add your handling code here:
        jLabel5.setForeground(new Color (240,224,96));
    }//GEN-LAST:event_jLabel5MouseEntered

    private void jLabel5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseExited
        // TODO add your handling code here:
        jLabel5.setForeground(Color.white);
    }//GEN-LAST:event_jLabel5MouseExited

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        // TODO add your handling code here:
        new sub_menu.Lupa_Password().setVisible(true);
    }//GEN-LAST:event_jLabel5MouseClicked

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
            java.util.logging.Logger.getLogger(Login_Form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login_Form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login_Form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login_Form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login_Form().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Background;
    private javax.swing.JLabel Close_Button;
    private javax.swing.JLabel Hide_Unhide;
    private javax.swing.JLabel Login_Button;
    private javax.swing.JLabel Minimize_Button;
    private javax.swing.JPasswordField Password_Field;
    private javax.swing.JLabel Peringatan_Warning;
    private javax.swing.JTextField Username_Field;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    // End of variables declaration//GEN-END:variables
}
