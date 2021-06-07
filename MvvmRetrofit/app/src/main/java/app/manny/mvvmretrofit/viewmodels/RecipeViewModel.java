package app.manny.mvvmretrofit.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import app.manny.mvvmretrofit.models.Recipe;
import app.manny.mvvmretrofit.repository.RecipeRepository;

public class RecipeViewModel extends ViewModel {

    private RecipeRepository recipeRepository;
    private String mRecipeId;
    private boolean retriveRecipe;


    public RecipeViewModel(){
        recipeRepository =RecipeRepository.getInstance();
        retriveRecipe = false;
    }

    public LiveData<Recipe> getRecipe(){
        return recipeRepository.getRecipe();
    }

    public void searchRecipeById(String recipeId){
        this.mRecipeId = recipeId;
        recipeRepository.searchRecipe(recipeId);
    }

    public LiveData<Boolean> isRecipeTimedOut(){
      return   recipeRepository.isRecipeTimedOut();
    }

    public String getRecipeId() {
        return mRecipeId;
    }

    public boolean isRetriveRecipe() {
        return retriveRecipe;
    }

    public void setRetriveRecipe(boolean retriveRecipe) {
        this.retriveRecipe = retriveRecipe;
    }
}
