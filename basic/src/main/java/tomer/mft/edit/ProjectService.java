package tomer.mft.edit;

import com.informatica.mi.runtime.ProjectExecutionRequest;
import com.informatica.mi.runtime.ProjectExecutionResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProjectService {
    String jobUrl = "/job";

    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @GET("job/{jobId}")
    Call<ResponseBody> getJobInfo(@Path("jobId") long jobId);

}
