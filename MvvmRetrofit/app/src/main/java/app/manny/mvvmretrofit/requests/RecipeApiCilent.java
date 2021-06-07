package app.manny.mvvmretrofit.requests;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import app.manny.mvvmretrofit.AppExecuters;
import app.manny.mvvmretrofit.models.Recipe;
import app.manny.mvvmretrofit.requests.responses.RecipeResponse;
import app.manny.mvvmretrofit.requests.responses.RecipeSearchResponse;
import retrofit2.Call;
import retrofit2.Response;

import static app.manny.mvvmretrofit.util.Constants.NETWORK_TIMEOUT;

public class RecipeApiCilent {

    private static final String TAG = "RecipeApiCilent";

    private static RecipeApiCilent instance;
    private MutableLiveData<List<Recipe>> mRecipes;
    private RetrieveRecipesRunnable retrieveRecipesRunnable;
    private MutableLiveData<Recipe> mRecipe;
    private RetrieveRecipeRunnable mRetrieveRecipeRunnable;
    private MutableLiveData<Boolean> mRecipeRequestTimeout =  new MutableLiveData<>();

    public static RecipeApiCilent getInstance(){
        if (instance == null){
            instance = new RecipeApiCilent();
        }
        return instance;
    }

    public RecipeApiCilent(){
        mRecipes = new MutableLiveData<>();
        mRecipe = new MutableLiveData<>();
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipes;
    }

    public LiveData<Recipe> getRecipe(){
        return mRecipe;
    }

    public LiveData<Boolean> isRecipeRequestTimeout(){
        return mRecipeRequestTimeout;
    }

    public void searchApiRecipe(String query, int pageNumber){
        if (retrieveRecipesRunnable != null){
            retrieveRecipesRunnable = null;
        }
        retrieveRecipesRunnable = new RetrieveRecipesRunnable(query, pageNumber);

        final Future handler = AppExecuters.getInstance().getmNetworkIO().submit(retrieveRecipesRunnable);

//        final Future handler = AppExecuters.getInstance().getmNetworkIO().submit(new Runnable() {
//            @Override
//            public void run() {
//                // retrive Data from rest Api
//                //mRecipes.postValue();
//
//            }
//        });

        AppExecuters.getInstance().getmNetworkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // let the user know that the network has timed out.
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }
    public void searchRecipeById(String recipeId){
        if(mRetrieveRecipeRunnable != null){
            mRetrieveRecipeRunnable = null;
        }
        mRetrieveRecipeRunnable = new RetrieveRecipeRunnable(recipeId);

        final Future handler = AppExecuters.getInstance().getmNetworkIO().submit(mRetrieveRecipeRunnable);
        mRecipeRequestTimeout.setValue(false);
        AppExecuters.getInstance().getmNetworkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // let the user know it's timed out
                mRecipeRequestTimeout.postValue(true);
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);

    }

    private class RetrieveRecipesRunnable implements Runnable{

        private String query;
        private int page;
        private boolean cancelRequest;

        public RetrieveRecipesRunnable(String query, int page){
            this.query = query;
            this.page = page;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipes(query, page).execute();
                if (response.code() == 200){
                    List<Recipe> list = new ArrayList<>(((RecipeSearchResponse)response.body()).getRecipes());
                    if (page == 1){
                        mRecipes.postValue(list);
                    }else {
                        List<Recipe> currentRecipes = mRecipes.getValue();
                        currentRecipes.addAll(list);
                        mRecipes.postValue(currentRecipes);

                    }
                }else{
                    Log.d(TAG, "Error: "+response.errorBody().toString());
                    mRecipes.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipes.postValue(null);
            }
        }


        private Call<RecipeSearchResponse> getRecipes(String query, int page){
           return ServiceGenerator.getReceipeApi().searchRecipe(query,String.valueOf(page));
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: "+"cancelling request");
            cancelRequest = true;
        }
    }

    private class RetrieveRecipeRunnable implements Runnable{

        private String recipeId;
        boolean cancelRequest;

        public RetrieveRecipeRunnable(String recipeId) {
            this.recipeId = recipeId;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipe(recipeId).execute();
                if(cancelRequest){
                    return;
                }
                if(response.code() == 200){
                    Recipe recipe = ((RecipeResponse)response.body()).getRecipe();
                    mRecipe.postValue(recipe);
                }
                else{
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error );
                    mRecipe.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipe.postValue(null);
            }

        }

        private Call<RecipeResponse> getRecipe(String recipeId){
            return ServiceGenerator.getReceipeApi().getRecipe(
                    recipeId
            );
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: canceling the search request.");
            cancelRequest = true;
        }
    }

    public void cancelRequest(){
        if (retrieveRecipesRunnable != null) {
            retrieveRecipesRunnable.cancelRequest();
        }
    }

}
