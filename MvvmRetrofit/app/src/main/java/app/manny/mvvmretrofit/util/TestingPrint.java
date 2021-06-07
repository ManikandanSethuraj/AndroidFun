package app.manny.mvvmretrofit.util;

import android.util.Log;

import java.util.List;

import app.manny.mvvmretrofit.models.Recipe;

public  class TestingPrint {

    public static void print(List<Recipe> recipes, String tag){
        for (Recipe recipe: recipes){
            Log.d(tag, "Recipes: "+ recipe.getTitle());
        }

    }

}
