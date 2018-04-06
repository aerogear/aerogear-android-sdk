package org.aerogear.mobile.example.ui;

import android.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.aerogear.mobile.auth.AuthService;
import org.aerogear.mobile.auth.authenticator.DefaultAuthenticateOptions;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.example.R;

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
        AuthService authService = ((MainActivity) getActivity()).getAuthService();
        DefaultAuthenticateOptions authOptions =
                        new DefaultAuthenticateOptions(this.getActivity(), LOGIN_RESULT_CODE);
        authService.login(authOptions, new Callback<UserPrincipal>() {
            @Override
            public void onSuccess(final UserPrincipal models) {
                // user logged in, continue on..
                Log.i(TAG, "user logged in " + models);
                ((MainActivity) getActivity()).navigateToAuthDetailsView(models);
            }

            @Override
            public void onError(final Throwable error) {
                // there is an error during the login
                Log.e(TAG, "login failed due to error " + error.getLocalizedMessage());
                messageDialog("Failed to Authenticate", error.getLocalizedMessage());
            }
        });
    }

    public void messageDialog(final String title, final String message) {
        activity.runOnUiThread(() -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
            dialogBuilder.setTitle(title).setMessage(message).show().create();
        });
    }
}
