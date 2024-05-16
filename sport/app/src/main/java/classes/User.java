package classes;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class User extends Guest {
    private String password;
    private String phone;

    public User(double step_size, double weight, String username, ArrayList<Session> ses, int lvl, int xp, String password, String phone) {
        super(step_size, weight, username, ses, lvl, xp);
        this.password = password;
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User{" +
                "step_size=" + step_size +
                ", weight=" + weight +
                ", username='" + username + '\'' +
                ", ses=" + ses +
                ", lvl=" + lvl +
                ", xp=" + xp +
                ", password='" + password + '\'' +
                '}';
    }
}
