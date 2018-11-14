package com.ainosi.doyle.ainomobilepayment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ainosi.doyle.ainomobilepayment.entity.User;
import com.ainosi.doyle.ainomobilepayment.helper.DatabaseHandler;
import com.ainosi.doyle.ainomobilepayment.helper.FieldName;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout input_layout_password, input_layout_email;
    private String passs, email;
    private EditText etpass, etemail;
    private Button btnSignUp;
    private Intent intent;
    private ConstraintLayout layoutluar;
    boolean doubleBackToExitPressedOnce = false;
    private List<User> listUser;
    private DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        input_layout_password = (TextInputLayout) findViewById(R.id.input_layout_passw);
        input_layout_email = (TextInputLayout) findViewById(R.id.input_layout_emaill);
        etpass = (EditText) findViewById(R.id.etpassw);
        etemail = (EditText) findViewById(R.id.etemaill);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        layoutluar = (ConstraintLayout) findViewById(R.id.layoutluar);

        etpass.addTextChangedListener(new MyTextWatcher(etpass));
        dbHandler = new DatabaseHandler(this);
        dbHandler.getWritableDatabase();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        listUser = dbHandler.getAllUsers();
        Log.e("list user", listUser.size() + "");
        for (int i = 0; i < listUser.size(); i++) {
            Log.e("list user", listUser.get(i).getUsername() + ";;;" + listUser.get(i).getPassword());
            if (email.equals(listUser.get(i).getUsername())) {
                StyleableToast.makeText(this, getResources().getString(R.string.user_already),
                        Toast.LENGTH_SHORT, R.style.gagaltoast).show();
            } else {
                dbHandler.addUser(new User(email, passs));
                StyleableToast.makeText(this, getResources().getString(R.string.success_registeruser),
                        Toast.LENGTH_SHORT, R.style.mytoast).show();
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
        passs = etpass.getText().toString().trim();
        if (passs.isEmpty()) {
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

    @Override
    public void onBackPressed() {
        finish();
        intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
