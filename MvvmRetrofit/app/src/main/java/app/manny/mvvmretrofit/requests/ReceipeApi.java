package app.manny.mvvmretrofit.requests;

import app.manny.mvvmretrofit.requests.responses.RecipeResponse;
import app.manny.mvvmretrofit.requests.responses.RecipeSearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ReceipeApi {


    @GET("api/search")
    Call<RecipeSearchResponse> searchRecipe(
            @Query("q")String query,
            @Query("page") String page
    );
    
    
    @GET("api/get")
    Call<RecipeResponse> getRecipe(
            @Query("rId")String recipeid
    );
}
