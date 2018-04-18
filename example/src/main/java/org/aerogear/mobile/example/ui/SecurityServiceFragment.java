package org.aerogear.mobile.example.ui;

import java.text.MessageFormat;
import java.util.Map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.example.R;
import org.aerogear.mobile.security.SecurityCheckExecutor;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.SecurityCheckType;
import org.aerogear.mobile.security.SecurityService;

import butterknife.BindView;


/**
 * Fragment for show casing security singleThreadService self-defence checks.
 */

public class SecurityServiceFragment extends BaseFragment {

    View view;

    @BindView(R.id.trustScore)
    ProgressBar trustScore;

    @BindView(R.id.trustScoreText)
    TextView trustScoreText;

    @BindView(R.id.trustScoreHeader)
    TextView trustScoreHeader;

    @BindView(R.id.rootAccess)
    RadioButton rootAccess;

    @BindView(R.id.lockScreenSetup)
    RadioButton lockScreenSetup;

    @BindView(R.id.emulatorAccess)
    RadioButton emulatorAccess;

    @BindView(R.id.debuggerAccess)
    RadioButton debuggerAccess;

    @BindView(R.id.allowBackup)
    RadioButton allowBackup;

    @BindView(R.id.deviceEncrypted)
    RadioButton deviceEncrypted;

    @BindView(R.id.developerOptions)
    RadioButton developerOptions;


    // Used to calculate trust store percentage
    private float totalTests = 0;
    private float totalTestFailures = 0;
    private SecurityService securityService;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_security_service;
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        securityService = MobileCore.getInstance().getService(SecurityService.class);
        runTests();
    }

    /**
     * Executes all test and calls setTrustScore() to calculate an average score
     */
    public void runTests() {

        Map<String, SecurityCheckResult> results = SecurityCheckExecutor.Builder
                        .newSyncExecutor(this.getContext())
                        .withSecurityCheck(SecurityCheckType.NOT_ROOTED)
                        .withSecurityCheck(SecurityCheckType.SCREEN_LOCK_ENABLED)
                        .withSecurityCheck(SecurityCheckType.NOT_IN_EMULATOR)
                        .withSecurityCheck(SecurityCheckType.NO_DEBUGGER)
                        .withSecurityCheck(SecurityCheckType.NO_DEVELOPER_MODE)
                        .withMetricsService(
                                        MobileCore.getInstance().getService(MetricsService.class))
                        .build().execute();

        // perform detections
        detectRoot(results);
        detectDeviceLock(results);
        debuggerDetected(results);
        detectEmulator(results);
        detectBackupEnabled(results);
        detectDeviceEncryptionStatus(results);
        detectDeveloperOptions(results);

        // get trust score
        setTrustScore();
    }

    /**
     * Detect if the device is rooted.
     */
    public void detectRoot(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result = results.get(SecurityCheckType.NOT_ROOTED.getType());
        if (result != null && !result.passed()) {
            setCheckFailed(rootAccess, R.string.root_detected_positive);
        }
    }

    /**
     * Detect if the device has a lock screen setup (pin, password etc).
     */
    public void detectDeviceLock(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result = results.get(SecurityCheckType.SCREEN_LOCK_ENABLED.getType());
        if (result != null && !result.passed()) {
            setCheckFailed(lockScreenSetup, R.string.device_lock_detected_negative);
        }
    }

    /**
     * Detect if a debugger is attached to the application.
     */
    public void debuggerDetected(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result = results.get(SecurityCheckType.NO_DEBUGGER.getType());
        if (result != null && !result.passed()) {
            setCheckFailed(debuggerAccess, R.string.debugger_detected_positive);
        }
    }

    /**
     * Detect if the application is being run in an emulator.
     */
    public void detectEmulator(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result = results.get(SecurityCheckType.NOT_IN_EMULATOR.getType());
        if (result != null && !result.passed()) {
            setCheckFailed(emulatorAccess, R.string.emulator_detected_positive);
        }
    }

    /**
     * Function to check if the backup flag is enabled in the application manifest file
     */
    public void detectBackupEnabled(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result = securityService.check(SecurityCheckType.ALLOW_BACKUP_DISABLED);
        if (result != null && !result.passed()) {
            setCheckFailed(allowBackup, R.string.allow_backup_detected_positive);
        }
    }

    /**
     * Function to check if the devices filesystem is encrypted
     */
    public void detectDeviceEncryptionStatus(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result =
                        securityService.check(SecurityCheckType.HAS_ENCRYPTION_ENABLED);
        if (result != null && !result.passed()) {
            setCheckFailed(deviceEncrypted, R.string.device_encrypted_negative);
        }
    }

    /**
     * Detect if the developer options mode is enabled on the device
     */
    public void detectDeveloperOptions(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result = results.get(SecurityCheckType.NO_DEVELOPER_MODE.getType());
        if (result != null && !result.passed()) {
            setCheckFailed(developerOptions, R.string.developer_options_positive);
        }
    }

    /**
     * Function to allow updates to the radio buttons UI when a security check has failed Passed
     * tests do not need updating due to being the default UI state
     *
     * @param uiElement - the UI element to update
     * @param textResource - the text resource to set the updates text for
     */
    public void setCheckFailed(RadioButton uiElement, int textResource) {
        totalTestFailures++;
        uiElement.setText(textResource);
        uiElement.setTextColor(getResources().getColor(R.color.primary));
    }

    /**
     * Set the trust score colouring as an indicator
     */
    public void setTrustScore() {
        int score = 100 - Math.round(((totalTestFailures / totalTests) * 100));
        trustScore.setProgress(score);
        trustScoreText.setText(MessageFormat.format("{0}%", score));
        trustScoreHeader.setText(MessageFormat.format("{0}\n({1} Tests)",
                        getText(R.string.trust_score_header_title), Math.round(totalTests)));

        // change the score percentage colour depending on the trust score
        if (trustScore.getProgress() == 100) {
            trustScoreHeader.setBackgroundColor(getResources().getColor(R.color.green));
            trustScoreText.setBackgroundColor(getResources().getColor(R.color.green));
        }
    }
}
