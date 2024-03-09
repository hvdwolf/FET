package xyz.hvdw.fytextratool;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.fragment.app.DialogFragment;

public class DialogWithCheckboxes extends DialogFragment {

    private CheckBox checkboxMain;
    private CheckBox checkboxCanbus;
    private CheckBox checkboxSound;
    private CheckBox checkboxCanup;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.checkboxes_dialog, null);

        CheckBox checkboxMain = view.findViewById(R.id.checkbox_main);
        CheckBox checkboxCanbus = view.findViewById(R.id.checkbox_canbus);
        CheckBox checkboxSound = view.findViewById(R.id.checkbox_sound);
        CheckBox checkboxCanup = view.findViewById(R.id.checkbox_canup);

        builder.setView(view)
                .setTitle(getString(R.string.fytcanbusmonitor_select))
                .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with your method after evaluating checkboxes
                        boolean mainChecked = checkboxMain.isChecked();
                        boolean canbusChecked = checkboxCanbus.isChecked();
                        boolean soundChecked = checkboxSound.isChecked();
                        boolean canupChecked = checkboxCanup.isChecked();

                        ((MainActivity) getActivity()).continueWithMethodForFytCanbusMonitor(mainChecked, canbusChecked, soundChecked, canupChecked);
                    }
                })
                .setNegativeButton(getString(R.string.btn_cancel), null);
        return builder.create();
    }

    // Method to get the states of checkboxes
    public boolean[] getCheckboxStates() {
        boolean[] states = new boolean[4];
        states[0] = checkboxMain.isChecked();
        states[1] = checkboxCanbus.isChecked();
        states[2] = checkboxSound.isChecked();
        states[3] = checkboxCanup.isChecked();
        return states;
    }
}