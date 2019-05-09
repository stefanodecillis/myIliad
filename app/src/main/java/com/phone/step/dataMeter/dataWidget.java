package com.phone.step.dataMeter;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.Map;

/**
 * Implementation of App Widget functionality.
 */
public class dataWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) throws IOException {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.data_widget);

        //update
        Map<String,String> data = UserHandler.getInstance().getUserData();
        String dataUsage = data.get("dataUsage");
        String dataMax = data.get("dataMax");
        views.setProgressBar(R.id.dataProg,100, 100,true);
        Double usage = Utils.dataConvert(dataUsage);
        Double max = Utils.dataConvert(dataMax);
        Double rest = (usage/max)*100;
        int dataPerc = 100 -  rest.intValue();
        long remain = max.longValue() - usage.longValue();
        String dataRemain = String.valueOf(remain);
        if (dataRemain.length() >= 9)
        {
            dataRemain = dataRemain.substring(0, dataRemain.length()-9);
            dataRemain+="GB";
        } else if (dataRemain.length() < 9 && dataRemain.length()>=6){
            dataRemain = dataRemain.substring(0, dataRemain.length()-6);
            dataRemain+="MB";
        } else if (dataRemain.length() < 6 && dataRemain.length()>=3){
            dataRemain = dataRemain.substring(0, dataRemain.length()-3);
            dataRemain+="KB";
        }  else if (dataRemain.length() < 3 ){
            dataRemain+="B";
        }
        views.setTextViewText(R.id.dataRemainTxt, String.valueOf(dataRemain));
        views.setProgressBar(R.id.dataProg,100, dataPerc,false);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            try {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}

