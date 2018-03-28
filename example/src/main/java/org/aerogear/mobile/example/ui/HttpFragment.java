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

import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.example.BR;
import org.aerogear.mobile.example.R;
import org.aerogear.mobile.example.model.User;

import butterknife.BindView;

public class HttpFragment extends BaseFragment {

    @BindView(R.id.userList)
    RecyclerView userList;

    private ObservableArrayList<User> users = new ObservableArrayList<>();

    @Override
    int getLayoutResId() {
        return R.layout.fragment_http;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userList.setLayoutManager(new LinearLayoutManager(getContext()));

        new LastAdapter(users, BR.user).map(User.class, R.layout.item_http).into(userList);

        HttpRequest httpRequest = activity.mobileCore.getHttpLayer().newRequest();
        httpRequest.get("https://jsonplaceholder.typicode.com/users");
        HttpResponse httpResponse = httpRequest.execute();
        httpResponse.onError(() -> {
            Log.e("<<< http error >>>", httpResponse.getError().toString());
        });
        httpResponse.onSuccess(() -> {
            String jsonResponse = httpResponse.stringBody();
            new AppExecutors().mainThread().execute(() -> {

                List<User> retrievesUsers = new Gson().fromJson(jsonResponse,
                                new TypeToken<List<User>>() {}.getType());

                activity.mobileCore.getLogger().info("Users: " + retrievesUsers.size());

                users.addAll(retrievesUsers);
            });
        });
    }

}
