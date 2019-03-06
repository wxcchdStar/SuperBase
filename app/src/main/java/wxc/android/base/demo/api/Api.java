package wxc.android.base.demo.api;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import wxc.android.base.api.ApiClient;
import wxc.android.base.api.model.ApiResult;
import wxc.android.base.demo.model.MusicRankingBean;
import wxc.android.base.demo.model.MusicRankingDetailBean;

public class Api {

    private static volatile Api sInstance;

    private InternalApi mApi;

    private Api() {
        ApiClient apiClient = ApiClient.create("https://api.apiopen.top/");
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

        @GET("/musicRankings")
        Observable<ApiResult<List<MusicRankingBean>>> getMusicRankings();

        @GET("/musicRankingsDetails")
        Observable<ApiResult<List<MusicRankingDetailBean>>> getMusicRankingDetail(@Query("type") int type);

    }
}
