package app.manny.mvvmretrofit;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.manny.mvvmretrofit.models.Recipe;
import app.manny.mvvmretrofit.requests.ReceipeApi;
import app.manny.mvvmretrofit.requests.ServiceGenerator;
import app.manny.mvvmretrofit.requests.responses.RecipeSearchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListActivity extends BaseActivity {

    private static final String TAG = "RecipeListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipelist);


        findViewById(R.id.test).setOnClickListener(view -> {
            Log.d("Logging","1");
            testRecipeApi();
           // setProgressBar(progressBar.getVisibility() != View.VISIBLE);
        });
    }


    private void testRecipeApi(){
        ReceipeApi receipeApi = ServiceGenerator.getReceipeApi();

        Call<RecipeSearchResponse> responseCall = receipeApi.searchRecipe("chicken","1");

        responseCall.enqueue(new Callback<RecipeSearchResponse>() {
            @Override
            public void onResponse(Call<RecipeSearchResponse> call, Response<RecipeSearchResponse> response) {
                Log.d(TAG, "onResponse: "+response.toString());
                Log.d(TAG, "onCall: "+call.toString());
                if (response.code() == 200){
                    Log.d(TAG, "onResponse:Body::"+response.body().toString());
                    List<Recipe> recipeList = new ArrayList<>(response.body().getRecipes());
                    for (Recipe recipe : recipeList){
                        Log.d(TAG, "onResponse:List"+recipe.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<RecipeSearchResponse> call, Throwable t) {

            }
        });
    }
}