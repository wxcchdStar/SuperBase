package wxc.android.base.api;

import java.util.concurrent.TimeUnit;

import wxc.android.base.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private String mBaseUrl;
    private Retrofit mRetrofit;

    private ApiClient(String baseUrl) {
        mBaseUrl = baseUrl;
        mRetrofit = createBuilder().build();
    }

    public static ApiClient create(String baseUrl) {
        if (baseUrl == null) {
            throw new IllegalArgumentException("The baseUrl must be not NULL!");
        }
        return new ApiClient(baseUrl);
    }

    private Retrofit.Builder createBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.retryOnConnectionFailure(true);
        builder.connectTimeout(15, TimeUnit.SECONDS); // 默认10s

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
            logger.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logger);
        }

        return new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());
    }

    public <T> T createApi(Class<T> clazz) {
        return mRetrofit.create(clazz);
    }

}
