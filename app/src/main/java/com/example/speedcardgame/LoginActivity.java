package com.example.speedcardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin;
    private Button btnSignUp;
    private EditText etUserName;
    private EditText etPassWord;
    private TextView tvDetails;

    private SharedPreferences sp;

    boolean validPassWord;
    boolean validUsername;

    public boolean isNewGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);

        etUserName = findViewById(R.id.etUserName);
        etPassWord = findViewById(R.id.etPassWord);

        sp = this.getSharedPreferences(getString(R.string.preference_file_key), 0);
        isNewGame = true;
    }

    @Override
    public void onClick(View view) {
        if (view == btnLogin) {
            validPassWord = initContactData();
            if (validPassWord) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(getString(R.string.user_name), etUserName.getText().toString());
                editor.putString(getString(R.string.user_password), etPassWord.getText().toString());
                editor.putBoolean(getString(R.string.first_game), isNewGame);
                editor.apply();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            if (!validPassWord){
                tvDetails.setText("Password Incorrect");
            }
        }

        if (view == btnSignUp) {
            if (initContactData()) {
                validUsername = Character.isUpperCase(etUserName.getText().charAt(0)) && etUserName.getText().length() > 5;
                for (int i = 1; i < etUserName.getText().length(); i++) {
                    if (Character.isUpperCase(etUserName.getText().charAt(i)))
                        validUsername = false;
                }

                if (validUsername) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(getString(R.string.user_name), etUserName.getText().toString());
                    editor.putString(getString(R.string.user_password), etPassWord.getText().toString());
                    editor.apply();
                    if (prepareSendData()) {
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    }
                }
                else
                    tvDetails.setText("Username Requirements : Must Be Between Six And 20 Characters long.\nFirst Character Must Be Uppercase.\nOther Characters Must Be Lowercase");
            }
            else
                tvDetails.setText("Username Requirements : Must Be Between Six And 20 Characters long.\nFirst Character Must Be Uppercase.\nOther Characters Must Be Lowercase");
        }
    }

    private boolean prepareSendData() { // standard method
        if (TextUtils.isEmpty(etUserName.getText().toString())  // also checks whether it is empty string
                || TextUtils.isEmpty(etPassWord.getText().toString()))
        {
            tvDetails.setText("Enter values of the user");
        }

        else
        {
            DBHelper dataManager = new DBHelper(this);
            Cursor cursor = dataManager.selectByName(etUserName.getText().toString());
            if (cursor != null && cursor.getCount() > 0) {
                tvDetails.setText("User exists\nPlease choose different Username");
            }
            else {
                DBHelper helper = new DBHelper(this);

                SQLiteDatabase database = helper.getWritableDatabase();
                helper.insertTblUser(etUserName.getText().toString(), etPassWord.getText().toString());
                return true;
            }
        }
        return false;
    }

    private boolean initContactData() {
        DBHelper dataManager = new DBHelper(this);
        Cursor cursor = dataManager.validationPass(etUserName.getText().toString(), etPassWord.getText().toString());
        if (cursor != null && cursor.getCount() > 0) {
            return true;
        }
        return false;
    }
}