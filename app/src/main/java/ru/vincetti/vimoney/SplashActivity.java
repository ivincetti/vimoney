package ru.vincetti.vimoney;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.vincetti.vimoney.data.JsonDownloader;
import ru.vincetti.vimoney.home.HomeActivity;

public class SplashActivity extends AppCompatActivity {
    private final String LOG_TAG = "SPLASH DEBUG";

    private ProgressBar progress;
    private JsonDownloader jsonDownloader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progress = findViewById(R.id.splash_content_progressbar);

        retrofitInit();
        Log.d(LOG_TAG, "Retrogit init OK");
        loadJson();
    }

    private void retrofitInit(){
        Retrofit json = new Retrofit.Builder()
                .baseUrl("https://vincetti.ru/vimoney/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonDownloader = json.create(JsonDownloader.class);
    }

    private void loadJson(){
        jsonDownloader.loadPreferences("Ru").enqueue(new Callback<ru.vincetti.vimoney.data.Response>() {
            @Override
            public void onResponse(Call<ru.vincetti.vimoney.data.Response> call,
                                   Response<ru.vincetti.vimoney.data.Response> response) {
                Log.d(LOG_TAG, response.body().getUser().getName());
                HomeActivity.start(getApplicationContext());

            }

            @Override
            public void onFailure(Call<ru.vincetti.vimoney.data.Response> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, t.getMessage());
            }
        });
    }
}
