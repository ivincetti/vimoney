package ru.vincetti.vimoney.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.vincetti.vimoney.models.json.ConfigFile;

public interface JsonDownloader {

    @GET("config.json")
    Call<ConfigFile> loadPreferences(@Query("q") String locale);
}
