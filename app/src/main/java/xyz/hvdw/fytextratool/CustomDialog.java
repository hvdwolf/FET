package xyz.hvdw.fytextratool;


import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CustomDialog extends AppCompatActivity {
    String configText = "";
    String returnText = "";

    public void onBackPressed() {
        finish();
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_layout);

        ImageView dialogImage = findViewById(R.id.dialogImage);
        TextView dialogText = findViewById(R.id.dialogText);
        Button btnContinue = findViewById(R.id.btnContinue);
        Button btnCancel = findViewById(R.id.btnCancel);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("TITLE"));
        }

        // Now start on the actions of the config.txt

        if ((getIntent().getStringExtra("ACTION")).contains("btsettingstofyt")) {
            dialogImage.setImageResource(R.drawable.btsettingstofyt);

            configText = loadConfigText(getIntent().getStringExtra("FILENAME"));
            if (configText.equals("config.txt not loaded")) {
                btnContinue.setEnabled(false);
                dialogText.setText(getString(R.string.config_txt_not_found));
                dialogImage.setImageResource(R.drawable.blank);
            } else { //We did find the config.txt
                returnText = checkConfigTextBTSettings(configText);
                // If not added already, do below
                if (returnText.contains("alreadyAdded")) {
                    // Nothing to do.Tell user it has already been added
                    btnContinue.setEnabled(false);
                    dialogText.setText(getString(R.string.btsettings_already_added_to_fyt_settings_text));
                } else {
                    configText = returnText;
                    dialogText.setText(getIntent().getStringExtra("TEXT"));
                }
            }
        } else if ((getIntent().getStringExtra("ACTION")).contains("adboverwifiandusbdebugging")) {
            dialogImage.setImageResource(R.drawable.blank);
            configText = loadConfigText(getIntent().getStringExtra("FILENAME"));
            if (configText.equals("config.txt not loaded")) {
                btnContinue.setEnabled(false);
                dialogText.setText(getString(R.string.config_txt_not_found));
                dialogImage.setImageResource(R.drawable.blank);
            } else { //We did find the config.txt
                returnText = checkConfigTextADB(configText);
                // If not added already, do below
                if (returnText.contains("alreadyAdded")) {
                    // Nothing to do.Tell user it has already been added
                    btnContinue.setEnabled(false);
                    dialogText.setText(getString(R.string.adb_already_activated));
                } else {
                    configText = returnText;
                    dialogText.setText(getIntent().getStringExtra("TEXT"));
                }
            }
        }




        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConfig(configText);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String loadConfigText(String fileName) {
        String longText;

        File textFile = new File(fileName);
        if (textFile.exists()) {
            longText = FileUtils.readFileToString(textFile);
        } else {
            longText = getString(R.string.config_txt_not_found);
            longText = "config.txt not loaded";
        }
        return longText;
    }

    private void saveConfig(String textToSave) {
        File externalStorage = Environment.getExternalStorageDirectory();
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(externalStorage, "config.txt");
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(textToSave.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logToFile("writing config.txt to external storage gives error " + e.toString());
            // Handle the exception, e.g., show an error message
        } finally {
            try {
                // Close the FileOutputStream
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Logger.logToFile("closing config.txt in in external storage givers error " + e.toString());
            }
        }
        Utils.prepareInternalFlash(this);
    }

    private String checkConfigTextBTSettings(String configText) {
        String[] lines = configText.split("\n");
        String fytOBD = "sys.fyt.systemobd=true";
        String a2dp = "persist.lsec.enable_a2dp=true";
        String btName = "ro.lsec.btname=Bluetooth builtin chip id=0";
        Boolean bOBD = false;
        Boolean ba2dp = false;
        Boolean bbtName = false;

        for (String line : lines) {
            if (line.contains(fytOBD)) {
                bOBD = true;
            }
            if (line.contains(a2dp)) {
                ba2dp = true;
            }
            if (line.contains(btName)) {
                bbtName = true;
            }
        }
        if (!bOBD) {
            configText += "\n" + fytOBD + "\n";
        }
        if (!ba2dp) {
            configText += "\n" + a2dp + "\n";
        }
        if (!bbtName) {
            configText += "\n" + btName +"\n";
        }

        if (bOBD && ba2dp && bbtName) {
            return "alreadyAdded";
        } else {
            return configText;
        }
    }

    private String checkConfigTextADB(String configText) {
        String[] lines = configText.split("\n");
        String ADB = "persist.adb.tcp.port=5555";
        String UD = "ro.build.type=userdebug";
        Boolean bADB = false;
        Boolean bUD = false;


        for (String line : lines) {
            if (line.contains(ADB)) {
                bADB = true;
            }
            if (line.contains(UD)) {
                bUD = true;
            }
        }
        if (!bADB) {
            configText += "\n" + ADB + "\n";
        }
        if (!bUD) {
            configText += "\n" + UD + "\n";
        }
        if (bADB && bUD) {
            return "alreadyAdded";
        } else {
            return configText;
        }
    }

}
