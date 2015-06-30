package joserocha.wowtokenwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import joserocha.wowtokenwidget.Modelos.AlarmManagerBroadcastReceiver;


public class main extends AppWidgetProvider {


    public static String jsonstr;
    public static String savedGold;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        Log.d("algo", "no pinche entra3");

        for(int i=0;i<appWidgetIds.length;i++){
            int appWidgetId = appWidgetIds[i];

            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wowtoken.info"));
            Intent intent = new Intent(context, main.class);

            PendingIntent pending = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_main);

            views.setOnClickPendingIntent(R.id.txtGold, pending);

            savedGold = "";

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

                jsonstr = buffer.toString();
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
               TokenInfo _tokinfo = _gsun.fromJson(jsonstr, TokenInfo.class);

               //int _sizelist = _tokinfo.NA.size();

               savedGold = _tokinfo.NA.get(_tokinfo.NA.size()-1)[1];


           }
           catch (Exception ex){
                Log.d("algo", "Excepcion de json token");
            }

            if(savedGold.isEmpty()){

                savedGold = "Buy Price:\n 0g";
            }
            else{
                savedGold = "Buy Price:\n"+savedGold+"g";
            }

            views.setTextViewText(R.id.txtGold, savedGold);
            views.setImageViewResource(R.id.imageView, R.drawable.coin);

            appWidgetManager.updateAppWidget(appWidgetId, views);



        }
    }

    @Override
    public void onDisabled(Context context) {

        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        super.onDisabled(context);
    }


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 3 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ 1000 * 3, 1800000, pi);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context,
                                          AppWidgetManager appWidgetManager, int appWidgetId,
                                          Bundle newOptions) {
        //Do some operation here, once you see that the widget has change its size or position.

    }


    public static class TokenInfo
    {
        public List<String[]> NA;

        public List<String[]> EU;

        public void setNA(List<String[]> NA) {
            this.NA = NA;
        }
        public void setEU(List<String[]> EU){
            this.EU = EU;
        }



    }

}


