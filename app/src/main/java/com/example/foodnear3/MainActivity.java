package com.example.foodnear3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

    public final int REQUEST_CODE = 100;
    public final int PERMISSION_CODE = 200;

    String street_name;
    ArrayList<Place> places;
    final String sv = "http://27.2.253.214";
//    final String sv = "http://27.2.255.76";
    final String port = ":5000";
    PlaceAdapter placeAdapter;

    ListView lv_place;
    View view;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start();
//        if (places.size() > 0) {
//            UpdateLV();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                if (data != null) {
                    int qrcode = data.getIntExtra("street_code", -1);
                    if (qrcode != -1) {
                        resetAfterScan();
                        getStreetName(qrcode);
                        getPlaceByCode(qrcode);
                    }
                }
            }
        } else {
            displayErrMes();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_act_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.scan_qr) {
            scanQRCode();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getStreetName(int code) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String path = sv + port + "/street_name/" + code;
//        Log.d("test", path);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, path, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    street_name = response;
                    toolbar.setTitle("Quán ăn đường " + street_name);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    error.printStackTrace();
                    displayErrMes();
                }
            }
        });
        requestQueue.add(stringRequest);
    }

    private void hideErrMes() {
        lv_place.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
    }

    private void displayErrMes() {
        toolbar.setTitle(R.string.app_name);
        lv_place.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }

    private void UpdateLV() {
        hideErrMes();
        placeAdapter.notifyDataSetChanged();
        lv_place.setAdapter(placeAdapter);
    }

    private void resetAfterScan() {
        places.clear();
        street_name = "";
    }

    private void getPlaceByCode(int code) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String path = sv + port + "/place/" + code;
//        Log.d("test", path);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, path, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length() > 0) {
                    String[] data = response.split("//");
                    for (String item : data) {
                        String[] t = item.split(",");
                        String cps = t[0];
                        cps = cps.substring(1);
                        String cs = t[1];
                        cs = cs.substring(1);
                        String ns = t[2];
                        ns = ns.substring(2, ns.length() - 1);
                        String ils = t[3];
                        ils = ils.substring(2, ils.length() - 1);
                        String as = t[4];
                        as = as.substring(2, as.length() - 2);
                        places.add(new Place(Integer.parseInt(cps), Integer.parseInt(cs), ns, sv + "/server/images/" + ils, as));
                    }
                    places.get(places.size() - 1).setAddress(places.get(places.size() - 1).getAddress().substring(0, places.get(places.size() - 1).getAddress().length() - 1));
                    UpdateLV();
                } else {
                    displayErrMes();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                displayErrMes();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void scanQRCode() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Yêu cầu quyền truy cập camera", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, PERMISSION_CODE);
        } else {
            Intent i = new Intent(getApplication(), ScanQRAct.class);
            startActivityForResult(i, REQUEST_CODE);
        }
    }

    private void start() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, PERMISSION_CODE);
        }

        places = new ArrayList<>();
        placeAdapter = new PlaceAdapter(places, getApplicationContext());

        lv_place = (ListView) findViewById(R.id.lv_place);
        view = findViewById(R.id.err_messg);

    }
}
