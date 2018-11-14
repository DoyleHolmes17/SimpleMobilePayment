package com.ainosi.doyle.ainomobilepayment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ainosi.doyle.ainomobilepayment.async.SendMessWifiAsync;
import com.ainosi.doyle.ainomobilepayment.entity.Trx;
import com.ainosi.doyle.ainomobilepayment.helper.DatabaseHandler;
import com.ainosi.doyle.ainomobilepayment.helper.DatePickers;
import com.ainosi.doyle.ainomobilepayment.helper.FieldName;
import com.ainosi.doyle.ainomobilepayment.helper.Utils;
import com.ainosi.doyle.ainomobilepayment.wifihelper.OkayAsync;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PaymentActivity extends AppCompatActivity {

    private ImageView loadingpayment, gbrqr, gambaratas1;
    private String isiqrcode, statusawal, toastt, paymenttype, iptarget, isipesan,
            akunn, portt, type_conn, message, midd, tidd, cmdd, shiftt, trxidd, ip, ipclient,
            trxammountt, trxdatee, trxissidd;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private Button scanbtn;
    private LinearLayout settingbtn;
    private TableLayout tablelblpayment;
    private BluetoothAdapter bluetoothAdapter;
    private Intent intent;
    private ServerSocket serverSocket;
    private BluetoothSocket mmSocket;
    private ConnectedThread mConnectedThread;
    private WifiManager wmanager;
    private BluetoothDevice perangkatintent;
    private Button settingbtn1, logoutbtn, scanqrbtn, generateqrbtn;
    boolean doubleBackToExitPressedOnce = false;
    private TextView typeconnpay, text_datetrf, text_tidtrf, text_midtrf, typeconnpayawal, text_ipaddress;
    private SimpleDateFormat dateFormatter, sdf;
    private TableRow layip;
    private DatabaseHandler dbHandler;
    private Date date;
    private CountDownTimer cdt;
    private static int splashInterval = 30000;
    private HashMap<String, String> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        gbrqr = (ImageView) findViewById(R.id.gbrqr);
        typeconnpay = (TextView) findViewById(R.id.typeconnpay);
        text_datetrf = (TextView) findViewById(R.id.text_datetrf);
        text_tidtrf = (TextView) findViewById(R.id.text_tidtrf);
        text_midtrf = (TextView) findViewById(R.id.text_midtrf);
        text_ipaddress = (TextView) findViewById(R.id.text_ipaddress);
        typeconnpayawal = (TextView) findViewById(R.id.typeconnpayawal);
        tablelblpayment = (TableLayout) findViewById(R.id.tablelblpayment);
        layip = (TableRow) findViewById(R.id.layip);
        gambaratas1 = (ImageView) findViewById(R.id.gambaratas1);

        Utils.saveSharedSetting(PaymentActivity.this, FieldName.STATUSAWAL, "1");

        wmanager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        AndroidNetworking.initialize(getApplicationContext());
//        Glide.with(this).load(R.drawable.aino).preload(250, 250);
        gbrqr.setBackground(getResources().getDrawable(R.drawable.aino));
        dateFormatter = new SimpleDateFormat(DatePickers.DATE_LOCAL_FORMAT);
        dbHandler = new DatabaseHandler(this);
        dbHandler.getWritableDatabase();
        dbHandler.tambahDatatrx();

        date = new Date();
        date.setTime(date.getTime() + 8);
        sdf = new SimpleDateFormat(DatePickers.DATE_SQL_FULLFORMAT);

//        &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

        text_datetrf.setText(dateFormatter.format(Calendar.getInstance().getTime()));
        text_tidtrf.setText("12345678");
        text_midtrf.setText("098765432");

        intent = getIntent();

        gambaratas1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(PaymentActivity.this, SettingActivity.class);
                finish();
                startActivity(intent);
            }
        });


        type_conn = Utils.readSharedSetting(PaymentActivity.this, "type_conn", "kosong");
        portt = Utils.readSharedSetting(PaymentActivity.this, FieldName.PORT, "1412");
        if (type_conn.equals("wifi")) {
            typeconnpay.setText(" Wifi");
            layip.setVisibility(View.VISIBLE);
            wmanager.setWifiEnabled(true);
            ip = Formatter.formatIpAddress(wmanager.getConnectionInfo().getIpAddress());
            text_ipaddress.setText(ip + ":" + portt);
//            iptarget = Utils.readSharedSetting(PaymentActivity.this, FieldName.IP_TARGET, "kosong");
//            if (iptarget.equals("kosong")) {
//                showInternetDialog(getResources().getString(R.string.ip_null_payment));
//            } else {
            startTransactionServer();
//            }
        } else if (type_conn.equals("bt")) {
            dapatditemukan();
//            perangkatintent = intent.getParcelableExtra(FieldName.BT_DEVICES);
            typeconnpay.setText(" Bluetooth");
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                Start_Server();
            } else {
                bluetoothAdapter.enable();
                Start_Server();
            }
        }
    }

    protected void timer() {
        cdt = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.e("DETIK", "seconds remaining: " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                Log.e("DETIK", "DONE");
                StyleableToast.makeText(PaymentActivity.this,
                        getString(R.string.payment_timeout),
                        Toast.LENGTH_SHORT, R.style.gagaltoast).show();
//                gbrqr.setBackground(getResources().getDrawable(R.drawable.aino));
                Glide.with(PaymentActivity.this).asBitmap().load(getResources().getDrawable(R.drawable.aino)).into(gbrqr);
            }

        }.start();
    }

    protected void getfastnetwork(String url) {
        Log.e("masuk fast", "ok");
        AndroidNetworking.get(url)
//                .addPathParameter("pageNumber", "0")
//                .addBodyParameter(hashMap)
                .addQueryParameter("limit", "3")
//                .addHeaders("token", "1234")
//                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    //                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        isiqrcode = response;

                        Log.e("response", isiqrcode);

                        try {
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.encodeBitmap(isiqrcode, BarcodeFormat.QR_CODE, 300, 300);

//                            gbrqr.setBackground(getResources().getDrawable(R.drawable.nfc));
                            gbrqr.setImageBitmap(bitmap);
                            typeconnpayawal.setVisibility(View.GONE);
                            typeconnpay.setText(getString(R.string.scan_this_qr));
                            if (type_conn.equals("wifi")) {
                                startTransactionClient("{\"status\":\"success\"}");
                            } else if (type_conn.equals("bt")) {
                                SendMessage("{\"status\":\"success\"}");
                            }
                            timer();
                        } catch (Exception e) {
                            Toast.makeText(PaymentActivity.this, "Error generate QR", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Toast.makeText(PaymentActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    protected void readjson(String json) {
        JSONObject jsonobj = null;

        Log.e("masuk read", "ok");
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

        date = new Date();
        date.setTime(date.getTime() + 8);

        hashMap = new HashMap<String, String>();
        hashMap.put(FieldName.PROCTYPE, FieldName.QR_INQ);
        hashMap.put(FieldName.MCC, "12345678");
        hashMap.put(FieldName.MID, "12345");
        hashMap.put(FieldName.TID, "54321");
        hashMap.put(FieldName.LOCALDTTM, sdf.format(date));
        hashMap.put(FieldName.PAYAMT, "10000");
        Log.e("hashmap", new JSONObject(hashMap).toString());

//        dbHandler.addTrx(new Trx(midd, tidd, shiftt,
//                Integer.parseInt(trxidd), trxammountt, trxdatee, trxissidd));

        Log.e("mau fast", "ok");
        getfastnetwork("https://ainosi-dce37.firebaseio.com/isinya.json");
    }

    @Override
    public void onBackPressed() {
        finish();
        intent = new Intent(PaymentActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void showInternetDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        finish();
                        intent = new Intent(PaymentActivity.this, SettingActivity.class);
                        startActivity(intent);
                    }
                })
                .setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        showInternetDialog(getResources().getString(R.string.ip_null_payment));
                    }
                });
        builder.create().show();
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

    private class SocketServerThread extends Thread {
        final int SocketServerPORT = Integer.parseInt(Utils.readSharedSetting(
                PaymentActivity.this, FieldName.PORT, "1412"));
        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
//                PaymentActivity.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        device_status_wifi.setText(" Online @" + ip + ":" + serverSocket.getLocalPort());
//                    }
//                });

                while (true) {
                    Socket socket = serverSocket.accept();
//                    ipclient = socket.getRemoteSocketAddress().toString();
                    String[] ipclientbaru = socket.getRemoteSocketAddress().toString().split(":");
                    ipclient = ipclientbaru[0].replace("/", "");

//                    Log.e("ip sopo", ipclient); // /10.42.0.80:48806
//                    count++;
//                    message += "#" + count + " from " + socket.getInetAddress()
//                            + ":" + socket.getPort() + ">>say:";
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
//                            if (hasilmess != null) {
//                                message = hasilmess;
//                            }
//                            StyleableToast.makeText(PaymentActivity.this,
//                                    message,
//                                    Toast.LENGTH_SHORT, R.style.mytoast).show();


                            String awalan = hasilmess.substring(0, 2);
                            if (hasilmess == null) {
                                StyleableToast.makeText(PaymentActivity.this,
                                        getString(R.string.null_comm_from),
                                        Toast.LENGTH_SHORT, R.style.gagaltoast).show();
                            } else if (awalan.equals("[\"")) {
                                cdt.cancel();
                                Glide.with(PaymentActivity.this).asBitmap().load(getResources().getDrawable(R.drawable.aino)).into(gbrqr);
//                                gbrqr.setBackground(getResources().getDrawable(R.drawable.aino));

                                StyleableToast.makeText(PaymentActivity.this,
                                        getString(R.string.payment_success),
                                        Toast.LENGTH_SHORT, R.style.mytoast).show();
                            } else if (awalan.equals("{\"")) {
                                readjson(hasilmess);
                            } else {
                                StyleableToast.makeText(PaymentActivity.this,
                                        hasilmess,
                                        Toast.LENGTH_SHORT, R.style.gagaltoast).show();
                            }
                        }
                    }.execute();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    protected void startTransactionClient(String isisurat) {
        Log.e("client", "ok");
        try {
            new SendMessWifiAsync(ipclient, isisurat).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
    protected void SendMessage(String message) {
        byte[] bytes = message.getBytes();
//        byte[] bytes = isipesan.getText().toString().getBytes(Charset.defaultCharset());
        if (mConnectedThread == null) {
            Toast.makeText(this, "Pilih perangkat BT terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else {
            mConnectedThread.write(bytes);
        }
    }

    private void dapatditemukan() {
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

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

    private void connected(BluetoothSocket mmSocket) {
        Log.e("error", "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

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
                            String awalan = incomingMessage.substring(0, 2);
                            if (incomingMessage == null) {
                                StyleableToast.makeText(PaymentActivity.this,
                                        getString(R.string.null_comm_from),
                                        Toast.LENGTH_SHORT, R.style.gagaltoast).show();
                            } else if (awalan.equals("[\"")) {
                                cdt.cancel();
                                Glide.with(PaymentActivity.this).asBitmap().load(getResources().getDrawable(R.drawable.aino)).into(gbrqr);
//                                gbrqr.setBackground(getResources().getDrawable(R.drawable.aino));

                                StyleableToast.makeText(PaymentActivity.this,
                                        getString(R.string.payment_success),
                                        Toast.LENGTH_SHORT, R.style.mytoast).show();
                            } else if (awalan.equals("{\"")) {
                                readjson(incomingMessage);
                            } else {
                                StyleableToast.makeText(PaymentActivity.this,
                                        incomingMessage,
                                        Toast.LENGTH_SHORT, R.style.gagaltoast).show();
                            }
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

}
