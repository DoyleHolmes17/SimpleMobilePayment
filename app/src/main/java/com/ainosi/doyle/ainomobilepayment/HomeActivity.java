package com.ainosi.doyle.ainomobilepayment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ainosi.doyle.ainomobilepayment.helper.FieldName;
import com.ainosi.doyle.ainomobilepayment.helper.Utils;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

public class HomeActivity extends AppCompatActivity {

    private ImageView gambarmain1;
    private BluetoothDevice perangkatintent;
    private String isiqrcode, statusawal, toastt, paymenttype, akunn, typeconn, device_bluetooth_from;
    private Button scanbtn, adduserbtn1;
    private LinearLayout settingbtn;
    private Intent intent;
    private Button settingbtn1, logoutbtn, scanqrbtn, generateqrbtn, paymentnfcbtn;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        settingbtn1 = (Button) findViewById(R.id.settingbtn1);
        logoutbtn = (Button) findViewById(R.id.logoutbtn);
        scanqrbtn = (Button) findViewById(R.id.scanqrbtn);
        adduserbtn1 = (Button) findViewById(R.id.adduserbtn1);
        settingbtn = (LinearLayout) findViewById(R.id.settingbtn);
//        generateqrbtn = (Button) findViewById(R.id.generateqrbtn);
        paymentnfcbtn = (Button) findViewById(R.id.paymentnfcbtn);
        device_bluetooth_from = Utils.readSharedSetting(HomeActivity.this, "device_bluetooth_from", "kosong");

        intent = getIntent();
        toastt = intent.getStringExtra(FieldName.TOAST);
        if (!device_bluetooth_from.equals("kosong")){
            perangkatintent = intent.getParcelableExtra(FieldName.BT_DEVICES);
        }

        if (toastt == null) {
            //do nothing
        } else if (toastt.equals("ok")) {
            StyleableToast.makeText(this, getResources().getString(R.string.login_sucess),
                    Toast.LENGTH_LONG, R.style.mytoast).show();
        }

//        akunn = Utils.readSharedSetting(this, FieldName.OPERATOR, "kosong");
//        if (akunn.equals("user")) {
//            settingbtn.setVisibility(View.GONE);
//        }

        settingbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(HomeActivity.this, SettingActivity.class);
//                if (perangkatintent == null){
//                    Utils.saveSharedSetting(HomeActivity.this, "device_bluetooth", " - ");
//                }
                finish();
                startActivity(intent);
            }
        });
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(HomeActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });
        adduserbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(HomeActivity.this, RegisterActivity.class);
                finish();
                startActivity(intent);
            }
        });
//        scanqrbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//                intent = new Intent(HomeActivity.this, BarcodeCaptureActivity.class);
//                startActivity(intent);
//            }
//        });
//        generateqrbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//                intent = new Intent(HomeActivity.this, GenerateQRActivity.class);
//                startActivity(intent);
//            }
//        });
        paymentnfcbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeconn = Utils.readSharedSetting(HomeActivity.this, "type_conn", "kosong");
                if (typeconn.equals("no_wifi")) {
                    StyleableToast.makeText(HomeActivity.this, getResources().getString(R.string.ip_null),
                            Toast.LENGTH_LONG, R.style.gagaltoast).show();
                } else if (typeconn.equals("kosong")) {
                    StyleableToast.makeText(HomeActivity.this, getResources().getString(R.string.type_conn_empty),
                            Toast.LENGTH_LONG, R.style.gagaltoast).show();
                } else if (typeconn.equals("bt")) {
//                    if (perangkatintent == null){
//                        StyleableToast.makeText(HomeActivity.this, getResources().getString(R.string.bt_conn_empty),
//                                Toast.LENGTH_LONG, R.style.gagaltoast).show();
//                    } else {
                        finish();
                        intent = new Intent(HomeActivity.this, PaymentActivity.class);
//                        intent.putExtra(FieldName.BT_DEVICES, perangkatintent);
                        startActivity(intent);
//                    }
                } else {            //Wifi
                    finish();
                    intent = new Intent(HomeActivity.this, PaymentActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {

            Utils.saveSharedSetting(this, FieldName.STATUSAWAL, "0");
            finish();
            HomeActivity.this.finishAffinity();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.back_double, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
