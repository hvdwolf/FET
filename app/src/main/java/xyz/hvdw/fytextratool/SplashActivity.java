package xyz.hvdw.fytextratool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.topjohnwu.superuser.Shell;

/** Based on https://github.com/topjohnwu/libsu:
 * An Android library providing a complete solution for apps using root permissions.
 * libsu comes with 2 main components:
 * the core module handles the creation of the Unix (root) shell process and wraps it with high level, robust Java APIs;
 * the service module handles the launching, binding, and management of root services over IPC,
 * Only the core is used in FET.
 * Similar to threads where there is a special "main thread", libsu also has the concept of the "main shell".
 * For each process, there is a single globally shared "main shell" that is constructed on-demand and cached.
 * Set default configurations before the main Shell instance is created
 *
 * Currently not used
 */

public class SplashActivity extends Activity {

    static {
        // Set settings before the main shell can be created
        //Shell.enableVerboseLogging = BuildConfig.DEBUG;
        Shell.setDefaultBuilder(Shell.Builder.create()
            .setFlags(Shell.FLAG_REDIRECT_STDERR)
            .setTimeout(10)
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Preheat the main root shell in the splash screen
        // so the app can use it afterwards without interrupting
        // application flow (e.g. root permission prompt)
        Shell.getShell(shell -> {
            // The main shell is now constructed and cached
            // Exit splash screen and enter main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
