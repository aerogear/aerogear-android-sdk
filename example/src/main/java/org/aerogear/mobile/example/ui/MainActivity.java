package org.aerogear.mobile.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.aerogear.mobile.auth.AuthService;
import org.aerogear.mobile.auth.configuration.AuthServiceConfiguration;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.example.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity
                implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                        R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        authService = MobileCore.getInstance().getService(AuthService.class);
        AuthServiceConfiguration authServiceConfiguration =
                        new AuthServiceConfiguration.AuthConfigurationBuilder()
                                        .withRedirectUri("org.aerogear.mobile.example:/callback")
                                        .build();
        authService.init(getApplicationContext(), authServiceConfiguration);

        navigationView.setNavigationItemSelectedListener(this);

        navigateTo(new HomeFragment());
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AuthFragment.LOGIN_RESULT_CODE) {
            authService.handleAuthResult(data);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_http:
                navigateTo(new HttpFragment());
                break;
            case R.id.nav_auth:
                UserPrincipal currentUser = authService.currentUser();
                if (currentUser != null) {
                    navigateToAuthDetailsView(currentUser);
                } else {
                    navigateTo(new AuthFragment());
                }
                break;
            case R.id.nav_checks:
                navigateTo(new SecurityServiceFragment());
                break;
            case R.id.nav_push:
                navigateTo(new PushFragment());
                break;
            default:
                navigateTo(new HomeFragment());
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void navigateTo(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public void navigateToAuthDetailsView(UserPrincipal currentUser) {
        AuthDetailsFragment nextFragment = new AuthDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("currentUser", currentUser);
        nextFragment.setArguments(bundle);
        navigateTo(nextFragment);
    }

}
