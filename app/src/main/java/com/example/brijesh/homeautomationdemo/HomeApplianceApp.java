package com.example.brijesh.homeautomationdemo;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class HomeApplianceApp extends Application {
    private static HomeApplianceApp mInstance = new HomeApplianceApp();
    private static Context mAppContext;


    private String TAG = HomeApplianceApp.class.getSimpleName();
    private String clientId;
    private MqttAndroidClient client;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mAppContext = this.getApplicationContext();

    }

    public MqttAndroidClient getClient(){
        if (clientId == null){
            clientId = MqttClient.generateClientId();
            client = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                    clientId);

            Log.e("checking", clientId);

            try {
                IMqttToken token = client.connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Log.d(TAG, "onSuccess");
                        Toast.makeText(HomeApplianceApp.this, "Connected!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Log.d(TAG, "onFailure");
                        Toast.makeText(HomeApplianceApp.this, "Connection Failed!", Toast.LENGTH_SHORT).show();


                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    public void getMQTTStatus() {

    }

    public static HomeApplianceApp getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
