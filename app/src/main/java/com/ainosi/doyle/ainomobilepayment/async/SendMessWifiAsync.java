package com.ainosi.doyle.ainomobilepayment.async;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.net.Socket;

public class SendMessWifiAsync extends AsyncTask<String, String, String> {
    private Socket socket = null;
    private String input;
    private String kiriman;

    public SendMessWifiAsync(String input, String kiriman) {
        this.input = input;
        this.kiriman = kiriman;
    }

    @Override
    protected String doInBackground(String... uri) {
        Log.e("bekgron","ok");
        if (kiriman == null) kiriman = "kosong";
        String msg_received = null;

        try {
            socket = new Socket(input, 1412);
            DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
            DOS.writeUTF(kiriman);
            Log.e("terikirim haruse","ok");
            socket.close();
        } catch (Exception e) {
            Log.e("ok",e +"");
        }
        return msg_received;
    }
}

