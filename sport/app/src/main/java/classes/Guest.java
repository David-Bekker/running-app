package classes;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Guest extends Data {
    protected  String username;
    protected ArrayList<Session> ses;
    protected int lvl;
    protected int xp;

    public Guest(double step_size, double weight, String username, ArrayList<Session> ses, int lvl, int xp) {
        super(step_size, weight);
        this.username = username;
        this.ses = ses;
        this.lvl = lvl;
        this.xp = xp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<Session> getSes() {
        return ses;
    }

    public void setSes(ArrayList<Session> ses) {
        this.ses = ses;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }


    @Override
    public String toString() {
        return "Guest{" +
                "step_size=" + step_size +
                ", weight=" + weight +
                ", username='" + username + '\'' +
                ", ses=" + ses +
                ", lvl=" + lvl +
                ", xp=" + xp +
                '}';
    }
}
