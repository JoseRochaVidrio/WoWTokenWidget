package joserocha.wowtokenwidget.Modelos;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.StrictMode;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import joserocha.wowtokenwidget.R;
import joserocha.wowtokenwidget.main;

/**
 * Created by Photodynamics5 on 4/16/2015.
 */
public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        //You can do the processing here update the widget/remote views.
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.activity_main);

        main.savedGold = "";
        String urlString = "https://wowtoken.info/history.json";
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            main.jsonstr = buffer.toString();
        }
        catch (Exception ex){
            Log.d("algo", "InputStream Exc");
        }
        finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        Gson _gsun = new Gson();

        try {
            main.TokenInfo _tokinfo = _gsun.fromJson(main.jsonstr, main.TokenInfo.class);

            //int _sizelist = _tokinfo.NA.size();

            main.savedGold = _tokinfo.NA.get(_tokinfo.NA.size()-1)[1];


        }
        catch (Exception ex){
            Log.d("algo", "Excepcion de json token");
        }

        if(main.savedGold.isEmpty()){

            main.savedGold = "Buy Price:\n 0g";
        }
        else{
            main.savedGold = "Buy Price:\n"+main.savedGold+"g";
        }

        remoteViews.setTextViewText(R.id.txtGold, main.savedGold);
        remoteViews.setImageViewResource(R.id.imageView, R.drawable.coin);


        ComponentName thiswidget = new ComponentName(context, main.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);


        manager.updateAppWidget(thiswidget, remoteViews);
        //Release the lock
        wl.release();
    }
}
