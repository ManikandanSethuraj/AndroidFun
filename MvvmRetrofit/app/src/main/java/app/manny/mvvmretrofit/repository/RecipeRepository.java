package app.manny.mvvmretrofit.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import app.manny.mvvmretrofit.models.Recipe;

public class RecipeRepository {

    private static RecipeRepository recipeRepository;
    private MutableLiveData<List<Recipe>> mRecipes;

    public static RecipeRepository getInstance(){
        if (recipeRepository == null){
            recipeRepository = new RecipeRepository();
        }
        return recipeRepository;
    }

    public RecipeRepository(){
        mRecipes = new MutableLiveData<>();
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipes;
    }

}
