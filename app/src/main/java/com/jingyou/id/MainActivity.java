package com.jingyou.id;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private String sex;
    private String birthday;
    private String address;

    private TextView result;
    private EditText editText;
    private Button button;

    private String httpUrl;
    private String httpArg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText= (EditText) findViewById(R.id.edit);
        result= (TextView) findViewById(R.id.result);
        button= (Button) findViewById(R.id.button);

        httpUrl = "http://apis.baidu.com/apistore/idservice/id";

        button.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button){
            httpArg = editText.getText().toString();
            request(httpUrl, httpArg);
        }
    }

    private Handler handler=new Handler(){

        public void handleMessage(Message msg){
            switch(msg.what){
                case 0:
                    String r= (String) msg.obj;

                    try{
                        JSONObject jsonObject=new JSONObject(r);
                        JSONObject weatherInfo=jsonObject.getJSONObject("retData");

                        sex=weatherInfo.getString("sex");
                        birthday=weatherInfo.getString("birthday");
                        address=weatherInfo.getString("address");

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    result.setText(" "+sex+" "+birthday+" "+address);
            }
        }


    };

    private void request(final String httpUrl, final String httpArg) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpURLConnection connection=null;

                try {
                    URL url = new URL(httpUrl + "?id=" + httpArg);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    // 填入apikey到HTTP header
                    connection.setRequestProperty("apikey",  "9683743a3bd1ba305d7874f6bc7b5bcf");
                    connection.connect();
                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String strRead;
                    StringBuilder sbf=new StringBuilder();
                    while ((strRead = reader.readLine()) != null) {
                        sbf.append(strRead);
                    }

                    Message message=new Message();
                    message.what=0;
                    message.obj=sbf.toString();
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(connection!=null){
                        connection.disconnect();
                    }
                }

            }
        }).start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
