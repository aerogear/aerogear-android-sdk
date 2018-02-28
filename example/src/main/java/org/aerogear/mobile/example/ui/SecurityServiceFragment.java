package org.aerogear.mobile.example.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.example.R;
import org.aerogear.mobile.security.SecurityCheckExecutor;
import org.aerogear.mobile.security.SyncSecurityCheckExecutor;
import org.aerogear.mobile.security.SecurityCheckType;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.SecurityService;

import java.text.MessageFormat;
import java.util.Map;

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

    @BindView(R.id.hookingDetected)
    RadioButton hookingDetected;

    @BindView(R.id.allowBackup)
    RadioButton allowBackup;

    @BindView(R.id.deviceEncrypted)
    RadioButton deviceEncrypted;

    @BindView(R.id.deviceOS)
    RadioButton deviceOS;

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
        securityService = activity.mobileCore.getInstance(SecurityService.class);
        runTests();
    }

    /**
     * Executes all test and calls setTrustScore() to calculate an average score
     */
    public void runTests() {

        SyncSecurityCheckExecutor executor = SecurityCheckExecutor.Builder.newSyncExecutor(this.getContext())
            .withSecurityCheck(SecurityCheckType.IS_ROOTED)
            .withSecurityCheck(SecurityCheckType.SCREEN_LOCK_ENABLED)
            .withSecurityCheck(SecurityCheckType.IS_EMULATOR)
            .withSecurityCheck(SecurityCheckType.IS_DEBUGGER)
            .withSecurityCheck(SecurityCheckType.IS_DEVELOPER_MODE)
            .withMetricsService(activity.mobileCore.getInstance(MetricsService.class))
            .build();
        Map<String, SecurityCheckResult> results = executor.execute();

        // perform detections
        detectRoot(results);
        detectDeviceLock(results);
        debuggerDetected(results);
        detectEmulator(results);
        detectHookingFramework(results);
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
        SecurityCheckResult result = results.get(SecurityCheckType.IS_ROOTED.getName());
        if (result != null && result.passed()) {
            setDetected(rootAccess, R.string.root_detected_positive);
        }
    }

    /**
     * Detect if the device has a lock screen setup (pin, password etc).
     */
    public void detectDeviceLock(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result = results.get(SecurityCheckType.SCREEN_LOCK_ENABLED.getName());
        if (result != null && result.passed()) {
            setDetected(lockScreenSetup, R.string.device_lock_detected_negative);
        }
    }

    /**
     * Detect if a debugger is attached to the application.
     */
    public void debuggerDetected(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result = results.get(SecurityCheckType.IS_DEBUGGER.getName());
        if (result != null && result.passed()) {
            setDetected(debuggerAccess, R.string.debugger_detected_positive);
        }
    }

    /**
     * Detect if the application is being run in an emulator.
     */
    public void detectEmulator(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result = results.get(SecurityCheckType.IS_EMULATOR.getName());
        if (result != null && result.passed()) {
            setDetected(emulatorAccess, R.string.emulator_detected_positive);
        }
    }

    /**
     * Detect if a hooking framework application is installed on the device
     */
    public void detectHookingFramework(Map<String, SecurityCheckResult> results) {
        totalTests++;
        //TODO: add check
    }

    /**
     * Function to check if the backup flag is enabled in the application manifest file
     */
    public void detectBackupEnabled(Map<String, SecurityCheckResult> results) {
        totalTests++;
        //TODO: add check
    }

    /**
     * Function to check if the devices filesystem is encrypted
     */
    public void detectDeviceEncryptionStatus(Map<String, SecurityCheckResult> results) {
        totalTests++;
        //TODO: add check
    }

    /**
     * Detect if the developer options mode is enabled on the device
     */
    public void detectDeveloperOptions(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result = results.get(SecurityCheckType.IS_DEVELOPER_MODE.getName());
        if (result != null && result.passed()) {
            setDetected(developerOptions, R.string.developer_options_positive);
        }
    }

    /**
     * Function to allow updates to the radio buttons UI
     *
     * @param uiElement    - the UI element to update
     * @param textResource - the text resource to set the updates text for
     */
    public void setDetected(RadioButton uiElement, int textResource) {
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
        trustScoreHeader.setText(MessageFormat.format("{0}\n({1} Tests)", getText(R.string.trust_score_header_title), Math.round(totalTests)));

        // change the score percentage colour depending on the trust score
        if (trustScore.getProgress() == 100) {
            trustScoreHeader.setBackgroundColor(getResources().getColor(R.color.green));
            trustScoreText.setBackgroundColor(getResources().getColor(R.color.green));
        }
    }
}
