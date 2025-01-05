package core;

import javax.swing.*;

public class Helper {
    public static void setTheme(){
        for (UIManager.LookAndFeelInfo info: UIManager.getInstalledLookAndFeels()){
        if (info.getName().equals("Nimbus")){
            try {
                UIManager.setLookAndFeel(info.getClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            }
            break;
        }
        }
    }

    public static boolean ifFieldEmpty(JTextField field){
        return field.getText().trim().isEmpty();
    }

    public static boolean isFieldListEmpty(JTextField[] fields){
     for (JTextField field: fields){
         if (ifFieldEmpty(field)) return true;
     }
     return false;
    }

    public static boolean isEmailValid(String mail){
        if (mail == null || mail.trim().isEmpty()) return false;
        if (!mail.contains("@")) return false;

        String[] parts = mail.split("@");

        if (parts.length != 2) return false;
        if (parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) return false;
        if (!parts[1].contains(".")) return false;

        return true;
    }

    public static void optionPanelDialogTR(){
        UIManager.put("OptionPane.okButtonText", "Tamam");
        UIManager.put("OptionPane.okButtonText", "Evet");
        UIManager.put("OptionPane.noButtonText", "Hayır");
    }

    public static void showMsg(String message){
        optionPanelDialogTR();
        String msg;
        String title = switch (message) {
            case "fill" -> {
                msg = "Lütfen tüm alanları doldurun!";
                yield "HATA!";
            }
            case "done" -> {
                msg = "İşlem Başarılı Hoşgeldiniz";
                yield "Sonuç";
            }
            case "eror" -> {
                msg = "Bir hata oluştu!";
                yield "HATA!";
            }
            default -> {
                msg = message;
                yield "Mesaj";
            }
        };

        JOptionPane.showMessageDialog(null,msg,title,JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean isFieldEmpty(JTextField fldCustomerMail) {
        return false;
    }
    public static boolean confirm(String str){
        optionPanelDialogTR();
        String msg;

        if (str.equals("sure")){
            msg = "Sİlmek istediğinize emin misiniz!";
        }else {
            msg= str;
        }

        return JOptionPane.showConfirmDialog(null,msg,"Emin misin?",JOptionPane.YES_NO_OPTION) == 0;
    }
}
