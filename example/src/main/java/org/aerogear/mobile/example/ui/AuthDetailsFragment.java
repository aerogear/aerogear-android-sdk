package org.aerogear.mobile.example.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.aerogear.mobile.example.R;

import butterknife.BindView;
import butterknife.OnClick;

public class AuthDetailsFragment extends BaseFragment {
    private static final String TAG = "AuthDetailsFragment";

    @BindView(R.id.divider_realm)
    TextView dividerRealm;

    @BindView(R.id.user_name)
    TextView userName;

    @BindView(R.id.user_email)
    TextView userEmail;

    @BindView(R.id.realm_roles)
    ListView listViewRealmRoles;

    ArrayAdapter<String> realmRoles;

    @Override
    int getLayoutResId() {
        return R.layout.fragment_auth_details;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userName.setText("Example name");
        userEmail.setText("Example email");
    }

    @OnClick(R.id.keycloak_logout)
    public void onLogout() {
        this.activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content, new AuthFragment())
            .commit();
    }
}
