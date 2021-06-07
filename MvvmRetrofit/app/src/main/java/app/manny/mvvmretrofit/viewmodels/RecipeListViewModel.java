package app.manny.mvvmretrofit.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import app.manny.mvvmretrofit.models.Recipe;
import app.manny.mvvmretrofit.repository.RecipeRepository;

public class RecipeListViewModel extends ViewModel {

   // private MutableLiveData<List<Recipe>> mRecipes;
    private RecipeRepository recipeRepository;
    private boolean mIsViewingRecipes;
    private boolean isPerformingQuery;

    public RecipeListViewModel() {
        recipeRepository = RecipeRepository.getInstance();
    }

    public LiveData<List<Recipe>> getRecipes(){
        return recipeRepository.getRecipes();
    }

    public void searchRecipesApi(String query, int pageNumber){
        mIsViewingRecipes = true;
        isPerformingQuery = true;
        recipeRepository.searchRecipes(query, pageNumber);
    }

    public void searchNextPage(){
        if (mIsViewingRecipes && !isPerformingQuery && !isQueryExhauted().getValue()){
            recipeRepository.searchNextRecipe();
        }
    }

    public LiveData<Boolean> isQueryExhauted(){
        return recipeRepository.isQueryExhauted();
    }

    public boolean isViewingRecipes() {
        return mIsViewingRecipes;
    }

    public void setIsViewingRecipes(boolean isViewingRecipes){
        mIsViewingRecipes = isViewingRecipes;
    }

    public boolean isPerformingQuery() {
        return isPerformingQuery;
    }

    public void setPerformingQuery(boolean isPerformingQuery){
        this.isPerformingQuery = isPerformingQuery;
    }

    public boolean onBackPressed(){
         if (isPerformingQuery){
             // cancel the request
             recipeRepository.cancelRequest();
             isPerformingQuery = false;
         }

        if (mIsViewingRecipes){
            mIsViewingRecipes = false;
            return false;
        }
        return true;
    }
}
