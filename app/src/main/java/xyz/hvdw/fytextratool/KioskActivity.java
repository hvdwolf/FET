package xyz.hvdw.fytextratool;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class KioskActivity extends Activity {

    private TextView clockTextView;
    private Handler handler;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk);

        clockTextView = findViewById(R.id.clockTextView);
        handler = new Handler();
        dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        // Start a runnable to update the clock display every second
        handler.post(updateClockRunnable);
    }

    private Runnable updateClockRunnable = new Runnable() {
        @Override
        public void run() {
            // Update the clock display with the current time
            String currentTime = dateFormat.format(new Date());
            clockTextView.setText(currentTime);

            // Schedule the next update after 1 second
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to prevent memory leaks
        handler.removeCallbacks(updateClockRunnable);
    }

    /*
    @Override
    public void onBackPressed() {
        // Disable the back button
        //Toast.makeText(this, "Back button disabled", Toast.LENGTH_SHORT).show();
        //Log.i("Back button disabled");
        // Next line to completely ignore the back button
        super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        // Disable the home button
        //Toast.makeText(this, "Home button disabled", Toast.LENGTH_SHORT).show();
        //Log.i("Home button disabled");
    }
     */
}
