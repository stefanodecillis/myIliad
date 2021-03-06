package com.phone.step.dataMeter;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.dataProg)
    ProgressBar dataProgress = null;
    @BindView(R.id.dataString)
    TextView dataString = null;
    @BindView(R.id.dataMaxString)
    TextView dataMaxString = null;
    @BindView(R.id.callProgress)
    ProgressBar callProgress = null;
    @BindView(R.id.callString)
    TextView callString = null;
    @BindView(R.id.smsProgress)
    ProgressBar smsProgress = null;
    @BindView(R.id.smsString)
    TextView smsString = null;
    @BindView(R.id.mmsProgress)
    ProgressBar mmsProgress = null;
    @BindView(R.id.mmsString)
    TextView mmsString = null;
    @BindView(R.id.creditTxt)
    TextView creditString = null;
    @BindView(R.id.renewalTxt)
    TextView renewalString = null;

    @BindView(R.id.resetBtn)
    Button resetBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        boolean status = true;
        try {
            if(IliadService.checkIliadService()){
                Log.d("DEBUG", "website is reachable");
            } else {
                creditString.setText("Service is not available. Try later");
                creditString.setTextSize(10);
                creditString.setTextColor(Color.RED);
                status = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //SharedPreferences.Editor editor = pref.edit();

        if(pref.getString(Constants.ID_KEY, null) != null){
            Log.d("DEBUG", "userID: " + pref.getString(Constants.ID_KEY, null));
        }

        if(pref.getString(Constants.ID_KEY, null) == null ||pref.getString(Constants.PSW_KEY, null) == null){
            showLogin();
        } else {
            if(status) {
                try {
                    runIliad();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePref();
            }
        });

    }

    private void runIliad() throws IOException {
        Thread thread = new Thread(){
            @Override
            public void run() {
              try {
                  SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                  String sessId = IliadService.getIdSession(pref.getString(Constants.ID_KEY,null),pref.getString(Constants.PSW_KEY,null));
                  Log.d("INFO", Constants.cookieSession.replaceAll("YYY", sessId));
                  Map<String,String> data = IliadService.getData(sessId);
                  UserHandler.getInstance().setUserData(data);
              } catch (IOException e){
                  e.printStackTrace();
              }
            }

        };

        thread.start();
        while(thread.isAlive() && UserHandler.getInstance().getUserData() == null){
            android.os.SystemClock.sleep(5);
        }
        if(UserHandler.getInstance().getUserData() == null){
            Log.d("DEBUG", "user data is null");
            creditString.setText("Service is not available");
            creditString.setTextSize(15);
            creditString.setTextColor(Color.RED);
        } else
        initUI();
    }

    private void initUI () throws IOException {
        Map<String,String> data = UserHandler.getInstance().getUserData();
        String dataUsage = data.get("dataUsage");
        String dataMax = data.get("dataMax");
        if(dataString == null)
        {
            ButterKnife.bind(this);
        }
        dataString.setText(dataUsage);
        creditString.setText(Constants.CREDIT_TXT.replace("X", data.get("credit")));
        dataMaxString.setText(dataMax);
        Double usage = Utils.dataConvert(dataUsage);
        Double max = Utils.dataConvert(dataMax);
        Double rest = null;
        if(usage == -1 || max == -1){
            Utils.showMessage(getApplicationContext(), "Per favore, riporta questo bug allo sviluppatore");
            rest = Double.valueOf(100);
        } else
            rest = (usage/max)*100;
        Log.d("INFO", "usage: "+ usage + " max: " + max + " rest: " + rest);
        int dataPerc = 100 -  rest.intValue();
        Log.d("INFO", String.valueOf(dataPerc*100));
        dataProgress.setProgress(dataPerc);
        String voiceMin = data.get("voiceMin");

        /**
         * i'm going to set progress to 100% because I know how to scrap data just from unlimited plan. Need to fix this with some help
         */

        //call
        callProgress.setProgress(100);
        callString.setText(voiceMin);

        //sms
        smsProgress.setProgress(100);
        smsString.setText(data.get("smsNum"));

        //mms
        mmsProgress.setProgress(100);
        mmsString.setText(data.get("mmsNum"));

        //renewal
        if(data.get("renewal") != null)
            renewalString.setText(data.get("renewal"));
        else
            renewalString.setText("");

    }

    private void showLogin(){
        Intent intent = new Intent(this,LogActivity.class);
        Log.d("DEBUG", "Log required");
        startActivity(intent);
        finish();
    }

    private void removePref(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();

        editor.remove(Constants.ID_KEY).commit();
        editor.remove(Constants.PSW_KEY).commit();
        editor.commit();

        Utils.showMessage(this, "Bye");

        showLogin();
    }

}
