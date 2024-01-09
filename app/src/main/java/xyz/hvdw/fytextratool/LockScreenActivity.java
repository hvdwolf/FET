package xyz.hvdw.fytextratool;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import p32929.easypasscodelock.Utils.EasyLock;
import p32929.easypasscodelock.Utils.LockscreenHandler;

public class LockScreenActivity extends LockscreenHandler {
    private HomeButtonReceiver homeButtonReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockscreen);


        // Register the BroadcastReceiver to capture the HOME button press
        homeButtonReceiver = new HomeButtonReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(homeButtonReceiver, intentFilter);
        Logger.logToFile("Trying to register the homekey receiver");

        //EasyLock.setBackgroundColor(Color.lightGreen);
        //EasyLock.setBackgroundColor(getResources().getColor(R.color.lightGreen));
        //EasyLock.checkPassword(this);

/*        boolean isActive = devicePolicyManager.isAdminActive(compName);
        disable.setVisibility(isActive ? View.VISIBLE : View.GONE);
        enable.setVisibility(isActive ? View.GONE : View.VISIBLE); */

        EasyLock.forgotPassword(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LockScreenActivity.this, "Clicked on forgot password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver when the activity is destroyed
        unregisterReceiver(homeButtonReceiver);
    }

    private class HomeButtonReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra("reason");
                if (reason != null && reason.equals("homekey")) {
                    Logger.logToFile("User pressed homekey from lockscreen.");
                    // Handle the HOME button press which means we do nothing here
                }
            }
        }
    }



    @Override
    public void onBackPressed() {
        // Disable the back button
        //Toast.makeText(this, "Back button disabled", Toast.LENGTH_SHORT).show();
        //Log.i("Back button disabled");
        // Next line to completely ignore the back button
        //super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        // Disable the home button
        //Toast.makeText(this, "Home button disabled", Toast.LENGTH_SHORT).show();
        //Log.i("Home button disabled");
    }

    public void setPass(View view) {
        EasyLock.setPassword(this, TestActivity.class);
    }

    public void changePass(View view) {
        EasyLock.changePassword(this, TestActivity.class);
    }

    public void disable(View view) {
        EasyLock.disablePassword(this, TestActivity.class);
    }

    public void checkPass(View view) {
        EasyLock.checkPassword(this);
    }


}
