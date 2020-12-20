package com.example.remoteservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RemoteService extends Service {
    public static final String TAG = "RemoteService";

    List<IRemoteServiceCallback> listeners = new ArrayList<>();
    private Binder binder = new IRemoteService.Stub() {
        @Override
        public boolean addCallback(IRemoteServiceCallback callback) throws RemoteException {
            Log.d(TAG, "Add callback : " + callback);
            listeners.add(callback);
            return true;
        }

        @Override
        public boolean removeCallback(IRemoteServiceCallback callback) throws RemoteException {
            Log.d(TAG, "Remove callback : " + callback);
            listeners.remove(callback);
            return true;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        handler.sendEmptyMessageDelayed(0, 5000);
        return binder;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            listeners.forEach(listener -> {
                try {
                    listener.onItemAdded("new item");
                    listener.onItemRemoved("old item");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
            sendEmptyMessageDelayed(0, 5000);
        }
    };
}
