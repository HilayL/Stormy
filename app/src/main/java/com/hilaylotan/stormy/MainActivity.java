package com.hilaylotan.stormy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hilaylotan.stormy.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static final String TAG=MainActivity.class.getSimpleName();

    private CurrentWeather currentWeather;

    private ImageView iconImageView;

    String latitude;
    String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        latitude = "37.8267";
        longitude = "-122.4233";

        getForcast(latitude,longitude);
        Log.d(TAG,"working");
    }

    private void getForcast(String latitude, String longitude) {
        final ActivityMainBinding binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        TextView darkSky = findViewById(R.id.darkSkyAttribution);

        darkSky.setMovementMethod(LinkMovementMethod.getInstance());

        String apiKey = "2b80efd6148fc04385744ab157c7c453";

        iconImageView = findViewById(R.id.iconImageView);

        String forcastURL = "https://api.darksky.net/forecast/"+apiKey+"/"+latitude+","+longitude;

        if (isNetwotkAvailable()) {

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(forcastURL).build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e(TAG, "shit", e);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        String jsonData=response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            currentWeather = getCurrentDetails(jsonData);

                            final CurrentWeather displayWeather = new CurrentWeather(
                                    currentWeather.getLocationLabel(),
                                    currentWeather.getIcon(),
                                    currentWeather.getTime(),
                                    currentWeather.getTemperature(),
                                    currentWeather.getHumidity(),
                                    currentWeather.getPrecipChance(),
                                    currentWeather.getSummary(),
                                    currentWeather.getTimeZone()
                            );

                            binding.setWeather(displayWeather);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Drawable drawable = getResources().getDrawable(displayWeather.getIconId());
                                    iconImageView.setImageDrawable(drawable);
                                }
                            });
                        } else {
                            aletUserAboutError();
                        }

                    } catch (IOException e) {
                        Log.e(TAG, "IO exeption caught", e);
                    }
                    catch (JSONException e){
                        Log.e(TAG,"Here comes JSON",e);
                    }
                }
            });
        }
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forcast = new JSONObject(jsonData);

        String timezone = forcast.getString("timezone");
        Log.i(TAG, R.string.from_json + timezone);

        JSONObject currently = forcast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();

        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setLocationLabel("Alcatraz Island, CA");
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);

        Log.i(TAG,currentWeather.getFormattedTime());

        return currentWeather;
    }

    private boolean isNetwotkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable=false;

        if (networkInfo!=null && networkInfo.isConnected())
            isAvailable=true;
        else
            Toast.makeText(this, R.string.network_unavailable_message,Toast.LENGTH_LONG).show();
        return isAvailable;
    }

    private void aletUserAboutError()
    {
        AlertDialogFragment dialog=new AlertDialogFragment();
        dialog.show(getFragmentManager(),"error_dialog");
    }
    public void refreshOnClick (View view)
    {
        getForcast(latitude,longitude);

    }
}
