package xyz.hvdw.fytextratool;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditorActivity extends AppCompatActivity {

    private EditText editText;
    private TextView filenameTextView;

    public void onBackPressed() {
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editText = findViewById(R.id.editText);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        filenameTextView = findViewById(R.id.filenameTextView);
        // Get the filename from the intent
        String fileName = getIntent().getStringExtra("FILENAME");
        //And for our testVersion
        /*if (MyGettersSetters.getTestVersion()) {
            String externalStorage = android.os.Environment.getExternalStorageDirectory().toString();
            Toast.makeText(this, fileName, Toast.LENGTH_SHORT).show();
            fileName = externalStorage + "/oem/app/config.txt";
        } */


        loadText(fileName);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("TITLE"));
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveText();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveText() {
        File cacheDir;
        String result = "";
        String resultScript = "";
        String textToSave = editText.getText().toString();

        FileUtils.removeAndRecreateFolder("lsec_updatesh");
        File externalStorage = Environment.getExternalStorageDirectory();
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(externalStorage, "config.txt");
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(textToSave.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logToFile("writing config.txt to lsec_updatesh gives error " + e.toString());
            // Handle the exception, e.g., show an error message
        } finally {
            try {
                // Close the FileOutputStream
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Logger.logToFile("closing config.txt in lsec_updatesh givers error " + e.toString());
            }
        }

        Map<String, String> fytPlatform = MyGettersSetters.getPropsHashMap();
        String binary = "";
        String script = "";
        String scriptFolder = "/storage/emulated/0/lsec_updatesh";
        if (fytPlatform.get("ro.board.platform").contains("ums512")){
            binary = "lsec6315update";
            script = "7862lsec.sh";
        } else {
            binary = "lsec6316update";
            script = "8581lsec.sh";
        }
        result = FileUtils.copyAssetsFileToExternalStorage(this, binary, binary);
        resultScript = FileUtils.copyAssetsFileToExternalStorageFolder(this, "config_script", "lsec_updatesh", script);

        if (result.equals("")) {
            Logger.logToFile("Copied " + binary + " to the external storage");
        } else {
            Logger.logToFile("Failed to copy " + binary + " to external storage");
        }
        if (resultScript.equals("")) {
            Logger.logToFile("Copied " + script + " to external storage lsec_updatesh");
        } else {
            Logger.logToFile("Failed to copy " + script + " to external storage lsec_updatesh");
        }


        //final Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage("android.rockchip.update.service");
        //startActivity( launchIntentForPackage );

        /*Toast.makeText(this,"Trying to start the android.rockchip.update.service", Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Code to be executed after the delay
                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("android.rockchip.update.service", "android.rockchip.update.service.UpdateAndRebootActivity");
                componentName = new ComponentName("android.rockchip.update.service", "android.rockchip.update.service.FirmwareUpdatingActivity");
                intent.setComponent(componentName);
                startService(intent);
            }
        }, 500); // 500 milliseconds wait
         */
        ShellRootCommands.rootExec("mount -o remount /storage/emulated/0");

    }

    private void loadText(String fileName) {
        String longText;

        filenameTextView.setText(FileUtils.extractFileName(fileName));
        File textFile = new File(fileName);
        if (textFile.exists()) {
            longText = FileUtils.readFileToString(textFile);
        } else {
            longText = getString(R.string.config_txt_not_found);
        }
        editText.setText(longText);
    }
}
