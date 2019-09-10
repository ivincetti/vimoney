package ru.vincetti.vimoney.data;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private final static Object LOCK = new Object();

    private static AppExecutors sInstance;
    private final Executor diskIO;
    private final Executor mainThreadIO;

    public AppExecutors(Executor diskIO, Executor mainThreadIO) {
        this.diskIO = diskIO;
        this.mainThreadIO = mainThreadIO;
    }

    public static AppExecutors getsInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutors(
                        Executors.newSingleThreadExecutor(),
                        new MainThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor mainThreadIO() {
        return mainThreadIO;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable runnable) {
            mainThreadHandler.post(runnable);
        }
    }
}
