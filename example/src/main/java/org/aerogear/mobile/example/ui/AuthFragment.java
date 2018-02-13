package org.aerogear.mobile.example.ui;

import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.aerogear.mobile.auth.AuthService;
import org.aerogear.mobile.auth.credentials.KeyCloakWebCredentials;
import org.aerogear.mobile.example.R;

import butterknife.BindView;
import butterknife.OnClick;

public class AuthFragment extends BaseFragment {

    private final static String TAG = "AuthFragment";

    @BindView(R.id.keycloak_login)
    TextView keycloakLogin;

    @BindView(R.id.auth_message)
    TextView authMessage;

    @BindView(R.id.background)
    ImageView background;

    @BindView(R.id.logo)
    ImageView logo;

    @Override
    int getLayoutResId() {
        return R.layout.fragment_auth;
    }

    @OnClick(R.id.keycloak_login)
    public void doLogin() {
        Log.i(TAG, "Performing login");
        // This will actually happen back in the activity when login is implemented.

        AuthService authService = (AuthService) activity.mobileCore.getInstance(AuthService.class);
        authService.init(this.getContext().getApplicationContext(), null);

        try {
            authService.login(new KeyCloakWebCredentials(this.getContext().getApplicationContext(), Uri.parse("org.aerogear.mobile.example:/callback"), this.getActivity()));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        this.activity.getSupportFragmentManager()
//            .beginTransaction()
//            .replace(R.id.content, new AuthDetailsFragment())
//            .commit();
    }
}
