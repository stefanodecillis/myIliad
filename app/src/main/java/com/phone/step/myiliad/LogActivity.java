package com.phone.step.myiliad;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LogActivity extends AppCompatActivity {

    @BindView(R.id.idTxt)
    EditText idTxt = null;
    @BindView(R.id.pswTxt)
    EditText pswTxt = null;
    @BindView(R.id.logBtn)
    Button logBtn = null;
    @BindView(R.id.infoBtn)
    Button infoBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        ButterKnife.bind(this);

        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCheck();
            }
        });
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog();
            }
        });

    }

    private void showMainPage(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        Log.d("DEBUG", "Go to the main page");
        finish();
    }

    private void doCheck(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                if(idTxt.getText() == null || pswTxt.getText() == null){
                    return;
                }
                if(idTxt.getText().toString().equals("") || pswTxt.getText().toString().equals("")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showMessage(getApplicationContext(), "Completa i campi");
                        }
                    });
                } else {
                    if(IliadService.doLog(idTxt.getText().toString(), pswTxt.getText().toString())){
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = pref.edit();

                        Log.d("DEBUG", "before saving preferences");
                        //save user details
                        String id = idTxt.getText().toString();
                        String psw = pswTxt.getText().toString();
                        editor.putString(Constants.ID_KEY, id);
                        editor.putString(Constants.PSW_KEY, psw);
                        editor.commit();

                        Log.d("DEBUG", "Saved preferences");
                        UserHandler.getInstance().setStatus(true);
                        showMainPage();
                    } else {
                        //todo show error
                        Log.d("ERROR", "do log failed");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showMessage(getApplicationContext(), "Sembra che le tue credenziali siano errate, riprova per favore");
                                idTxt.setText("");
                                pswTxt.setText("");
                            }
                        });
                    }
                }
            }
        };
        thread.start();
    }

    private void alertDialog() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage(Constants.INFO_LOGIN);
        dialog.setTitle("Credenziali di accesso");
        dialog.setPositiveButton("Ho capito",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Log.d("DEBUG", "undestood button is clicked on the log page");
                    }
                });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }
}
