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

import com.slimgears.slimrepo.example.R;
import com.slimgears.slimrepo.example.repository.GeneratedUserRepositoryService;
import com.slimgears.slimrepo.example.repository.UserEntity;
import com.slimgears.slimrepo.example.repository.UserRepository;
import com.slimgears.slimrepo.example.repository.UserRepositoryService;

import java.io.IOException;
import java.util.List;

/**
 * Created by Denis on 22-Apr-15
 * <File Description>
 */
public class MainActivity extends Activity {

    class UserListAdapter extends BaseAdapter {
        private final List<UserEntity> users;

        UserListAdapter(List<UserEntity> users) {
            this.users = users;
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
                        .inflate(R.layout.list_item_user, parent, false);
            }

            UserEntity user = users.get(position);

            setText(convertView, R.id.item_id, user.getId());
            setText(convertView, R.id.item_name, user.getFullName());
            setText(convertView, R.id.item_age, user.getAge());
            setText(convertView, R.id.item_country, user.getCountry().getName());

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

        setContentView(R.layout.activity_main);

        UserRepositoryService userRepositoryService = new GeneratedUserRepositoryService(this);

        setOnClickListener(R.id.button_add_user, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddUserActivity.show(MainActivity.this);
            }
        });

        setOnClickListener(R.id.button_add_country, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCountryActivity.show(MainActivity.this);
            }
        });

        try {
            UserRepository repo = userRepositoryService.open();
            try {
                ListView userListView = (ListView) findViewById(R.id.user_list_view);
                userListView.setAdapter(new UserListAdapter(repo.users().toList()));
            } finally {
                repo.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setOnClickListener(int viewId, View.OnClickListener listener) {
        findViewById(viewId).setOnClickListener(listener);
    }
}
