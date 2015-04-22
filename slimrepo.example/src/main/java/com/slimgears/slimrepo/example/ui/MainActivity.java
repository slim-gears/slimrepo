// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.example.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.slimgears.slimrepo.android.core.SqliteOrmServiceProvider;
import com.slimgears.slimrepo.example.R;
import com.slimgears.slimrepo.example.repository.CountryEntity;
import com.slimgears.slimrepo.example.repository.GeneratedUserRepositoryService;
import com.slimgears.slimrepo.example.repository.UserEntity;
import com.slimgears.slimrepo.example.repository.UserRepository;
import com.slimgears.slimrepo.example.repository.UserRepositoryService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Denis on 22-Apr-15
 * <File Description>
 */
public class MainActivity extends Activity {
    UserRepositoryService userRepositoryService;

    class UserListAdapter extends BaseAdapter {
        private final List<UserEntity> users;
        private final Map<Integer, CountryEntity> countries;

        UserListAdapter(List<UserEntity> users, Map<Integer, CountryEntity> countries) {
            this.users = users;
            this.countries = countries;
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return users.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater
                        .from(MainActivity.this)
                        .inflate(R.layout.user_list_item, parent, false);
            }

            UserEntity user = users.get(position);

            setText(convertView, R.id.item_id, user.getId());
            setText(convertView, R.id.item_name, user.getFullName());
            setText(convertView, R.id.item_age, user.getAge());
            setText(convertView, R.id.item_country, countries.get(user.getCountryId()));

            return convertView;
        }

        private void setText(View view, int id, Object text) {
            TextView textView = (TextView)view.findViewById(id);
            textView.setText(text.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        setContentView(R.layout.main);

        userRepositoryService = new GeneratedUserRepositoryService(new SqliteOrmServiceProvider(this));

        try {
            UserRepository repo = userRepositoryService.open();
            try {
                ListView userListView = (ListView) findViewById(R.id.user_list_view);
                userListView.setAdapter(new UserListAdapter(repo.users().toList(), repo.countries().toMap()));
            } finally {
                repo.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
