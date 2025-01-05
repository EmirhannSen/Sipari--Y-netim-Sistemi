import business.UserController;
import core.Helper;
import entity.User;
import view.DashboardUI;
import view.LoginUI;

public class Ap {
    public Ap() {
    }

    public static void main(String[] args) {

        Helper.setTheme();
        //LoginUI loginUI = new LoginUI();

        UserController userController = new UserController();
        User user = userController.findByLogin("emirhan.sen1@ogr.gelisim.edu.tr","2387577");
        DashboardUI dashboardUI = new DashboardUI(user);
    }
}
