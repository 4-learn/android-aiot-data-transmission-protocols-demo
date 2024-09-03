package com.wisky.aiot_data_transmission_protocols;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // 提取 API 地址為變數
    private static final String API_URL = "http://140.127.196.91:8085/api/post";

    // 定義靜態的 JWT token
    private static final String STATIC_JWT = "C123";

    private TextView responseTextView;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseTextView = findViewById(R.id.responseTextView);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendPostRequestTask().execute();
            }
        });
    }

    private class SendPostRequestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                // 創建 URL 對象
                URL url = new URL(API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                // 設置 header
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Authorization", "Bearer " + STATIC_JWT);
                conn.setDoOutput(true);

                // 要發送的 JSON 數據
                String jsonInputString = "{\"param1\":\"value1\", \"param2\":\"value2\"}";

                // 將 JSON 數據寫入請求體中
                try (OutputStream os = conn.getOutputStream()) {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonInputString);
                    writer.flush();
                    writer.close();
                    os.close();
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }

            } catch (Exception e) {
                Log.e("HTTP Request Error", e.getMessage(), e);
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            responseTextView.setText(result);
        }
    }

    // 生成 Authorization token 的方法已經不再使用，因為我們使用靜態的 JWT
    // private String generateAuthorizationToken() {
    //     return UUID.randomUUID().toString();
    // }
}
