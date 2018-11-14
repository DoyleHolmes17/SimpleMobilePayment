package com.ainosi.doyle.ainomobilepayment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ainosi.doyle.ainomobilepayment.helper.FieldName;
import com.ainosi.doyle.ainomobilepayment.helper.Utils;
import com.ainosi.doyle.ainomobilepayment.wifihelper.OkayAsync;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SettingActivity extends AppCompatActivity {

    private ImageView gambarmain1;
    private String isiqrcode, paymenttype, type_conn, device_bluetooth_from, midd, tidd, cmdd, shiftt, trxidd, ip,
            trxammountt, trxdatee, trxissidd, sport;
    private String msg, statusakun, serverIpAddress, str1, msg1, message, ipserv, isipesan, device_bluetooth;
    private Button okbtn;
    private Intent intent;
    private EditText iptarget, portt;
    private LinearLayout settingbtn;
    private Spinner spinnertype;
    private LinearLayout settingbt, settingwifi;
    boolean doubleBackToExitPressedOnce = false;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> devices;
    private List<BluetoothDevice> s;
    private List<String> str;
    private ListView listbt, listdiscover;
    private int posisisi;
    private TextView device_status_wifi, myip, selected_device;
    private ArrayAdapter<String> adapter, detectedAdapter;
    private BluetoothSocket mmSocket;
    private ConnectedThread mConnectedThread;
    private BluetoothDevice perangkatintent, mmDevice;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private WifiManager wmanager;
    private ServerSocket serverSocket;
    private Boolean isBonded = false, bool;
    private ArrayList<BluetoothDevice> arrayListBluetoothDevices = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        spinnertype = (Spinner) findViewById(R.id.spinnertype);
        settingbt = (LinearLayout) findViewById(R.id.settingbt);
        settingwifi = (LinearLayout) findViewById(R.id.settingwifi);
        listbt = (ListView) findViewById(R.id.listbt);
        listdiscover = (ListView) findViewById(R.id.listdiscover);
        device_status_wifi = (TextView) findViewById(R.id.device_status_wifi);
        myip = (TextView) findViewById(R.id.myip);
        selected_device = (TextView) findViewById(R.id.selected_device);
//        iptarget = (EditText) findViewById(R.id.iptarget);
        portt = (EditText) findViewById(R.id.portt);
        type_conn = Utils.readSharedSetting(SettingActivity.this, "type_conn", "kosong");
        if (type_conn.equals("wifi")) {
            spinnertype.setSelection(1);
        } else { // bt and default
            spinnertype.setSelection(0);
        }

        Utils.saveSharedSetting(SettingActivity.this, "device_bluetooth_from", "kosong");
        ipserv = Utils.readSharedSetting(SettingActivity.this, FieldName.IP_TARGET, "kosong");
//        if (!ipserv.equals("kosong")) iptarget.setText(ipserv);
        settingwifi.setVisibility(View.GONE);
//        settingbt.setVisibility(View.VISIBLE);
        posisisi = 1412;
        wmanager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        device_bluetooth = Utils.readSharedSetting(this, "device_bluetooth", " - ");
        selected_device.setText(device_bluetooth);

        device_status_wifi.setText(getResources().getString(R.string.offline));
        arrayListBluetoothDevices = new ArrayList<BluetoothDevice>();
        detectedAdapter = new ArrayAdapter<String>(SettingActivity.this, R.layout.custom_dropdown_menu);
        listdiscover.setAdapter(detectedAdapter);
        detectedAdapter.notifyDataSetChanged();

        spinnertype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {            // BT
                    settingwifi.setVisibility(View.GONE);
//                    settingbt.setVisibility(View.VISIBLE);
//                    intisari();
                    Utils.saveSharedSetting(SettingActivity.this, "type_conn",
                            "bt");
                } else if (position == 1) {      // wifi
                    settingwifi.setVisibility(View.VISIBLE);
                    settingbt.setVisibility(View.GONE);
                    wmanager.setWifiEnabled(true);
                    ip = Formatter.formatIpAddress(wmanager.getConnectionInfo().getIpAddress());
                    if (ip != null) {
                        device_status_wifi.setText(getResources().getString(R.string.online));
                        myip.setText(ip);
                        startTransactionServer();
                        Utils.saveSharedSetting(SettingActivity.this, "type_conn",
                                "wifi");
                    } else {
                        Utils.saveSharedSetting(SettingActivity.this, "type_conn",
                                "no_wifi");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                intisari();
                Utils.saveSharedSetting(SettingActivity.this, "type_conn",
                        "bt");
            }
        });
    }

    private void intisari() {
//        Log.e("enable?", bluetooth.isEnabled()+"");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.e("enableada?", bluetoothAdapter.isEnabled() + "");

        if (bluetoothAdapter.isEnabled()) {
            dapatditemukan();
            startSearching();
            getbounded();

            listbt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.e("masuk konek thread", s.get(position).getName());
                    new ConnectThread(s.get(position)).run();
                    posisisi = position;
                    selected_device.setText(s.get(position).getName());
                    Utils.saveSharedSetting(SettingActivity.this, "device_bluetooth", s.get(position).getName());
                    perangkatintent = s.get(position);
                    Utils.saveSharedSetting(SettingActivity.this, "device_bluetooth_from", "bounded");
                }
            });
            listdiscover.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.e("masuk konek thread", position + "");
//                    selected_device.setText(listdiscover.getSelectedItem().toString());
                    selected_device.setText(" " + arrayListBluetoothDevices.get(position).getName());
                    Utils.saveSharedSetting(SettingActivity.this, "device_bluetooth",
                            arrayListBluetoothDevices.get(position).getName());
                    perangkatintent = arrayListBluetoothDevices.get(position);
                    try {
                        isBonded = createBond(arrayListBluetoothDevices.get(position));
                        if (isBonded) {
                            //arrayListpaired.add(bdDevice.getName()+"\n"+bdDevice.getAddress());
                            //adapter.notifyDataSetChanged();
//                            getPairedDevices();
//                            adapter.notifyDataSetChanged();
                            StyleableToast.makeText(SettingActivity.this,
                                    getResources().getString(R.string.bluetooth_bounded),
                                    Toast.LENGTH_LONG, R.style.mytoast).show();
                            intisari();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Utils.saveSharedSetting(SettingActivity.this, "device_bluetooth_from", "discover");
                }
            });
            Start_Server();
//            SendMessage();


        } else {
            bluetoothAdapter.enable();
            Log.e("enable?", bluetoothAdapter.isEnabled() + "");
        }
    }

    public boolean removeBond(BluetoothDevice btDevice)
            throws Exception {
        Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }


    public boolean createBond(BluetoothDevice btDevice)
            throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    private void dapatditemukan() {
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    private void getbounded() {
        devices = bluetoothAdapter.getBondedDevices();

        s = new ArrayList<BluetoothDevice>();
        str = new ArrayList<String>();
//            s = new ArrayList<String>();
        for (BluetoothDevice bt : devices) {
            s.add(bt);
            str.add(bt.getName());
        }
        int i;
//            Log.e("device", devices.size() + "");
        for (i = 0; i < s.size(); i++) {
            Log.e("device " + String.valueOf(i) + ";;;", str.get(i));
        }
        adapter = new ArrayAdapter<String>(this, R.layout.custom_dropdown_menu, str);
        listbt.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private void startSearching() {
        Log.i("Log", "in the start searching method");
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(myReceiver, intentFilter);
        bluetoothAdapter.startDiscovery();
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = Message.obtain();
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Toast.makeText(context, getResources().getString(R.string.bluetooth_found), Toast.LENGTH_SHORT).show();

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                try
//                {
//                    //device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
//                    //device.getClass().getMethod("cancelPairingUserInput", boolean.class).invoke(device);
//                }
//                catch (Exception e) {
//                    Log.i("Log", "Inside the exception: ");
//                    e.printStackTrace();
//                }

                if (arrayListBluetoothDevices.size() < 1) { // this checks if the size of bluetooth device is 0,then add the device to the arraylist.
                    detectedAdapter.add(device.getName() + "\n" + device.getAddress());
                    arrayListBluetoothDevices.add(device);
                    detectedAdapter.notifyDataSetChanged();
                } else {
                    boolean flag = true;    // flag to indicate that particular device is already in the arlist or not
                    for (int i = 0; i < arrayListBluetoothDevices.size(); i++) {
                        if (device.getAddress().equals(arrayListBluetoothDevices.get(i).getAddress())) {
                            flag = false;
                        }
                    }
                    if (flag == true) {
                        detectedAdapter.add(device.getName() + "\n" + device.getAddress());
                        arrayListBluetoothDevices.add(device);
                        detectedAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    private void showInternetDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        finish();
                        intent = new Intent(SettingActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                })
                .setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        type_conn = Utils.readSharedSetting(SettingActivity.this, "type_conn", "kosong");
        device_bluetooth_from = Utils.readSharedSetting(SettingActivity.this, "device_bluetooth_from", "kosong");
        if (type_conn.equals("wifi")) {
//            Utils.saveSharedSetting(SettingActivity.this, FieldName.IP_TARGET, iptarget.getText().toString());
            Utils.saveSharedSetting(SettingActivity.this, FieldName.PORT, portt.getText().toString());
//            if (iptarget.getText().toString().isEmpty()){
//                showInternetDialog(getResources().getString(R.string.ip_null));
//            } else
            if (portt.getText().toString().isEmpty()) {
                showInternetDialog(getResources().getString(R.string.port_null));
            } else {
                finish();
                intent = new Intent(SettingActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        } else if (type_conn.equals("bt")) {
//            if (device_bluetooth_from.equals("kosong")) {
//                showInternetDialog(getResources().getString(R.string.back_setting_nodevices));
//            } else
            LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
            finish();
            intent = new Intent(SettingActivity.this, HomeActivity.class);
            intent.putExtra(FieldName.BT_DEVICES, perangkatintent);
            startActivity(intent);
//            }
        } else {
            showInternetDialog(getResources().getString(R.string.back_setting_nodevices));
        }
    }

    /*
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
     */

    public class ConnectThread extends Thread {

        public ConnectThread(BluetoothDevice devicer) {
//        public ConnectThread(BluetoothDevice devicer, UUID uuid) {
            Log.e("catch", "ConnectThread: started.");
            mmDevice = devicer;
//            deviceUUID = uuid;
        }

        public void run() {
//            // Cancel discovery because it will slow down the connection
//            mBluetoothAdapter.cancelDiscovery();
            BluetoothSocket tmp = null;
            Log.e("catch", "RUN mConnectThread ");

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                Log.e("catch", "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                        + MY_UUID_INSECURE);
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e("catch", "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }
            mmSocket = tmp;
            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
                Log.e("wow konek ", "ok");
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                    Log.e("catch", "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e("catch", "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                Log.e("catch", "run: ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE);
            }

            //will talk about this in the 3rd video
            connected(mmSocket);
        }

        public void cancel() {
            try {
                Log.e("catch", "cancel: Closing Client Socket.");
                if (mmSocket != null)
                    mmSocket.close();
            } catch (IOException e) {
                Log.e("catch", "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }

    private void connected(BluetoothSocket mmSocket) {
        Log.e("error", "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    protected void readjson(String json) {
        JSONObject jsonobj = null;
        try {
            jsonobj = new JSONObject(json);
            midd = jsonobj.has(FieldName.MID)
                    ? jsonobj.getString(FieldName.MID) : null;
            tidd = jsonobj.has(FieldName.TID)
                    ? jsonobj.getString(FieldName.TID) : null;
            cmdd = jsonobj.has(FieldName.CMD)
                    ? jsonobj.getString(FieldName.CMD) : null;
            shiftt = jsonobj.has(FieldName.SHIFT)
                    ? jsonobj.getString(FieldName.SHIFT) : null;
            JSONObject dataa = jsonobj.has(FieldName.DATA)
                    ? jsonobj.getJSONObject(FieldName.DATA) : null;
            if (dataa == null) {
                trxidd = "kosong";
                trxammountt = "kosong";
                trxdatee = "kosong";
                trxissidd = "kosong";
            } else {
                trxidd = dataa.has(FieldName.TRXID)
                        ? dataa.getString(FieldName.TRXID) : null;
                trxammountt = dataa.has(FieldName.TRXAMT)
                        ? dataa.getString(FieldName.TRXAMT) : null;
                trxdatee = dataa.has(FieldName.TRXDATE)
                        ? dataa.getString(FieldName.TRXDATE) : null;
                trxissidd = dataa.has(FieldName.TRXISSID)
                        ? dataa.getString(FieldName.TRXISSID) : null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        getfastnetwork("https://ainosi-dce37.firebaseio.com/isinya.json");
    }

    /*
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
     */

    // maintaining the BTConnection, Sending the data, and
    // receiving incoming data through input/output streams respectively
    private class ConnectedThread extends Thread {
        //        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private Context context;

        public ConnectedThread(BluetoothSocket socket) {
            Log.e("error", "ConnectedThread: Starting.");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream

            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                // Read from the InputStream
                try {
                    bytes = mmInStream.read(buffer);
                    final String incomingMessage = new String(buffer, 0, bytes);
                    Log.e("error", "InputStream: " + incomingMessage);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            String awalan = incomingMessage.substring(0, 2);
//                            if (incomingMessage == null) {
//                                StyleableToast.makeText(SettingActivity.this,
//                                        getString(R.string.null_comm_from),
//                                        Toast.LENGTH_SHORT, R.style.gagaltoast).show();
//                            } else if (awalan.equals("{\"")) {
//                                readjson(incomingMessage);
//                            } else {
//                                StyleableToast.makeText(SettingActivity.this,
//                                        incomingMessage,
//                                        Toast.LENGTH_SHORT, R.style.gagaltoast).show();
//                            }
//                            view_data.setText(incomingMessage);
//                            Toast.makeText(context, incomingMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (IOException e) {
                    Log.e("error", "write: Error reading Input Stream. " + e.getMessage());
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.e("error", "write: Writing to outputstream: " + text);
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("error", "write: Error writing to output stream. " + e.getMessage());
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    /*
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
     */

    protected void Start_Server() {
        AcceptThread accept = new AcceptThread();
        accept.start();
    }

    // accept incoming connection
    private class AcceptThread extends Thread {

        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            // Create a new listening server socket
            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("appname", MY_UUID_INSECURE);
                Log.e("error", "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e("error", "AcceptThread: IOException: " + e.getMessage());
            }
            mmServerSocket = tmp;
        }

        public void run() {
            Log.e("error", "run: AcceptThread Running.");
            BluetoothSocket socket = null;

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                Log.e("error", "run: RFCOM server socket start.....");

                socket = mmServerSocket.accept();

                Log.e("error", "run: RFCOM server socket accepted connection.");

            } catch (IOException e) {
                Log.e("error", "AcceptThread: IOException: " + e.getMessage());
            }
            //talk about this is in the 3rd
            if (socket != null) {
                connected(socket);
            }
            Log.e("error", "END mAcceptThread ");
        }

        public void cancel() {
            Log.e("error", "cancel: Canceling AcceptThread.");
            try {
                if (mmServerSocket != null)
                    mmServerSocket.close();
            } catch (IOException e) {
                Log.e("error", "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }
    }

    /*
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
    ##################################################################################################
     */

    // client / send
    protected void SendMessage() {
//        Log.e("isipesan", isipesan.getText().toString());
//        Log.e("isipesan byte", isipesan.getText().toString().getBytes()+"");
//        Log.e("isipesan byte utf", isipesan.getText().toString().getBytes(Charset.defaultCharset())+"");
        byte[] bytes = "coba send data".getBytes();
//        byte[] bytes = isipesan.getText().toString().getBytes(Charset.defaultCharset());
        if (mConnectedThread == null) {
            Toast.makeText(this, "Pilih perangkat BT terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else {
            mConnectedThread.write(bytes);
        }
    }

    /*
    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
     */


    protected void startTransactionServer() {
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {
        static final int SocketServerPORT = 1412;
        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                SettingActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        device_status_wifi.setText(" Online @" + ip + ":" + serverSocket.getLocalPort());
                    }
                });

                while (true) {
                    Socket socket = serverSocket.accept();
                    count++;
                    message += "#" + count + " from " + socket.getInetAddress()
                            + ":" + socket.getPort() + ">>say:";
                    DataInputStream in = new DataInputStream(socket.getInputStream());
//                    Log.e("ok", in.readUTF());
                    new OkayAsync(in) {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                        }

                        @Override
                        protected void onPostExecute(String hasilmess) {
                            super.onPostExecute(hasilmess);
                            if (hasilmess != null) {
                                message += " " + hasilmess;
                            } else {
                                message += " null";
                            }
                            StyleableToast.makeText(SettingActivity.this,
                                    message,
                                    Toast.LENGTH_SHORT, R.style.mytoast).show();
                        }
                    }.execute();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
    }
}
