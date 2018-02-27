package org.aerogear.mobile.example.ui;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.aerogear.mobile.auth.AuthService;
import org.aerogear.mobile.auth.Callback;
import org.aerogear.mobile.auth.authenticator.DefaultAuthenticateOptions;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.example.R;

import butterknife.BindView;
import butterknife.OnClick;

public class AuthFragment extends BaseFragment {

    public static final int LOGIN_RESULT_CODE = 1;
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
        AuthService authService = ((MainActivity) getActivity()).getAuthService();
        DefaultAuthenticateOptions authOptions = new DefaultAuthenticateOptions(this.getActivity(), LOGIN_RESULT_CODE);
        authService.login(authOptions, new Callback<UserPrincipal>() {
            @Override
            public void onSuccess(UserPrincipal models) {
                //user logged in, continue on..
                Log.i(TAG, "user logged in " + models.toString());
                ((MainActivity) getActivity()).navigateToAuthDetailsView(models);
            }

            @Override
            public void onError(Throwable error) {
                //there is an error during the login
                Log.e(TAG, "logined failed due to error " + error.getLocalizedMessage());
            }
        });
    }
}
