package sub_menu;
import java.sql.Connection;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;
import stationerypro.StationeryPro;
public class javaMailUtil {
    public static void sendMail (String recepient) throws Exception {
        System.out.println("Preparing to send message");
        Properties properties = new Properties();
        
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        
        String myAccountEmail = "stationerypro.official@gmail.com";
        String password = "aremafire778";
        
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected  PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccountEmail, password);
            }
});
        Message message = prepareMessage(session, myAccountEmail, recepient);
        
        Transport.send(message);
        System.out.println("message sent successfully");
    }
    private static Message prepareMessage(Session session, String myAccountEmail, String recepient) {
        try {
            
            String sql = "SELECT * FROM akun WHERE username='"+Lupa_Password.txt_username.getText()
                    +"'AND roles='"+"admin"+"'";
            java.sql.Connection conn=(Connection)StationeryPro.configDB();
            java.sql.PreparedStatement pst=conn.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery(sql);
            while (rs.next()) {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myAccountEmail));
            message.setRecipient(Message.RecipientType.TO,new InternetAddress(recepient));
            message.setSubject("Lupa Kata Sandi - Stationery Pro");
            message.setText("Username : " + rs.getString("username")+"\n"+ "Password : "+rs.getString("password"));
            return message;
                    
                }
            
        } catch (Exception ex) {
            Logger.getLogger(javaMailUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
