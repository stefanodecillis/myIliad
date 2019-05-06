package com.phone.step.myiliad;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.phone.step.myiliad.Entity.Report;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class Utils {

    public static void showMessage(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void sendDebug(String body, String detail) throws IOException {
        Log.d("DEBUG", "Reporting error");
        Gson gson = new Gson();
        Report report = new Report(body, detail);
        String msg = gson.toJson(report);
        Connection connection = Jsoup.connect(Constants.reportAddress)
                .method(Connection.Method.POST)
                .requestBody(msg);
        connection.execute();
    }

    public static  Double dataConvert(String data) throws IOException {
        String numStr = data.replaceAll("GB", "")
                .replaceAll("MB","")
                .replace("KB","")
                .replace("B","")
                .replace("kb","")
                .replace("b","")
                .replaceAll(",",".");
        Double num = null;
        try {
            num = Double.valueOf(numStr);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        if (num == null){
            sendDebug(data,"dataConverter Exception");
            throw new NumberFormatException();
        }
        if(data.contains("GB")){
            num= num*1000000000;
        } else if (data.contains("MB")){
            num= num*1000000;
        } else if (data.contains("KB") || data.contains("kb")){
            num= num*1000;
        } else if (data.contains("b")||data.contains("B")){
            //nothing
        } else {
            Log.d("ERROR", "wtf  --> " + data);
        }

        return num;
    }
}
