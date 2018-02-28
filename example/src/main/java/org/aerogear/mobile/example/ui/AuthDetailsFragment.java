package org.aerogear.mobile.example.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.aerogear.mobile.auth.AuthService;
import org.aerogear.mobile.auth.Callback;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.auth.user.UserRole;
import org.aerogear.mobile.example.R;

import butterknife.BindView;
import butterknife.OnClick;

public class AuthDetailsFragment extends BaseFragment {
    private static final String TAG = "AuthDetailsFragment";

    private UserPrincipal currentUser;

    @BindView(R.id.divider_realm)
    TextView dividerRealm;

    @BindView(R.id.user_name)
    TextView userName;

    @BindView(R.id.user_email)
    TextView userEmail;

    @BindView(R.id.realm_roles)
    ListView listViewRealmRoles;

    ProgressDialog logoutProgressDialog;

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
            // show a spinner dialog to show that a logout is in progress
            showLogoutProgress();
            authService.logout(currentUser, new Callback<UserPrincipal>() {
                @Override
                public void onSuccess() {
                    hideLogoutDialog();
                    // User Logged Out Successfully
                    Log.i(TAG, "User Successfully Logged Out");
                    showMessage("Logout Successful");
                    navigateToLogin();
                }

                @Override
                public void onError(Throwable error) {
                    hideLogoutDialog();
                    // User Not Logged Out Successfully
                    Log.e(TAG, "Logout Failed: " + error.getLocalizedMessage());
                    showMessage("Logout Failed");
                }
            });
        }

    }

    /**
     * Navigate to the auth fragment.
     */
    private void navigateToLogin() {
        this.activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content, new AuthFragment())
            .commit();
    }

    /**
     * Show a snackbar message
     *
     * @param message the message to show in the snackbar
     */
    private void showMessage(final String message) {
        Snackbar.make(this.activity.findViewById(R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Show the logout progress dialog.
     */
    private void showLogoutProgress() {
        this.logoutProgressDialog = new ProgressDialog(getActivity());
        logoutProgressDialog.setTitle("Logout");
        logoutProgressDialog.setMessage("Trying to connect to the server...");
        logoutProgressDialog.setCancelable(false);
        logoutProgressDialog.show();
    }

    /**
     * Hide the logout progress dialog.
     */
    private void hideLogoutDialog() {
        logoutProgressDialog.cancel();
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
            currentUser = (UserPrincipal) args.getSerializable("currentUser");
            updateFields();
        }
    }
}
