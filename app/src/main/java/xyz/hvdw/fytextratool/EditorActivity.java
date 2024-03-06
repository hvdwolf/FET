package xyz.hvdw.fytextratool;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditorActivity extends AppCompatActivity {

    private EditText editText;
    private TextView filenameTextView;
    Button btnSave;

    public void onBackPressed() {
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editText = findViewById(R.id.editText);
        btnSave = findViewById(R.id.btnSave);
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
    // Handle mulit-window support in case of splitscreen
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void saveText() {
        File cacheDir;
        String result = "";
        String resultScript = "";
        String textToSave = editText.getText().toString();


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

        // Now do the flashing part.
       Utils.prepareInternalFlash(this);
    }

    private void loadText(String fileName) {
        String longText;

        filenameTextView.setText(FileUtils.extractFileName(fileName));
        File textFile = new File(fileName);
        if (textFile.exists()) {
            longText = FileUtils.readFileToString(textFile);
        } else {
            longText = getString(R.string.config_txt_not_found);
            btnSave.setEnabled(false);
        }
        editText.setText(longText);
    }
}
