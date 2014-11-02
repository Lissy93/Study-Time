package net.as93.studytime;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Alicia on 01/11/2014.
 */
public class TimerSettings {

    public static final String COUNTDOWN = "countdown";
    private SharedPreferences.Editor timerEditor;
    private SharedPreferences pref;


    public TimerSettings(Context c){
        pref = c.getSharedPreferences("net.as93.studytime.pref", c.MODE_PRIVATE);
        timerEditor = pref.edit();
    }

    public boolean isShowExit() {
        return pref.getBoolean("show.exit", true);
    }

    public void setShowExit(boolean showExit) {
        timerEditor.putBoolean("show.exit", showExit);
        timerEditor.commit();
        timerEditor.apply();
    }

    public void decrementTimer(){
        timerEditor.putInt(COUNTDOWN, pref.getInt(COUNTDOWN, 0)-1);
        timerEditor.commit();
        timerEditor.apply();
    }

    public void decrementTimer(int amount){
        timerEditor.putInt(COUNTDOWN, pref.getInt(COUNTDOWN, 0)-amount);
        timerEditor.commit();
        timerEditor.apply();
    }
    public boolean isTimerRunning(){
        int timeRemaining  = pref.getInt(COUNTDOWN, 0);
        if(timeRemaining > 0){
            return true;
        }
        return false;
    }


    public void createTimerSettings(int timeRemaining){
        timerEditor.putInt(COUNTDOWN, timeRemaining);
        timerEditor.commit();
        timerEditor.apply();
    }

    public  int getRemainingTime(){
        return pref.getInt(COUNTDOWN, 0);
    }

    public void cancelTimer(){
        timerEditor.clear();
        timerEditor.commit();
    }

}
