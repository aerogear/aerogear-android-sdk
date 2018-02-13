package org.aerogear.mobile.example.ui;

import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.aerogear.mobile.auth.AuthService;
import org.aerogear.mobile.auth.Callback;
import org.aerogear.mobile.auth.authenticator.AuthenticateOptions;
import org.aerogear.mobile.auth.authenticator.OIDCAuthenticateOptions;
import org.aerogear.mobile.auth.configuration.AuthServiceConfiguration;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.example.R;

import java.security.Principal;

import butterknife.BindView;
import butterknife.OnClick;

public class AuthFragment extends BaseFragment {

    private final static String TAG = "AuthFragment";

    public static final int LOGIN_RESULT_CODE = 1;

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
        AuthServiceConfiguration authServiceConfiguration = new AuthServiceConfiguration.AuthConfigurationBuilder()
            .withRedirectUri("org.aerogear.mobile.example/callback")
            .allowSelfSignedCertificate(true)
            .build();
        authService.init(this.getContext().getApplicationContext(), authServiceConfiguration);

        UserPrincipal currentUser = authService.currentUser();
        if (currentUser == null) {
            //not current user, let's login
            OIDCAuthenticateOptions authOptions = new OIDCAuthenticateOptions(this.getActivity(), LOGIN_RESULT_CODE);
            authService.login(authOptions, new Callback<Principal>() {
                @Override
                public void onSuccess(Principal models) {
                    //user logged in, continue on..
                }

                @Override
                public void onError(Throwable error) {
                    //there is an error during the login
                }
            });
        } else {
            //we have the current user, we can show the user info
            String username = currentUser.getName();
        }

//        this.activity.getSupportFragmentManager()
//            .beginTransaction()
//            .replace(R.id.content, new AuthDetailsFragment())
//            .commit();
    }
}
