package org.aerogear.mobile.example.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import org.aerogear.mobile.example.R;
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
        // perform detections
        detectRoot();
        detectDeviceLock();
        detectEmulator();
        debuggerDetected();
//        detectHookingFramework();
//        detectBackupEnabled();
//        detectDeviceEncryptionStatus();
//        detectLatestOS();
//        detectDeveloperOptions();

        // get trust score
        setTrustScore();
    }

    /**
     * Detect if the device is rooted.
     */
    public void detectRoot() {
        totalTests++;
        SecurityCheckResult result = securityService.check(SecurityCheckType.IS_ROOTED);
        if (result.passed()) {
            setDetected(rootAccess, R.string.root_detected_positive);
        }
    }

    /**
     * Detect if the device has a lock screen setup (pin, password etc).
     */
    public void detectDeviceLock() {
        totalTests++;
        SecurityCheckResult result = securityService.check(SecurityCheckType.HAS_SCREENLOCK);
        if(result.passed()){
            setDetected(lockScreenSetup, R.string.device_lock_detected_positive);
        }
    }

    /**
     * Detect if a debugger is attached to the application.
     */
    public void debuggerDetected() {
        totalTests++;
        SecurityCheckResult result = securityService.check(SecurityCheckType.IS_DEBUGGER);
        if(result.passed()){
            setDetected(debuggerAccess, R.string.debugger_detected_positive);
        }
    }

    /**
     * Detect if the application is being run in an emulator.
     */
    public void detectEmulator() {
        totalTests++;
        SecurityCheckResult result = securityService.check(SecurityCheckType.IS_EMULATOR);
        if(result.passed()){
            setDetected(emulatorAccess, R.string.emulator_detected_positive);
        }
    }

    /**
     * Detect if a hooking framework application is installed on the device
     */
    public void detectHookingFramework() {
        totalTests++;
        //TODO: add check
    }

    /**
     * Function to check if the backup flag is enabled in the application manifest file
     */
    public void detectBackupEnabled() {
        totalTests++;
        //TODO: add check
    }

    /**
     * Function to check if the devices filesystem is encrypted
     */
    public void detectDeviceEncryptionStatus() {
        totalTests++;
        //TODO: add check
    }

    /**
     * Function to check if the device is running the latest Android OS
     */
    public void detectLatestOS() {
        totalTests++;
        //TODO: add check
    }

    /**
     * Detect if the developer options mode is enabled on the device
     */
    public void detectDeveloperOptions() {
        totalTests++;
        SecurityCheckResult result = securityService.check(SecurityCheckType.IS_DEVELOPER_MODE);
        if (result.passed()) {
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
        trustScoreText.setText(score + "%");
        trustScoreHeader.setText(getText(R.string.trust_score_header_title) + "\n(" + Math.round(totalTests) + " Tests)");

        // change the score percentage colour depending on the trust score
        if (trustScore.getProgress() == 100) {
            trustScoreHeader.setBackgroundColor(getResources().getColor(R.color.green));
            trustScoreText.setBackgroundColor(getResources().getColor(R.color.green));
        }
    }
}
