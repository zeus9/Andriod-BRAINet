package cse535.brainet;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ClassifierService {
    @Headers("Content-Type: text/plain")
    @POST("/")
    Call<String> classify(@Body String request);
}