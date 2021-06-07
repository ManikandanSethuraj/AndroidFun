package app.manny.mvvmretrofit.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import app.manny.mvvmretrofit.models.Recipe;
import app.manny.mvvmretrofit.requests.RecipeApiCilent;

public class RecipeRepository {

    private static RecipeRepository recipeRepository;
   // private MutableLiveData<List<Recipe>> mRecipes;
    private RecipeApiCilent recipeApiCilent;
    private String query;
    private int page;
    private MutableLiveData<Boolean> mIsQueryExhauted = new MutableLiveData<>();
    private MediatorLiveData<List<Recipe>> mRecipes = new MediatorLiveData<>();

    public static RecipeRepository getInstance(){
        if (recipeRepository == null){
            recipeRepository = new RecipeRepository();
        }
        return recipeRepository;
    }

    public RecipeRepository(){
       recipeApiCilent = RecipeApiCilent.getInstance();
        // mRecipes = new MutableLiveData<>();
        initMediaters();
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipes;
    }

    private void initMediaters(){
        LiveData<List<Recipe>> recipes = recipeApiCilent.getRecipes();
        mRecipes.addSource(recipes, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                if (recipes != null){
                    mRecipes.setValue(recipes);
                    doneQuery(recipes);
                }else {
                    doneQuery(null);
                }
            }
        });
    }


    private void doneQuery(List<Recipe> list){
        if (list != null){
            if (list.size() % 30 != 0){
                mIsQueryExhauted.setValue(true);
            }
        }else {
            mIsQueryExhauted.setValue(true);
        }
    }

    public LiveData<Boolean> isQueryExhauted(){
        return mIsQueryExhauted;
    }

    public LiveData<Recipe> getRecipe(){
        return recipeApiCilent.getRecipe();
    }

    public LiveData<Boolean> isRecipeTimedOut(){
      return recipeApiCilent.isRecipeRequestTimeout();
    }

    public void searchRecipe(String recipeId){
        recipeApiCilent.searchRecipeById(recipeId);
    }

    public void searchRecipes(String search, int pageNumber){
        if (pageNumber == 0){
            pageNumber = 1;
        }
        query = search;
        page = pageNumber;
        mIsQueryExhauted.setValue(false);
        recipeApiCilent.searchApiRecipe(search, pageNumber);
    }

    public void searchNextRecipe(){
        recipeApiCilent.searchApiRecipe(query, page + 1);
    }

    public void cancelRequest(){
        recipeApiCilent.cancelRequest();
    }

}
