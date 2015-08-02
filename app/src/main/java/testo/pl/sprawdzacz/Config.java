package testo.pl.sprawdzacz;

/**
 * Created by Lukasz Marczak on 2015-08-02.
 */
public class Config {
    public static int NOTIFY_DURATION = 60;
    public static void setNotifyDuration(int minutes){
        NOTIFY_DURATION = minutes * 60;
    }

}
