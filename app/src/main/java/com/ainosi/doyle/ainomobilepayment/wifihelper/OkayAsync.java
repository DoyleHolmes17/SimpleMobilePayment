package com.ainosi.doyle.ainomobilepayment.wifihelper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class OkayAsync extends AsyncTask<String, String, String> {
    private Socket socket = null;
    private DataInputStream input;
    private Context context;
    private String isi;

    public OkayAsync(DataInputStream input) {
        this.context = context;
        this.input = input;
    }

    @Override
    protected String doInBackground(String... uri) {
        isi = "koos";
        Log.e("bekgron","ok");
        try {
            isi = input.readUTF();
//            isi = input.readUTF() + "\n";
            Log.e("terikirim haruse","ok");
        } catch (Exception e) {
            Log.e("ok",e +"");
        }
        return isi;
    }
}

