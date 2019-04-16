package com.phone.step.myiliad;

import android.util.Log;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IliadService {

    public static String getIdSession(String id, String psw) throws IOException {

        Log.d("DEBUG", "Iliad called");
        Connection connection = Jsoup.connect(Constants.iliadWeb)
                .method(Connection.Method.POST)
                .requestBody(Constants.payload.replaceAll("%%", id).replaceAll("££",psw));
        Connection.Response response = connection.execute();
        if(response != null) {
            String sessid = response.cookies().get("ACCOUNT_SESSID");
            Log.d("DEBUG", sessid);
            return sessid;
        } else {
            Log.d("DEBUG", "response is null");
        }

        return "success";
    }

    public static Map<String,String> getData(String sessId) throws IOException {
        Connection.Response response = Jsoup.connect(Constants.iliadWeb)
                .method(Connection.Method.GET)
                .followRedirects(true)
                .header("Cookie", Constants.cookieSession.replaceAll("YYY", sessId))
                .execute();
        Document doc = response.parse();
        Elements elements = doc.body().getElementsByClass("conso-infos conso-local");
        Element dataContainer = elements.get(0);
        elements = dataContainer.getElementsByClass("conso__text");
        Map<String,String> data = getInternetData(elements);
        data.putAll(getVoiceMessages(elements));
        data.putAll(getCredit(doc));

        return data;
    }

    private static Map<String,String> getCredit(Document doc){
        Elements elements = doc.body().getElementsByClass("page p-conso");
        elements = elements.get(0).getAllElements();
        Log.d("SIM", "Credit: " + elements.get(3).text());
        Map<String,String> data = new HashMap<>();
        data.put("credit", elements.get(3).text());
        return data;
    }

    /**
     *
     * @param elements
     * @return
     *
     * using the html page to retrieve data information throught the div element.
     */
    private static Map<String,String>  getInternetData(Elements elements){

        Element internetDiv = elements.get(2);
        elements = internetDiv.getAllElements();

        String dataUsage,dataMax,dataCost = null;

        String[] infos = elements.get(0).getAllElements().get(0).text().split(" ");
        List<String> internetInfo = new ArrayList<>();
        internetInfo.add(infos[0]); //usage
        internetInfo.add(infos[2]); //max
        internetInfo.add(infos[5]); //cost

        dataUsage = internetInfo.get(0);
        dataMax = internetInfo.get(1);
        dataCost = internetInfo.get(2);

        Map<String,String> data = new HashMap<>();
        data.put("dataUsage", dataUsage);
        data.put("dataMax", dataMax);
        data.put("dataCost", dataCost);

        Log.d("DEBUG", "usage: " + dataUsage);
        Log.d("DEBUG", "max: " + dataMax);

        return data;
    }

    private static Map<String,String> getVoiceMessages(Elements elements) {

        //use it to check if the div is the right one
        /*for (Element element : elements){
            Log.d("NEW", element.text());
        }*/

        /* minutes */
        String[] info = elements.get(0).text().split(" ");

        Map<String, String> data = new HashMap<>();
        data.put("voiceMin", info[1]);
        data.put("voiceCost", info[4]);

        /* sms */
        info = elements.get(1).text().split(" ");
        data.put("smsNum", info[0]+" "+info[1]);
        data.put("smsCost", info[4]);

        /* mms */
        info = elements.get(3).text().split(" ");
        data.put("mmsNum", info[0]+ " " + info[1]);
        data.put("mmsCost", info[4]);

        for(String key : data.keySet()){
            Log.d("DEBUG", data.get(key));
        }

        return data;
    }

    public static boolean doLog(String id, String psw) throws IOException {

        UserHandler.getInstance().setId(id);
        UserHandler.getInstance().setPsw(psw);
        Thread thread = new Thread(){
            @Override
            public void run() {
                Log.d("DEBUG", "Trying to access..");
                Connection connection = Jsoup.connect(Constants.iliadWeb)
                        .method(Connection.Method.POST)
                        .requestBody(Constants.payload.replaceAll("%%", UserHandler.getInstance().getId()).replaceAll("££",UserHandler.getInstance().getPsw()));
                Document doc = null;
                try {
                    doc = connection.execute().parse();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("DEBUG","id body: " + doc.body().id());

                if(doc.body().id().equals("account-conso")||doc.body().id().equals("account-autologin")){
                    Log.d("DEBUG", "logged in");
                    UserHandler.getInstance().setStatus(true);
                } else {
                    Log.d("DEBUG", "didn't log in");
                    UserHandler.getInstance().setStatus(false);
                }

            }
        };
        thread.start();
        while(thread.isAlive()){
            android.os.SystemClock.sleep(10);
        }

        return UserHandler.getInstance().getStatus();
    }

    public static boolean checkIliadService() throws IOException, InterruptedException {

        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(Constants.CURL_SITE); //<- Try ping -c 1 www.serverURL.com
        proc.waitFor();
        Log.d("NEW", "Accessibility iliad is:  " + proc.exitValue());
        if(proc.exitValue() == 2)
            return true;
        else
            return false;
    }
}
