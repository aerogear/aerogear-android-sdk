package org.aerogear.mobile.example.ui;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.example.R;
import org.aerogear.mobile.example.handler.NotificationBarMessageHandler;
import org.aerogear.mobile.push.MessageHandler;
import org.aerogear.mobile.push.PushService;
import org.aerogear.mobile.push.UnifiedPushMessage;

import butterknife.BindView;
import butterknife.OnClick;

public class PushFragment extends BaseFragment implements MessageHandler {

    @BindView(R.id.messages)
    ListView messageList;

    @BindView(R.id.register)
    Button register;

    @BindView(R.id.unregister)
    Button unregister;

    private static final String TAG = PushFragment.class.getName();
    private ArrayAdapter<String> adapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                        new ArrayList<>());
        messageList.setAdapter(adapter);
    }

    @Override
    int getLayoutResId() {
        return R.layout.fragment_push;
    }

    @Override
    public void onStart() {
        super.onStart();
        PushService.registerMainThreadHandler(this);
        PushService.unregisterBackgroundThreadHandler(NotificationBarMessageHandler.getInstance());
    }

    @Override
    public void onStop() {
        super.onStop();
        PushService.unregisterMainThreadHandler(this);
        PushService.registerBackgroundThreadHandler(NotificationBarMessageHandler.getInstance());
    }

    @Override
    public void onMessage(Context context, Map<String, String> message) {
        adapter.add(message.get(UnifiedPushMessage.MESSAGE));
    }

    @OnClick(R.id.register)
    void register() {
        register.setEnabled(false);

        PushService pushService = MobileCore.getInstance().getService(PushService.class);
        pushService.registerDevice(new Callback() {
            @Override
            public void onSuccess() {
                new AppExecutors().mainThread().execute(() -> registered(true));
            }

            @Override
            public void onError(Throwable error) {
                new AppExecutors().mainThread().execute(() -> {
                    register.setEnabled(true);
                    MobileCore.getLogger().error(TAG, error.getMessage(), error);
                    registered(false);
                    Toast.makeText(getContext(), R.string.device_register_error, Toast.LENGTH_LONG)
                                    .show();
                });
            }
        });
    }

    @OnClick(R.id.unregister)
    void unregister() {
        unregister.setEnabled(false);

        PushService pushService = MobileCore.getInstance().getService(PushService.class);
        pushService.unregisterDevice(new Callback() {
            @Override
            public void onSuccess() {
                new AppExecutors().mainThread().execute(() -> registered(false));

            }

            @Override
            public void onError(Throwable error) {
                register.setEnabled(false);
                MobileCore.getLogger().error(error.getMessage(), error);
                new AppExecutors().mainThread().execute(() -> {
                    registered(true);
                    Toast.makeText(getContext(), R.string.device_register_error, Toast.LENGTH_LONG)
                                    .show();
                });
            }
        });
    }

    private void registered(boolean registered) {
        register.setEnabled(!registered);
        unregister.setEnabled(registered);
    }

}
