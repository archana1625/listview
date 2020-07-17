package com.example.list;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class DetailActivity extends AppCompatActivity {

    TextView tvid, tvname, tvemail, tvcontact, tvaddress;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        handleSSLHandshake();
        //Initializing Views
        tvid = findViewById(R.id.txtid);
        tvname = findViewById(R.id.txtname);
        tvemail = findViewById(R.id.txtemail);
        tvcontact = findViewById(R.id.txcontact);
        tvaddress = findViewById(R.id.txtaddress);

        Intent intent = getIntent();
        position = intent.getExtras().getInt("position");

        tvid.setText("ID: " + MainActivity.employeeArrayList.get(position).getId());
        tvname.setText("Name: " + MainActivity.employeeArrayList.get(position).getName());
        tvemail.setText("Email: " + MainActivity.employeeArrayList.get(position).getEmail());
        tvcontact.setText("Contact: " + MainActivity.employeeArrayList.get(position).getContact());
        tvaddress.setText("Address: " + MainActivity.employeeArrayList.get(position).getAddress());


    }

    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }
}

