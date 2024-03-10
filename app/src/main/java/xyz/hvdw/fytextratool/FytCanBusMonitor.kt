package xyz.hvdw.fytextratool;

import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.aoe.fytcanbusmonitor.ModuleCodes.MODULE_CODE_CANBUS
import com.aoe.fytcanbusmonitor.ModuleCodes.MODULE_CODE_CAN_UP
import com.aoe.fytcanbusmonitor.ModuleCodes.MODULE_CODE_MAIN
import com.aoe.fytcanbusmonitor.ModuleCodes.MODULE_CODE_SOUND
import com.aoe.fytcanbusmonitor.MsToolkitConnection

class FytCanBusMonitor : AppCompatActivity(), ComponentCallbacks2 {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getStringExtra("OPTION").equals("GENERAL")) {
            setContentView(R.layout.activity_fytcanbusmonitor)

            ModuleCallback.init(this)
            if (intent.getBooleanExtra("MAIN", true)) {
                connectMain()
            }
            if (intent.getBooleanExtra("CANBUS", false)) {
                connectCanbus()
            }
            if (intent.getBooleanExtra("SOUND", false)) {
                connectSound()
            }
            if (intent.getBooleanExtra("CANUP", false)) {
                connectCanUp()
            }
        } else { // MAIN_ONLY uses a limited outputs only
            setContentView(R.layout.activity_fyt_main_monitor)

            ModuleCallback.init(this)
            connectMain()
        }

        val title = intent.getStringExtra("TITLE")
        supportActionBar?.title = title

        MsToolkitConnection.instance.connect(this)
    }
    // Make multi-window (split-screen) aware
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Check if the activity is in multi-window mode
        val isInMultiWindowMode = isInMultiWindowMode()

        if (isInMultiWindowMode) {
            // The activity is in multi-window mode
            // Handle multi-window mode specific behavior here
        } else {
            // The activity is not in multi-window mode
            // Handle single-window mode specific behavior here
        }
    }

    private fun connectMain() {
        val callback = ModuleCallback("Main", findViewById(R.id.text_view))
        val connection = IPCConnection(MODULE_CODE_MAIN)
        for (i in 0..119) {
            connection.addCallback(callback, i)
        }
        MsToolkitConnection.instance.addObserver(connection)
    }

    private fun connectCanbus() {
        val callback = ModuleCallback("Canbus", findViewById(R.id.text_view))
        val connection = IPCConnection(MODULE_CODE_CANBUS)
        for (i in 0..50) {
            connection.addCallback(callback, i)
        }
        for (i in 1000..1036) {
            connection.addCallback(callback, i)
        }
        MsToolkitConnection.instance.addObserver(connection)
    }

    private fun connectSound() {
        val callback = ModuleCallback("Sound", findViewById(R.id.text_view))
        val connection = IPCConnection(MODULE_CODE_SOUND)
        for (i in 0..49) {
            connection.addCallback(callback, i)
        }
        MsToolkitConnection.instance.addObserver(connection)
    }

    private fun connectCanUp() {
        val callback = ModuleCallback("CanUp", findViewById(R.id.text_view))
        val connection = IPCConnection(MODULE_CODE_CAN_UP)
        connection.addCallback(callback, 100)
        MsToolkitConnection.instance.addObserver(connection)
    }
}