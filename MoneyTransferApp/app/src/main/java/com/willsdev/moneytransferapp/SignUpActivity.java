package com.willsdev.moneytransferapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class SignUpActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Fresh Dropdown
        final Spinner dropdown = findViewById(R.id.country_spinner);
        String[] items = new String[]{"Australia", "Russia", "China"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        final EditText first_name = findViewById(R.id.signup_username);
        final EditText last_name = findViewById(R.id.signup_password);
        final EditText phone_number = findViewById(R.id.signup_password_confirm);

        Button sign_up_next = findViewById(R.id.splash_login);
        sign_up_next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SignUpActivity.this, SignUpActivity2.class);
                intent.putExtra("first_name",first_name.getText().toString());
                intent.putExtra("last_name",last_name.getText().toString());
                intent.putExtra("phone_number",phone_number.getText().toString());
                intent.putExtra("country",dropdown.getSelectedItem().toString());
                startActivity(intent);
            }
        });
    }
}
