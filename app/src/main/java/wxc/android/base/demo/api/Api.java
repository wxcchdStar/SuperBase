package wxc.android.base.demo.api;

import java.util.List;

import wxc.android.base.api.ApiResult;
import wxc.android.base.api.RxApiClient;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class Api {

    private static volatile Api sInstance;

    private InternalApi mApi;

    private Api() {
        RxApiClient apiClient = RxApiClient.create("https://api.douban.com/v2/");
        mApi = apiClient.createApi(InternalApi.class);
    }

    public static InternalApi get() {
        if (sInstance == null) {
            synchronized (Api.class) {
                if (sInstance == null) {
                    sInstance = new Api();
                }
            }
        }
        return sInstance.mApi;
    }

    public interface InternalApi {

        @GET("/book/series/{id}/books")
        Observable<ApiResult<List<BookBean>>> getBooks(@Path("id") String id);

    }
}
