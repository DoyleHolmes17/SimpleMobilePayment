package com.ainosi.doyle.ainomobilepayment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ainosi.doyle.ainomobilepayment.helper.DatabaseHandler;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Intent intent;
    private DatabaseHandler dbHandler;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private String p1 = Manifest.permission.BLUETOOTH_ADMIN, p2 = Manifest.permission.INTERNET,
            p3 = Manifest.permission.CHANGE_WIFI_STATE, p4 = Manifest.permission.ACCESS_WIFI_STATE,
            p5 = Manifest.permission.CHANGE_NETWORK_STATE, p6 = Manifest.permission.ACCESS_NETWORK_STATE,
            p7 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static int splashInterval = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        dbHandler = new DatabaseHandler(this);
        dbHandler.getWritableDatabase();

        progressBar.setProgress(40);

        if (Build.VERSION.SDK_INT >= 23) {
            permissionAccess();
        } else {
            Log.e("bluetooth", "kakaka");
            async();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void async() {
        if (isNetworkAvailable()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    progressBar.setProgress(100);
                    if (progressBar.getProgress() == 100) {
                        finish();
                        intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
            }, splashInterval);
        } else {
            showInternetDialog(getString(R.string.no_internet));
        }
    }

    private void showInternetDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setNegativeButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
        builder.create().show();
    }

    private void permissionAccess() {
        if (!checkPermission(p1)) {
            Log.e("catch", p1);
            requestPermission(p1);
        } else if (!checkPermission(p2)) {
            Log.e("catch", p2);
            requestPermission(p2);
        } else if (!checkPermission(p3)) {
            Log.e("catch", p3);
            requestPermission(p3);
        } else if (!checkPermission(p4)) {
            Log.e("catch", p4);
            requestPermission(p4);
        } else if (!checkPermission(p5)) {
            Log.e("catch", p5);
            requestPermission(p5);
        } else if (!checkPermission(p6)) {
            Log.e("catch", p6);
            requestPermission(p6);
        } else if (!checkPermission(p7)) {
            Log.e("catch", p7);
            requestPermission(p7);
        } else {
            Toast.makeText(MainActivity.this, "All permission granted", Toast.LENGTH_LONG).show();
            async();
        }
    }

    private void requestPermission(String permission) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        } else {
            //Do the stuff that requires permission...
        }
    }

    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
//                Log.e(""catch"", "val " + grantResults[0]);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionAccess();
//                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                } else {
//                    Toast.makeText(MainActivity.this, "Bye bye", Toast.LENGTH_LONG).show();
                    permissionAccess();
                }
                break;
        }

    }
}
