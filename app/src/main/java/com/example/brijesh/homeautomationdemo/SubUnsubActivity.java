package com.example.brijesh.homeautomationdemo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.brijesh.homeautomationdemo.databinding.ActivitySubUnsubBinding;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

public class SubUnsubActivity extends AppCompatActivity {

    private ActivitySubUnsubBinding binding;
    private int qos = 1;
    private MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sub_unsub);

        client = HomeApplianceApp.getInstance().getClient();


        binding.tvSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (client.isConnected()) {
                    subscribe();
                } else {
                    Toast.makeText(SubUnsubActivity.this, "Oops! No client is connected.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.tvUnsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (client.isConnected()) {
                    unSubscribe();
                } else {
                    Toast.makeText(SubUnsubActivity.this, "Oops! No client is connected.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.tvDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SubUnsubActivity.this, DashboardActivity.class));
            }
        });

    }

    private void subscribe() {
        try {
            IMqttToken subToken = client.subscribe(Constants.TOPIC, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                    Log.e("checking", "subscribe sucesss");
                    Toast.makeText(SubUnsubActivity.this, "Subscribed successfully!", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.e("checking", "subscribe failed");
                    Toast.makeText(SubUnsubActivity.this, "Subscribed Failed, Please Try Again.", Toast.LENGTH_SHORT).show();


                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void unSubscribe() {
        try {
            IMqttToken unsubToken = client.unsubscribe(Constants.TOPIC);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                    Toast.makeText(SubUnsubActivity.this, "Unsubscribed successfully!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
                    Toast.makeText(SubUnsubActivity.this, "You don't have subscription, Please subscribe first!", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
