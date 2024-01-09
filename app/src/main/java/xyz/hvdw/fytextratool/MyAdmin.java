package xyz.hvdw.fytextratool;

import androidx.annotation.NonNull;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by ssaurel on 04/09/2017.
 */
public class MyAdmin extends DeviceAdminReceiver {

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        Toast.makeText(context, context.getString(R.string.admin_message_enabled), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        Toast.makeText(context, context.getString(R.string.admin_message_disabled), Toast.LENGTH_SHORT).show();
    }
}
