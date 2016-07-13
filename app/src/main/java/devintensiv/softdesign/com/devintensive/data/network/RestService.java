package devintensiv.softdesign.com.devintensive.data.network;

import devintensiv.softdesign.com.devintensive.data.network.req.UserLoginReq;
import devintensiv.softdesign.com.devintensive.data.network.res.UserModelRes;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RestService {

/*    @Headers({
            "Custom-Header : me header Value"
    })*/
    @POST("login")
    Call<UserModelRes> loginUser (@Body UserLoginReq req);

}
