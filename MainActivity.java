package com.example.list;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    MyAdapter adapter;
    public static ArrayList<Employee> employeeArrayList = new ArrayList<>();
    String url = "https://192.168.42.75/retrieve.php";
    Employee employee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleSSLHandshake();
        listView = findViewById(R.id.myListView);
        adapter = new MyAdapter(this, employeeArrayList);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                ProgressDialog progressDialog = new ProgressDialog(view.getContext());

                CharSequence[] dialogItem = {"View Data", "Edit Data", "Delete Data"};
                builder.setTitle(employeeArrayList.get(position).getName());
                builder.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        switch (i) {

                            case 0:

                                startActivity(new Intent(getApplicationContext(), DetailActivity.class)
                                        .putExtra("position", position));

                                break;

                            case 1:
                                startActivity(new Intent(getApplicationContext(), Edit_Activity.class)
                                        .putExtra("position", position));

                                break;

                            case 2:

                                deleteData(employeeArrayList.get(position).getId());

                                break;


                        }


                    }
                });


                builder.create().show();


            }
        });

        retrieveData();


    }

    private void deleteData(final String id) {

        StringRequest request = new StringRequest(Request.Method.POST, "https://192.168.42.75/delete.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.equalsIgnoreCase("Data Deleted")) {
                            Toast.makeText(MainActivity.this, "Data Deleted Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Data Not Deleted", Toast.LENGTH_SHORT).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }

    public void retrieveData() {

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        employeeArrayList.clear();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String sucess = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            if (sucess.equals("1")) {


                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    String email = object.getString("email");
                                    String contact = object.getString("contact");
                                    String address = object.getString("address");

                                    employee = new Employee(id, name, email, contact, address);
                                    employeeArrayList.add(employee);
                                    adapter.notifyDataSetChanged();


                                }


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }

    public void btn_add_activity(View view) {
        startActivity(new Intent(getApplicationContext(), Add_Data_Activity.class));
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
