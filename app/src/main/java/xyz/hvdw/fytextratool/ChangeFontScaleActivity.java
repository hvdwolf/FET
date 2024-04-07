package xyz.hvdw.fytextratool;

import static java.lang.Float.valueOf;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

public class ChangeFontScaleActivity extends AppCompatActivity {
    TextView font_preview;
    SeekBar bar;
    Button btn_save;
    private float fontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_font_scale);

        font_preview = (TextView)findViewById(R.id.fontsizepreview);
        bar = (SeekBar)findViewById(R.id.font_size_seekbar);
        btn_save = (Button)findViewById(R.id.btn_savefontsize);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.change_font_scale_title));
        }
        font_preview.setText(getString(R.string.change_font_scale_preview) + ": 1.0");

        if (!Settings.System.canWrite(this)) {
            Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);
        }

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                font_preview.setTextSize(valueOf(i)*2);
                fontSize = valueOf(i) / 10.0f;
                font_preview.setText(getString(R.string.change_font_scale_preview) + ": " + String.valueOf(fontSize));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFontSize();
            }
        });

    }

    private void saveFontSize() {
        if (fontSize != 0.0d) {
            Settings.System.putFloat(getBaseContext().getContentResolver(), "font_scale", fontSize);
            Logger.logToFile("Writing the fontsize being " + String.valueOf(fontSize));
        } else {
            Utils.showInfoDialog(this, "incorrect value", "font size cannot be 0");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
