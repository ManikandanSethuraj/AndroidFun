package app.manny.mvvmretrofit;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AppExecuters {

    private static AppExecuters instance;

    public static AppExecuters getInstance(){
        if (instance == null){
            instance = new AppExecuters();
        }
        return instance;
    }


    private final ScheduledExecutorService mNetworkIO = Executors.newScheduledThreadPool(3);

    public ScheduledExecutorService getmNetworkIO(){
        return mNetworkIO;
    }

}
