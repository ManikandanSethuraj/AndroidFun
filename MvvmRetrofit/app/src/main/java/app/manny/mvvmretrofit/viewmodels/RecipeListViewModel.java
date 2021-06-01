package app.manny.mvvmretrofit.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import app.manny.mvvmretrofit.models.Recipe;
import app.manny.mvvmretrofit.repository.RecipeRepository;

public class RecipeListViewModel extends ViewModel {

   // private MutableLiveData<List<Recipe>> mRecipes;
    private RecipeRepository recipeRepository;

    public RecipeListViewModel() {
        recipeRepository = RecipeRepository.getInstance();
    }

    public LiveData<List<Recipe>> getRecipes(){
        return recipeRepository.getRecipes();
    }
}
