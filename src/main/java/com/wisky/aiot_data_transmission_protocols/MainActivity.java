package com.wisky.aiot_data_transmission_protocols;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAckReturnCode;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String MQTT_BROKER_HOST = "test.mosquitto.org";  // 使用公共 MQTT Broker
    private static final int MQTT_BROKER_PORT = 1883;
    private static final String MQTT_TOPIC = "testTopic";

    private TextView responseTextView;
    private Mqtt3AsyncClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseTextView = findViewById(R.id.responseTextView);

        // 初始化 MQTT 客戶端
        client = MqttClient.builder()
                .useMqttVersion3()
                .serverHost(MQTT_BROKER_HOST)
                .serverPort(MQTT_BROKER_PORT)
                .buildAsync();

        connectToMqttBroker();
    }

    private void connectToMqttBroker() {
        client.connectWith()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "Failed to connect to MQTT broker", throwable);
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "MQTT 連接失敗", Toast.LENGTH_SHORT).show());
                    } else if (connAck.getReturnCode() == Mqtt3ConnAckReturnCode.SUCCESS) {
                        Log.i(TAG, "Connected to MQTT broker");
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "MQTT 連接成功", Toast.LENGTH_SHORT).show());
                        subscribeToTopic();  // 在成功連接後訂閱主題
                    } else {
                        Log.e(TAG, "Failed to connect to MQTT broker: " + connAck.getReturnCode());
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "MQTT 連接失敗: " + connAck.getReturnCode(), Toast.LENGTH_SHORT).show());
                    }
                });
    }

    private void subscribeToTopic() {
        client.subscribeWith()
                .topicFilter(MQTT_TOPIC)
                .callback(publish -> {
                    String message = new String(publish.getPayloadAsBytes());
                    Log.i(TAG, "Received MQTT message: " + message);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "接收到消息: " + message, Toast.LENGTH_SHORT).show();
                        responseTextView.setText("接收到消息: " + message);  // 將接收到的消息顯示在 TextView 上
                    });
                })
                .send();
    }
}
