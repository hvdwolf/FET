package xyz.hvdw.fytextratool;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class BluetoothUtils {

    private final static int REQUEST_ENABLE_BT = 1;
    private static BluetoothManager bluetoothManager;
    private static BluetoothAdapter bluetoothAdapter;
    public static void startBluetoothSettings(Context context) {
        Logger.logToFile("Starting the Bluetooth settings");
        Intent intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        context.startActivity(intent);
    }

    public static List<String> checkBluetoothDevice(Context context) {
        List<String> adapterProperties= new ArrayList<String>();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                // Bluetooth is not enabled, prompt the user to enable it
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                // Bluetooth is enabled, proceed with Bluetooth operations
                adapterProperties.add(bluetoothAdapter.getName()); // Get the name of the Bluetooth adapter
                Logger.logToFile("Primary Bluetooth Adapter Name: " + bluetoothAdapter.getName());
                adapterProperties.add(bluetoothAdapter.getAddress()); // Get the MAC address of the Bluetooth adapter
                Logger.logToFile("Primary Bluetooth Adapter MAC address: " + bluetoothAdapter.getAddress());
                adapterProperties.add(String.valueOf(bluetoothAdapter.isEnabled())); // Check if Bluetooth is currently enabled
                Logger.logToFile("Primary Bluetooth Adapter enabled?: " + bluetoothAdapter.isEnabled());
                adapterProperties.add(String.valueOf(bluetoothAdapter.getState())); // Get the current state of the Bluetooth adapter
                Logger.logToFile("Primary Bluetooth Adapter state: " + String.valueOf(bluetoothAdapter.getState()));
            }
        } else {
            // Device does not support Bluetooth
            Logger.logToFile("Bluetooth not supported on this device");
        }
        return adapterProperties;
    }

    // Currently Android only supports one Bluetooth adapter, but the API could be extended to support more.
    @SuppressLint({"NewApi"})
    public static List<String> checkFYTBluetoothAdapter(Context context) {
        List<String> adapterProperties = new ArrayList<String>();
        // Initialize BluetoothManager
        try {
            //bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothManager = (BluetoothManager) context.getSystemService("bluetooth");
            bluetoothAdapter = bluetoothManager.getAdapter();

            try {
                Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

                // Display information about the default adapter
                adapterProperties.add("Bluetooth Adapter:\n");
                adapterProperties.add("Name: " + bluetoothAdapter.getName() + "\n");
                adapterProperties.add("Address: " + bluetoothAdapter.getAddress() + "\n");
                adapterProperties.add("Bonded Devices:\n");
                // Display information about each bonded device
                for (BluetoothDevice device : bondedDevices) {
                    adapterProperties.add(" -> " + device.getName() + " - " + device.getAddress() + "\n");
                }
            } catch (Exception e) {
                Logger.logToFile(e.toString());
                adapterProperties.add(e.toString());
                throw new RuntimeException(e);
            } finally {
                adapterProperties.add("Some crash");
            }
        } catch (Exception e) {
            Logger.logToFile(e.toString());
            adapterProperties.add(e.toString());
            throw new RuntimeException(e);
        } finally {
            adapterProperties.add("Some crash");
        }

        return adapterProperties;
        /*if (bluetoothManager != null) {
            List<BluetoothAdapter> bluetoothAdapters = bluetoothManager.getAdapter();

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Bluetooth Adapters:\n");

            for (BluetoothAdapter adapter : bluetoothAdapters) {
                stringBuilder.append("Adapter Name: " + adapter.getName()).append("\n");
            }
        }*/
    }

    /*private static Object getSystemService(String bluetoothService) {
    } */

    public static List<String> checkMultipleBTAdapters(Context context) {
        List<String> adapterProperties = new ArrayList<String>();

        // Get the default Bluetooth adapter
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();

        if (defaultAdapter == null) {
            // Device does not support Bluetooth
            return null;
        }

        // Get a list of all bonded (paired) devices for the default adapter
        Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();

        // Display information about the default adapter
        adapterProperties.add("Default Bluetooth Adapter:\n");
        adapterProperties.add("Name: " + defaultAdapter.getName() + "\n");
        adapterProperties.add("Address: " + defaultAdapter.getAddress() + "\n");
        adapterProperties.add("Bonded Devices:\n");

        // Display information about each bonded device
        for (BluetoothDevice device : bondedDevices) {
            adapterProperties.add(" -> " + device.getName() + " - " + device.getAddress() + "\n");
        }

        // Get information about secondary Bluetooth adapters if available
        BluetoothAdapter secondaryAdapter = BluetoothAdapter.getDefaultAdapter();
        if (secondaryAdapter != null && !secondaryAdapter.equals(defaultAdapter)) {
            adapterProperties.add("\nSecondary Bluetooth Adapter:\n");
            adapterProperties.add("Name: " + secondaryAdapter.getName() + "\n");
            adapterProperties.add("Address: " + secondaryAdapter.getAddress() + "\n");
            adapterProperties.add("Bonded Devices:\n");

            // Get a list of all bonded (paired) devices for the secondary adapter
            Set<BluetoothDevice> secondaryBondedDevices = secondaryAdapter.getBondedDevices();

            // Display information about each bonded device for the secondary adapter
            for (BluetoothDevice device : secondaryBondedDevices) {
                adapterProperties.add(" -> " + device.getName() + " - " + device.getAddress() + "\n");
            }
        }
        return adapterProperties;
    }


    private static void startActivityForResult(Intent enableBtIntent, int requestEnableBt) {
    }

    public static List<BluetoothAdapter> findSecondaryBluetoothAdapters( Context context) {
        List<BluetoothAdapter> secondaryAdapters = new ArrayList<>();

        try {
            // Get the BluetoothAdapter class
            Class<?> bluetoothAdapterClass = Class.forName("android.bluetooth.BluetoothAdapter");

            // Get the field containing all BluetoothAdapter instances
            Field adaptersField = bluetoothAdapterClass.getDeclaredField("adapters");

            // Ensure the field is accessible
            adaptersField.setAccessible(true);

            // Get the array of BluetoothAdapter instances
            BluetoothAdapter[] allAdapters = (BluetoothAdapter[]) adaptersField.get(null);

            // Iterate through all adapters and check if they're secondary
            for (BluetoothAdapter adapter : allAdapters) {
                // Check if the adapter is not null and not the default adapter
                if (adapter != null && !adapter.equals(BluetoothAdapter.getDefaultAdapter())) {
                    // Found a secondary adapter
                    secondaryAdapters.add(adapter);
                }
            }
        } catch (Exception e) {
            // Handle reflection exceptions
            Logger.logToFile(e.toString());
            e.printStackTrace();
        }

        return secondaryAdapters;
    }
}
