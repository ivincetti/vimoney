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
import ru.vincetti.vimoney.data.ConfigFile;
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
        loadJson();
    }

    private void retrofitInit() {
        Retrofit json = new Retrofit.Builder()
                .baseUrl("https://vincetti.ru/vimoney/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonDownloader = json.create(JsonDownloader.class);
    }

    private void loadJson() {
        jsonDownloader.loadPreferences("Ru").enqueue(new Callback<ConfigFile>() {
            @Override
            public void onResponse(Call<ConfigFile> call, Response<ConfigFile> response) {
                HomeActivity.start(getApplicationContext());
            }

            @Override
            public void onFailure(Call<ConfigFile> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, t.getMessage());
            }
        });
    }
}
