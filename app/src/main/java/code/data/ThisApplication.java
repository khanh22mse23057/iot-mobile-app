package code.data;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.marcinorlowski.fonty.Fonty;


public class ThisApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Fonty.context(this)
                .normalTypeface("dashboard.ttf")
                .italicTypeface("dashboard.ttf")
                .boldTypeface("dashboard.ttf")
                .build();
    }

}
