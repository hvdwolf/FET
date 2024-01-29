package xyz.hvdw.fytextratool;

// AppListAdapter.java
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class AppListAdapter extends ArrayAdapter<PackageInfo> {

    private final LayoutInflater inflater;
    private final List<PackageInfo> packageInfoList;
    private final PackageManager packageManager;

    public AppListAdapter(Context context, List<PackageInfo> packageInfoList) {
        super(context, 0, packageInfoList);
        this.inflater = LayoutInflater.from(context);
        this.packageInfoList = packageInfoList;
        this.packageManager = context.getPackageManager();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_app_list, parent, false);
        }

        PackageInfo packageInfo = packageInfoList.get(position);
        ApplicationInfo appInfo = packageInfo.applicationInfo;

        if (appInfo != null && isLauncherApp(appInfo.packageName)) {
            ImageView appIconImageView = view.findViewById(R.id.appIconImageView);
            appIconImageView.setImageDrawable(appInfo.loadIcon(packageManager));

            TextView appNameTextView = view.findViewById(R.id.appNameTextView);
            appNameTextView.setText(appInfo.loadLabel(packageManager));

            TextView packageNameTextView = view.findViewById(R.id.packageNameTextView);
            packageNameTextView.setText(appInfo.packageName);

            CheckBox checkBox = view.findViewById(R.id.checkbox);
            checkBox.setChecked(false); // Reset checkbox state
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle checkbox click
                    // You can use the position to identify which app is selected
                }
            });
        } else {
            // If not a launcher app, hide the view
            view.setVisibility(View.GONE);
        }

        return view;
    }

    private boolean isLauncherApp(String packageName) {
        Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
        return launchIntent != null;
    }
}
