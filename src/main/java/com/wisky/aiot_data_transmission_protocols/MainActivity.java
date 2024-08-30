package com.wisky.aiot_data_transmission_protocols;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String WEBSOCKET_URL = "ws://140.127.196.91:8085/ws";
    private TextView responseTextView;
    private Button sendButton;
    private WebSocket webSocket;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseTextView = findViewById(R.id.responseTextView);
        sendButton = findViewById(R.id.sendButton);

        // 初始化 WebSocket 連接
        initiateWebSocketConnection();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webSocket != null) {
                    String message = "{\"param1\":\"value1\", \"param2\":\"value2\"}";
                    webSocket.send(message);
                    Log.d("WebSocket", "Sent: " + message);
                } else {
                    Log.e("WebSocket", "WebSocket is not connected.");
                }
            }
        });
    }

    private void initiateWebSocketConnection() {
        client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(WEBSOCKET_URL)
                .addHeader("Authorization", "Bearer " + generateAuthorizationToken())
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                runOnUiThread(() -> responseTextView.setText("Connected to WebSocket server."));
                Log.d("WebSocket", "Connected to WebSocket server.");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                runOnUiThread(() -> responseTextView.append("\nReceived: " + text));
                Log.d("WebSocket", "Received: " + text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                runOnUiThread(() -> responseTextView.append("\nReceived bytes: " + bytes.hex()));
                Log.d("WebSocket", "Received bytes: " + bytes.hex());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                runOnUiThread(() -> responseTextView.append("\nClosing: " + reason));
                Log.d("WebSocket", "Closing: " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                runOnUiThread(() -> responseTextView.append("\nError: " + t.getMessage()));
                Log.e("WebSocket", "Error: ", t);
            }
        });
    }

    // 生成 Authorization token 的方法
    private String generateAuthorizationToken() {
        return UUID.randomUUID().toString();  // 這裡用 UUID 作為簡單的 Authorization token 生成器
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Activity destroyed");
        }
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
    }
}
