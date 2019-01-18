package co.tton.android.base.api;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import co.tton.android.base.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RxApiClient {

    private String mBaseUrl;
    private Retrofit mRetrofit;

    private RxApiClient(String baseUrl) {
        mBaseUrl = baseUrl;
        mRetrofit = createBuilder().build();
    }

    public static RxApiClient create(String baseUrl) {
        if (baseUrl == null) {
            throw new IllegalArgumentException("The baseUrl must be not NULL!");
        }
        return new RxApiClient(baseUrl);
    }

    private Retrofit.Builder createBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.retryOnConnectionFailure(true);
        builder.connectTimeout(15, TimeUnit.SECONDS); // 默认10s
//        setHttps(builder);

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

    private void setHttps(OkHttpClient.Builder builder) {
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());

            HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            builder.sslSocketFactory(sslContext.getSocketFactory());
            builder.hostnameVerifier(DO_NOT_VERIFY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T createApi(Class<T> clazz) {
        return mRetrofit.create(clazz);
    }

}
