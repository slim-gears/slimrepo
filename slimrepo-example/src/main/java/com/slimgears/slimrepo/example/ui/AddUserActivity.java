// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.example.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.slimgears.slimrepo.core.interfaces.RepositoryService;
import com.slimgears.slimrepo.example.R;
import com.slimgears.slimrepo.example.repository.*;

import java.io.IOException;

/**
 * Created by Denis on 23-Apr-15
 *
 */
public class AddUserActivity extends Activity {
    private UserRepositoryService mUserRepositoryService;
    private EditText mViewFirstName;
    private EditText mViewLastName;
    private EditText mViewAge;
    private Spinner mViewCountry;
    private Button mButtonCancel;
    private Button mButtonOk;

    class CountriesAdapter extends BaseAdapter {
        private final CountryEntity[] mCountries;

        CountriesAdapter(final UserRepositoryService repoService) throws IOException {
            mCountries = repoService.query(new RepositoryService.QueryAction<UserRepository, CountryEntity[]>() {
                @Override
                public CountryEntity[] execute(UserRepository repository) throws IOException {
                    return repository.countries().toArray();
                }
            });
        }

        @Override
        public int getCount() {
            return mCountries.length;
        }

        @Override
        public Object getItem(int position) {
            return mCountries[position];
        }

        @Override
        public long getItemId(int position) {
            return mCountries[position].getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = new TextView(AddUserActivity.this);
                textView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                textView = (TextView)convertView;
            }
            textView.setText(mCountries[position].getName());
            return textView;
        }
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        setContentView(R.layout.activity_add_user);

        mViewFirstName = (EditText)findViewById(R.id.text_first_name);
        mViewLastName = (EditText)findViewById(R.id.text_last_name);
        mViewAge = (EditText)findViewById(R.id.number_age);
        mViewCountry = (Spinner)findViewById(R.id.selection_country);
        mButtonOk = (Button)findViewById(R.id.button_ok);
        mButtonCancel = (Button)findViewById(R.id.button_cancel);

        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onProceed();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });

        mUserRepositoryService = new GeneratedUserRepositoryService(this);

        try {
            mViewCountry.setAdapter(new CountriesAdapter(mUserRepositoryService));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void show(Context context) {
        context.startActivity(new Intent(context, AddUserActivity.class));
    }

    private void onProceed() throws IOException {
        mUserRepositoryService.users().add(UserEntity.builder()
                .firstName(mViewFirstName.getText().toString())
                .lastName(mViewLastName.getText().toString())
                .age(Integer.valueOf(mViewAge.getText().toString()))
                .country((CountryEntity) mViewCountry.getSelectedItem())
                .build());
        finish();
    }

    private void onCancel() {
        finish();
    }
}
