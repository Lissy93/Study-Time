package net.as93.studytime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Field;


public class MainActivity extends Activity {

    public static TimerSettings ts;

    NumberPicker npHours;
    NumberPicker npMins;
    TextView txtCountdown;
    Button btnClose;
    static LinearLayout container_finished;
    TextView txtHowToExit;

    Context c;

    private static final int MY_NOTIFICATION_ID=1;
    private NotificationManager notificationManager;
    private Notification myNotification;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
        if (firstrun){
            new AlertDialog.Builder(this).setTitle("Welcome to Study Time!").setMessage("IMPORTANT: After clicking 'Study' for the first time, you will see a dialog asking you to select a launcher. Choose 'Study Time' and then press 'Always', (the app will not function correctly if this is not done). \n" +
                    "This won't interfere with the normal use of your phone while the apps not running, and you only need to do it once.").setNeutralButton("OK", null).show();            // Save the state
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstrun", false)
                    .commit();
        }

        c = getApplicationContext();

        ts = new TimerSettings(getApplicationContext());

        setUpGui();

        handler = new Handler();

        if(ts.isTimerRunning()) {
            decrementTime();
        }



    }


    public void decrementTime() {
        if(ts.getRemainingTime()>0){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ts.decrementTimer(60);
                    txtCountdown.setText(formatMinutes(ts.getRemainingTime()) + "");
                    decrementTime();
                }
            }, 60000);
        }
        else{
            finishedSession();
        }


    }


    @Override
    public boolean isFinishing() {
        ts.createTimerSettings(getNumberPickerTime());
        return super.isFinishing();
    }

    @Override
    public void onBackPressed() {
        if(!ts.isTimerRunning()) {
//            super.onBackPressed();
            getPackageManager().clearPackagePreferredActivities(getPackageName());
            NotificationManager notifManager= (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            notifManager.cancelAll();
            finish();
            System.exit(0);
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void setUpGui(){


        /* Timer Class */


        /* Make full screen */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        /* Labels + Text */
        TextView txtHours = (TextView) findViewById(R.id.txtHours);
        TextView txtColon = (TextView) findViewById(R.id.txtColon);
        TextView txtMinutes = (TextView) findViewById(R.id.txtMinutes);
        TextView txtColon2 = (TextView) findViewById(R.id.txtColon2);
        txtCountdown = (TextView) findViewById((R.id.txtCountDownTimer));
        txtHowToExit = (TextView) findViewById(R.id.howToExit);

        /* Buttons */
        Button btnStart = (Button) findViewById(R.id.btnStart);
        btnClose = (Button) findViewById(R.id.btnClose);

        /* Containers */
        final TableRow contTimeLabels = (TableRow) findViewById(R.id.container_timeLabels);
        final TableRow contTimeSet    = (TableRow) findViewById(R.id.container_time);
        final LinearLayout contStartButtons = (LinearLayout) findViewById(R.id.container_startButtons);
        final TableRow contCountdown = (TableRow) findViewById(R.id.container_countdown);
        container_finished = (LinearLayout) findViewById(R.id.container_finished);

        /* Other components */
        npHours = (NumberPicker) findViewById(R.id.numberpicker_hours);
        npMins = (NumberPicker) findViewById(R.id.numberpicker_mins);
        final CheckBox chkShowExit = (CheckBox) findViewById(R.id.chkExit);


        /* Set fonts */
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Walkway.ttf");
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setTypeface(font);
        txtHours.setTypeface(font);
        txtColon.setTypeface(font);
        txtMinutes.setTypeface(font);
        txtColon2.setTypeface(font);
        btnStart.setTypeface(font);
        btnClose.setTypeface(font);
        txtCountdown.setTypeface(font);



        /* Set up number pickers */
        setNumberPickerTextColor(npHours, Color.argb(250, 250, 250, 250));
        npHours.setMaxValue(23);
        npHours.setMinValue(0);
        npHours.setWrapSelectorWheel(true);
        setNumberPickerTextColor(npMins, Color.argb(250, 250, 250, 250));

        String[] values=new String[6];
        for(int i=0;i<values.length;i++){
            values[i]=Integer.toString(i*10);
        }
        npMins.setMaxValue(values.length-1);
        npMins.setMinValue(0);
        npMins.setDisplayedValues(values);


        npMins.setWrapSelectorWheel(true);


        if(ts.isTimerRunning()){
            intoStudyMode(contTimeLabels, contTimeSet, contStartButtons, btnClose, contCountdown, getApplicationContext());
        }


        /* Start Button */
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(npHours.getValue()==0&&npMins.getValue()==0){
                    Toast.makeText(getApplicationContext(), "Please select an amount of time first!", Toast.LENGTH_SHORT).show();
                }

                else {
                    /* Call Launcher change */
                    PackageManager pm = getPackageManager();
                    ComponentName cn1 = new ComponentName("net.as93.studytime", "net.as93.studytime.LauncherAlias1");
                    ComponentName cn2 = new ComponentName("net.as93.studytime", "net.as93.studytime.LauncherAlias2");
                    int dis = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                    if (pm.getComponentEnabledSetting(cn1) == dis) dis = 3 - dis;
                    pm.setComponentEnabledSetting(cn1, dis, PackageManager.DONT_KILL_APP);
                    pm.setComponentEnabledSetting(cn2, 3 - dis, PackageManager.DONT_KILL_APP);

                    /* Set default launcher */
                    Toast.makeText(getApplicationContext(), "Select 'Study Time' then press 'Always'", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent, "Select Study Time to continue"));
                    startActivityForResult(intent, 1);

                    /* Enter study  mode */
                    int totalSeconds = getNumberPickerTime();
                    if(chkShowExit.isChecked()){ ts.setShowExit(true);  }
                    else{  ts.setShowExit(false); }

                    intoStudyMode(contTimeLabels, contTimeSet, contStartButtons, btnClose, contCountdown, getApplicationContext());
                    ts.createTimerSettings(totalSeconds);
                    txtCountdown.setText("Study Time needs to be set as default first");

                }
            }
        });


        /* Exit Button */
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ts.cancelTimer(); // Kill timer
                getPackageManager().clearPackagePreferredActivities(getPackageName());
                NotificationManager notifManager= (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
                notifManager.cancelAll();
                finish();
                System.exit(0);
            }
        });

    }


    public int getNumberPickerTime() {

        return (npHours.getValue() * 3600) +  (npMins.getValue() * 600);
    }

    public int getCurrentTime(){
        return 0;
    }


    /**
     * Hides the set timer controlls. Shows the countdown timer.
     * @param contTimeLabels
     * @param contTimeSet
     * @param contStartButtons
     */
    public void intoStudyMode(TableRow contTimeLabels, TableRow contTimeSet, LinearLayout contStartButtons, Button btnExit, TableRow contCountdown, Context c){
        /* Hide timer controls  */
        contTimeLabels.setVisibility(View.GONE);
        contTimeSet.setVisibility(View.GONE);
        contStartButtons.setVisibility(View.GONE);
        txtHowToExit.setVisibility(View.GONE);

        /* Show timer */
        contCountdown.setVisibility(View.VISIBLE);

        /* Maybe hide exit button */
        if(!ts.isShowExit()){
            btnExit.setVisibility(View.GONE);
        }

        /* Set Text */
        txtCountdown.setText(formatMinutes(ts.getRemainingTime()));

        /* Show Notifications */
        showNotification();

        /* Mute Volume */
        AudioManager audio = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
        audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);


    }



    public void showNotification(){

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myNotification = new Notification(R.drawable.launcher_ico, "Study Mode Enabled", System.currentTimeMillis());
        Context context = getApplicationContext();
        String notificationTitle = "Exit Study Time?";
        String notificationText = "Click here to quit your study session";
        Intent myIntent = new Intent(this, ConfirmExit.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0,   myIntent, Intent.FILL_IN_ACTION);
        myNotification.flags |= Notification.FLAG_ONGOING_EVENT ;
        myNotification.setLatestEventInfo(context, notificationTitle, notificationText, pendingIntent);

        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
    }



    public void confirmExit(){
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }





    /**
     * Hides and sets to zero the coutdown timer. Shows the set timer controlls.
     */
    public static void outOfStudyMode(TableRow contTimeLabels, TableRow contTimeSet, LinearLayout contStartButtons, TableRow contCountdown, Context c){
        /* Hide timer controls  */
        contTimeLabels.setVisibility(View.VISIBLE);
        contTimeSet.setVisibility(View.VISIBLE);
        contStartButtons.setVisibility(View.VISIBLE);

        /* Show timer */
        contCountdown.setVisibility(View.GONE);

    }


    /**
     * Hides and sets to zero the coutdown timer. Shows the set timer controlls.
     */
    public void finishedSession(){
        container_finished.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);
        AudioManager audio = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
        audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    public String formatMinutes(int t){
        Double dHours =Math.floor((t/60) / 60);
        int hours = dHours.intValue();
        int minutes = ((t/60)-(hours*60));
        return String.format("%dh : %02dm", hours, minutes);
    }

    /**
     *
     * @param numberPicker
     * @param color
     * @return
     */
 static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalAccessException e){
                    Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalArgumentException e){
                    Log.w("setNumberPickerTextColor", e);
                }
            }
        }
        return false;
    }
}
