// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.example.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.slimgears.slimrepo.example.R;
import com.slimgears.slimrepo.example.repository.CountryEntity;
import com.slimgears.slimrepo.example.repository.GeneratedUserRepositoryService;
import com.slimgears.slimrepo.example.repository.UserRepositoryService;

import java.io.IOException;

/**
 * Created by Denis on 23-Apr-15
 *
 */
public class AddCountryActivity extends Activity {
    private UserRepositoryService mUserRepositoryService;
    private EditText mViewName;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        setContentView(R.layout.activity_add_country);

        mViewName = (EditText)findViewById(R.id.text_name);

        Button buttonOk = (Button) findViewById(R.id.button_ok);
        Button buttonCancel = (Button) findViewById(R.id.button_cancel);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onProceed();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });

        mUserRepositoryService = new GeneratedUserRepositoryService(this);
    }

    public static void show(Context context) {
        context.startActivity(new Intent(context, AddCountryActivity.class));
    }

    private void onProceed() throws IOException {
        mUserRepositoryService.countries().add(CountryEntity
                        .create()
                        .setName(mViewName.getText().toString()));
        finish();
    }

    private void onCancel() {
        finish();
    }
}
