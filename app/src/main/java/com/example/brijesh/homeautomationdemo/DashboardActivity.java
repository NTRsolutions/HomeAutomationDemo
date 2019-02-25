package com.example.brijesh.homeautomationdemo;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brijesh.homeautomationdemo.databinding.ActivityDashboardBinding;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;


public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private MqttAndroidClient client;
    private RotateAnimation r;

    private Vibrator vibrator;
    private Ringtone myRingtone;

    private AlertDialog dialog;

    public int value = 255;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        myRingtone = RingtoneManager.getRingtone(HomeApplianceApp.getAppContext(), uri);

        client = HomeApplianceApp.getInstance().getClient();


        if (SharedPrefHelper.getPref(SharedPrefHelper.FAB_POSITION_X, null) != null) {
            binding.ivFanOffOn.setX(Float.parseFloat(SharedPrefHelper.getPref(SharedPrefHelper.FAB_POSITION_X, null)));
            binding.ivFanOffOn.setY(Float.parseFloat(SharedPrefHelper.getPref(SharedPrefHelper.FAB_POSITION_Y, null)));
        }


        if (SharedPrefHelper.getPref(SharedPrefHelper.DIMMER, null) != null) {
            int temp_val = Integer.parseInt(SharedPrefHelper.getPref(SharedPrefHelper.DIMMER, null));
            if (temp_val != 255) {
                value = Integer.parseInt(SharedPrefHelper.getPref(SharedPrefHelper.DIMMER, null));
                binding.ivFanOffOn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            } else {
                value = Integer.parseInt(SharedPrefHelper.getPref(SharedPrefHelper.DIMMER, null));
                binding.ivFanOffOn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
            }

        } else {
            value = 255;
            binding.ivFanOffOn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
        }


        binding.ivFanOffOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                View v = LayoutInflater.from(DashboardActivity.this).inflate(R.layout.dialog_dimmer, null);
                AppCompatSeekBar dimmer = v.findViewById(R.id.rs_dimmer);
                TextView progress_tv = v.findViewById(R.id.tv_progress);
                progress_tv.setText(value + "");
                dimmer.setProgress((256 - value));
                TextView close = v.findViewById(R.id.tv_close);
                TextView set = v.findViewById(R.id.tv_set);

                dimmer.setOnSeekBarChangeListener(
                        new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress,
                                                          boolean fromUser) {

                                value = (256 - progress);
                                progress_tv.setText(value + "");

                            }
                        }
                );

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                set.setOnClickListener(view12 -> {
                    // calling api
                    Log.e("checking value", "" + value);


                    JSONObject msgObject = new JSONObject();

                    try {
                        if (value == 255)
                            msgObject.put("V", 0);
                        else
                            msgObject.put("V", 1);

                        msgObject.put("D", value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    String msg = msgObject.toString();
                    // MqttMessage message = new MqttMessage(msg,);
                    try {
                        client.publish(Constants.TOPIC, msg.getBytes(), 0, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                    Log.e("position", "" + binding.ivFanOffOn.getX() + " " + binding.ivFanOffOn.getY());

                    SharedPrefHelper.putPref(SharedPrefHelper.DIMMER, String.valueOf(value));
                    SharedPrefHelper.putPref(SharedPrefHelper.FAB_POSITION_X, String.valueOf(binding.ivFanOffOn.getX()));
                    SharedPrefHelper.putPref(SharedPrefHelper.FAB_POSITION_Y, String.valueOf(binding.ivFanOffOn.getY()));

                    dialog.dismiss();


                });
                dialog = new AlertDialog.Builder(DashboardActivity.this)
                        .setView(v)
                        .create();
                dialog.show();

            }
        });


        if (client != null && client.isConnected()) {

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e("checking", "message: " + "connection lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String received_msg = new String(message.getPayload());

                    JSONObject obj = new JSONObject(received_msg.toString());
                    int val = Integer.parseInt(obj.get("V").toString());
                    int range = Integer.parseInt(obj.get("D").toString());
                    Log.e("checking", "message: " + received_msg);
                    Log.e("checking", "value: " + val);

                    vibrator.vibrate(500);
                    myRingtone.play();

                    if (val == 0) {
                        binding.ivFanOffOn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                        Toast.makeText(DashboardActivity.this, "Fan Off", Toast.LENGTH_SHORT).show();


                    } else {
                        binding.ivFanOffOn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                        Toast.makeText(DashboardActivity.this, "Fan On " + range, Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.e("checking", "message: ");

                }
            });

        }
    }

}
