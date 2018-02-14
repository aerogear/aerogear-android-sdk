package org.aerogear.mobile.example.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.aerogear.mobile.auth.AuthService;
import org.aerogear.mobile.auth.user.UserPrincipalImpl;
import org.aerogear.mobile.auth.user.UserRole;
import org.aerogear.mobile.example.R;

import butterknife.BindView;
import butterknife.OnClick;

public class AuthDetailsFragment extends BaseFragment {
    private static final String TAG = "AuthDetailsFragment";

    private UserPrincipalImpl currentUser;

    @BindView(R.id.divider_realm)
    TextView dividerRealm;

    @BindView(R.id.user_name)
    TextView userName;

    @BindView(R.id.user_email)
    TextView userEmail;

    @BindView(R.id.realm_roles)
    ListView listViewRealmRoles;

    ArrayAdapter<UserRole> realmRoles;

    @Override
    int getLayoutResId() {
        return R.layout.fragment_auth_details;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.keycloak_logout)
    public void onLogout() {
        if (currentUser != null) {
            AuthService authService = ((MainActivity) this.activity).getAuthService();
            authService.logout(currentUser);
        }
        this.activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content, new AuthFragment())
            .commit();
    }

    public void updateFields() {
        if (currentUser != null) {
            userName.setText(this.currentUser.getName());
            userEmail.setText(this.currentUser.getEmail());
            UserRole[] rolesArray = new UserRole[currentUser.getRoles().size()];
            rolesArray = currentUser.getRoles().toArray(rolesArray);
            realmRoles = new ArrayAdapter<UserRole>(getContext(), R.layout.item_auth, rolesArray);
            listViewRealmRoles.setAdapter(realmRoles);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null ) {
            currentUser = (UserPrincipalImpl) args.getSerializable("currentUser");
            updateFields();
        }
    }
}
