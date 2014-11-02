package net.as93.studytime;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class ConfirmExit extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_exit);

        TextView txtConfirmExit = (TextView) findViewById(R.id.txtConfirmExit);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Walkway.ttf");
        txtConfirmExit.setTypeface(font);

        Button btnConfirmYes = (Button) findViewById(R.id.btnConfirmYes);
        Button btnConfirmNo = (Button) findViewById(R.id.btnConfirmNo);


        /* Exit Button */
        btnConfirmYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerSettings ts = new TimerSettings(getApplicationContext());
                ts.cancelTimer(); // Kill timer
                getPackageManager().clearPackagePreferredActivities(getPackageName());
                NotificationManager notifManager= (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notifManager.cancelAll();
                finish();
                System.exit(0);

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);

                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
            }
        });

        /* Close Dialog  */
        btnConfirmNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }

    protected void onNewIntent(Intent intent) {
        Toast.makeText(getApplicationContext(), "I do like my bread... TOASTED!", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.confirm_exit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
