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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ainosi.doyle.ainomobilepayment.entity.User;
import com.ainosi.doyle.ainomobilepayment.helper.DatabaseHandler;
import com.ainosi.doyle.ainomobilepayment.helper.DatePickers;
import com.ainosi.doyle.ainomobilepayment.helper.FieldName;
import com.ainosi.doyle.ainomobilepayment.helper.Utils;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText etpass, etemail;
    private TextInputLayout input_layout_password, input_layout_email;
    private Button btnLogin;
    private String email, passadm;
    private String[] passarray;
    private Intent intent;
    private Date currentTime;
    private SimpleDateFormat dateFormatter;
    private DatabaseHandler dbHandler;
    private List<User> listUser;
    boolean validuser, validpass, gointent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        input_layout_password = (TextInputLayout) findViewById(R.id.input_layout_password);
        input_layout_email = (TextInputLayout) findViewById(R.id.input_layout_email);
        etpass = (EditText) findViewById(R.id.etpass);
        etemail = (EditText) findViewById(R.id.etemail);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        dateFormatter = new SimpleDateFormat(DatePickers.DATE_SQL_FORMAT);

        passadm = dateFormatter.format(Calendar.getInstance().getTime());
        passadm = passadm.replace("-", "");
        dbHandler = new DatabaseHandler(this);
        dbHandler.getWritableDatabase();

//        dbHandler.addUser(new User("qqq", "q"));
//        dbHandler.addUser(new User("aaa", "a"));
        dbHandler.tambahDatauser();

//        etemail.setText(passadm);
//        etpass.setText("q");
        etemail.setText("admin");
        etpass.setText(passadm);

        etpass.addTextChangedListener(new MyTextWatcher(etpass));

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
                Log.e("user masuk", "ok");
            }
        });
    }

    /**
     * Validating form
     */
    private void submitForm() {
        validpass = true;
        validuser = true;
        gointent = false;
        if (!validateEmail()) {
            return;
        }
        Log.e("user email", "ok");

        if (!validatePassword()) {
            return;
        }
        Log.e("user pass", "ok");

        if (email.equals("admin")) {
            if (etpass.getText().toString().trim().equals(passadm)) {
                Utils.saveSharedSetting(this, FieldName.OPERATOR, "admin");
                finish();
                intent = new Intent(this, HomeActivity.class);
                intent.putExtra(FieldName.TOAST, "ok");
                startActivity(intent);
            } else {
                StyleableToast.makeText(this, getResources().getString(R.string.login_adm_fail),
                        Toast.LENGTH_LONG, R.style.gagaltoast).show();
            }
        } else {
            Log.e("user else", "ok");
            Utils.saveSharedSetting(this, FieldName.OPERATOR, "user");
            listUser = dbHandler.getAllUsers();
            String passsss = etpass.getText().toString().trim();
            Log.e("user mau", listUser.size() + "");
            for (int i = 0; i < listUser.size(); i++) {
                Log.e("listttttt", listUser.get(i).getUsername() + ";;;" +
                listUser.get(i).getPassword());
                if (email.equals(listUser.get(i).getUsername())) {
                    if (passsss.equals(listUser.get(i).getPassword())) {
                        gointent = true;
                         i = listUser.size();
                    } else {
                        Log.e("salah pass", "ok");
                        validpass = false;
                        i = listUser.size();
//                        StyleableToast.makeText(this, getResources().getString(R.string.pass_wrong),
//                                Toast.LENGTH_SHORT, R.style.gagaltoast).show();
                    }
                } else {
                    Log.e("salah user", "ok");
                    validuser = false;
//                    StyleableToast.makeText(this, getResources().getString(R.string.puser_wrong),
//                            Toast.LENGTH_SHORT, R.style.gagaltoast).show();
                }
            }
            if (gointent) {
                finish();
                intent = new Intent(this, HomeActivity.class);
                intent.putExtra(FieldName.TOAST, "ok");
                startActivity(intent);
            } else if (!validpass) {
                StyleableToast.makeText(this, getResources().getString(R.string.pass_wrong),
                        Toast.LENGTH_SHORT, R.style.gagaltoast).show();
            } else if (!validuser) {
                StyleableToast.makeText(this, getResources().getString(R.string.puser_wrong),
                        Toast.LENGTH_SHORT, R.style.gagaltoast).show();
            }
        }
    }

    private boolean validateEmail() {
        email = etemail.getText().toString().trim();
//        if (email.isEmpty() || !isValidEmail(email)) {
        if (email.isEmpty()) {
            input_layout_email.setError(getString(R.string.err_msg_username));
            requestFocus(etemail);
            return false;
        } else {
            input_layout_email.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (etpass.getText().toString().trim().isEmpty()) {
            input_layout_password.setError(getString(R.string.err_msg_password));
            requestFocus(etpass);
            return false;
        } else {
            input_layout_password.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {
        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.etpass:
                    validatePassword();
                    break;
                case R.id.etemail:
                    validateEmail();
                    break;
            }
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
}
