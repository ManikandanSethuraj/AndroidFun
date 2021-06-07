package app.manny.mvvmretrofit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.manny.mvvmretrofit.adapters.OnRecipeListener;
import app.manny.mvvmretrofit.adapters.RecipeRecyclerAdapter;
import app.manny.mvvmretrofit.models.Recipe;
import app.manny.mvvmretrofit.requests.ReceipeApi;
import app.manny.mvvmretrofit.requests.ServiceGenerator;
import app.manny.mvvmretrofit.requests.responses.RecipeResponse;
import app.manny.mvvmretrofit.requests.responses.RecipeSearchResponse;
import app.manny.mvvmretrofit.util.TestingPrint;
import app.manny.mvvmretrofit.util.VerticalSpacingItemDecorator;
import app.manny.mvvmretrofit.viewmodels.RecipeListViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListActivity extends BaseActivity implements OnRecipeListener {

    private static final String TAG = "RecipeListActivity";

    private RecipeListViewModel recipeListViewModel;
    private RecyclerView recyclerView;
    private RecipeRecyclerAdapter recipeRecyclerAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipelist);

        recipeListViewModel =new ViewModelProvider(this).get(RecipeListViewModel.class);
        recyclerView = findViewById(R.id.recipe_recycler_view);
        searchView = findViewById(R.id.search_view);
        initRecyclerView();
        subscribeObservers();
        initSearchView();
        if(!recipeListViewModel.isViewingRecipes()){
            displaySearchCategories();
        }
      //  testRecipeApiTwo();

//        findViewById(R.id.test).setOnClickListener(view -> {
//            Log.d("Logging","1");
//            testRecipeApiTwo();
//            // testRecipeApi();
//           // setProgressBar(progressBar.getVisibility() != View.VISIBLE);
//        });
    }


    private void initRecyclerView(){
        recipeRecyclerAdapter = new RecipeRecyclerAdapter(this);
        VerticalSpacingItemDecorator verticalSpacingItemDecorator = new VerticalSpacingItemDecorator(30);
        recyclerView.addItemDecoration(verticalSpacingItemDecorator);
        recyclerView.setAdapter(recipeRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                             @Override
                                             public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                                 if (!recyclerView.canScrollVertically(1)){
                                                       recipeListViewModel.searchNextPage();
                                                 }
                                             }
                                         }
        );

    }

    private void subscribeObservers(){
        recipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                if (recipes != null){
                    if (recipeListViewModel.isViewingRecipes()){
                        TestingPrint.print(recipes, "SubRecipes");
                        recipeListViewModel.setPerformingQuery(false);
                        recipeRecyclerAdapter.setRecipes(recipes);

                    }
                }

            }
        });

        recipeListViewModel.isQueryExhauted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Log.d(TAG, "onChanged: QueryIsExhauted");
                    recipeRecyclerAdapter.setQueryExhausted();
                }
            }
        });
    }

    private void displaySearchCategories(){
        Log.d(TAG, "displaySearchCategories: called.");
        recipeListViewModel.setIsViewingRecipes(false);
        recipeRecyclerAdapter.displaySearchCategories();
    }


    private void initSearchView(){

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                recipeRecyclerAdapter.displayLoading();
                recipeListViewModel.searchRecipesApi(s,0);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (recipeListViewModel.onBackPressed()){
            super.onBackPressed();
        }else {
            displaySearchCategories();
        }

    }

    private void testRecipeApiTwo(){
        recipeListViewModel.searchRecipesApi("chicken",0);
    }

    private void testRecipeSearchApi(){
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
                }else {
                    try {
                        Log.d(TAG, "onErrorBody: "+response.errorBody().toString());
                    }catch (Exception e){
                        Log.d(TAG, "onError: "+e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<RecipeSearchResponse> call, Throwable t) {

            }
        });
    }

    private void testRecipeApi(){
        ReceipeApi receipeApi = ServiceGenerator.getReceipeApi();

        Call<RecipeResponse> responseCall = receipeApi.getRecipe("41470");

        responseCall.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                Log.d(TAG, "onResponse: "+response.toString());
                Log.d(TAG, "onCall: "+call.toString());
                if (response.code() == 200){
                    Log.d(TAG, "onResponse:Body::"+response.body().toString());

                }else {
                    try {
                        Log.d(TAG, "onErrorBody: "+response.errorBody().toString());
                    }catch (Exception e){
                        Log.d(TAG, "onError: "+e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onRecipeClick(int position) {
        Log.d(TAG, "onRecipeClick: clicked. " + position);
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe", recipeRecyclerAdapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {
        Log.d(TAG, "onCategoryClick: clicked. ");
        recipeRecyclerAdapter.displayLoading();
        recipeListViewModel.searchRecipesApi(category, 1);
        searchView.clearFocus();
    }
}