package org.aerogear.mobile.example.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.aerogear.mobile.auth.AuthService;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.auth.user.UserRole;
import org.aerogear.mobile.example.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AuthDetailsFragment extends BaseFragment {
    private static final String TAG = "AuthDetailsFragment";

    public static final String PRINCIPAL = "USER-PRINCIPAL";

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

        AuthService authService = (AuthService) activity.mobileCore.getInstance(AuthService.class);

        UserPrincipal userPrincipal = authService.currentUser();

        userName.setText(userPrincipal.getName());
        userEmail.setText(userPrincipal.getEmail());

        // roles to array list
        final List<String> roles = new ArrayList<>(userPrincipal.getRoles().size());
        for (UserRole role : userPrincipal.getRoles()) {
            roles.add(role.getName());
        }

        listViewRealmRoles.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, roles));
    }

    @OnClick(R.id.keycloak_logout)
    public void onLogout() {
        this.activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content, new AuthFragment())
            .commit();
    }
}
