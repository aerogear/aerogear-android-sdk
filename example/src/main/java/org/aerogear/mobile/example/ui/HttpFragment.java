package org.aerogear.mobile.example.ui;

import java.util.List;

import com.github.nitrico.lastadapter.LastAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.reactive.Responder;
import org.aerogear.mobile.example.BR;
import org.aerogear.mobile.example.R;
import org.aerogear.mobile.example.model.User;

import butterknife.BindView;

public class HttpFragment extends BaseFragment {

    private static final String TAG = "HttpFragment";

    @BindView(R.id.userList)
    RecyclerView userList;

    private ObservableArrayList<User> users = new ObservableArrayList<>();

    @Override
    int getLayoutResId() {
        return R.layout.fragment_http;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userList.setLayoutManager(new LinearLayoutManager(getContext()));

        new LastAdapter(users, BR.user).map(User.class, R.layout.item_http).into(userList);

        HttpRequest httpRequest = MobileCore.getInstance().getHttpLayer().newRequest();

        httpRequest.get("https://jsonplaceholder.typicode.com/users").map((response) -> {
            String stringBody = response.stringBody();
            List<User> retrievedUsers = new Gson().fromJson(stringBody,
                            new TypeToken<List<User>>() {}.getType());
            return retrievedUsers;
        }).respondOn(new AppExecutors().mainThread()).respondWith(new Responder<List<User>>() {
            @Override
            public void onResult(List<User> retrievedUsers) {
                MobileCore.getLogger().info("Users: " + retrievedUsers.size());
                users.addAll(retrievedUsers);
            }

            @Override
            public void onException(Exception exception) {
                Log.e(TAG, exception.toString());
            }

        });
    }

}
