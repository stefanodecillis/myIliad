package com.phone.step.myiliad;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.Map;

/**
 * Implementation of App Widget functionality.
 */
public class CallWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.call_widget);
        //update
        Map<String,String> data = UserHandler.getInstance().getUserData();
        String voiceMin = data.get("voiceMin");
        views.setTextViewText(R.id.callTxt, String.valueOf(voiceMin));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        runIliad(context);

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void runIliad(final Context context){
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                    String sessId = IliadService.getIdSession(pref.getString(Constants.ID_KEY,null),pref.getString(Constants.PSW_KEY,null));
                    Log.d("INFO_WIDGET", Constants.cookieSession.replaceAll("YYY", sessId));
                    Map<String,String> data = IliadService.getData(sessId);
                    UserHandler.getInstance().setUserData(data);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

        };
        thread.start();
    }
}

