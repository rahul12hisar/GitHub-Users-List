package com.example.amit.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    private String response;
    private boolean status;
    RecyclerView recyclerView;
    EditText editTextSearch;
    private LinearLayoutManager mLayoutManager;
    private ActivityAdapter mAdapter;
    ArrayList<ViewModel> viewModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        editTextSearch.addTextChangedListener(filterTextWatcher);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        getResponse();

    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            filter(s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }
    };

    private void filter(String s) {
        //new array list that will hold the filtered data
        ArrayList<ViewModel> filterdNames = new ArrayList<>();
        //looping through existing elements
        for (int i = 0; i < viewModelList.size(); i++) {
            ViewModel viewModel = viewModelList.get(i);
            if (viewModel.getName().contains(s)) {
                filterdNames.add(viewModel);
            }
        }
        //calling a method of the adapter class and passing the filtered list
        mAdapter.filterList(filterdNames);
    }

    private void getResponse() {
        //calling a method for get userlist in Json from gitHub and parsing the data
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            // show "Connected" & type of network "WIFI or MOBILE"
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://api.github.com/search/repositories?q=tetris+language:assembly&sort=stars&order=desc";

// Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("items");
                                System.out.print("jsonArray object is : " + jsonObject.get("items"));
                                //getting name and score from JsonArray
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject userObject = (JSONObject) jsonArray.get(i);
                                    String name = userObject.getString("name");
                                    String score = userObject.getString("score");
                                    ViewModel viewModel = new ViewModel();
                                    viewModel.setName(name);
                                    viewModel.setScore(Float.parseFloat(score));
                                    viewModelList.add(viewModel);
                                }
                                //sorting the data by user's Score(ascending order)
                                Collections.sort(viewModelList, new Comparator<ViewModel>() {
                                    @Override
                                    public int compare(ViewModel lhs, ViewModel rhs) {
                                        int result = Float.compare(lhs.getScore(), rhs.getScore());
                                        if (result == 0)
                                            result = Float.compare(lhs.getScore(), rhs.getScore());
                                        return result;
                                    }
                                });
                                mAdapter = new ActivityAdapter(viewModelList);
                                recyclerView.setAdapter(mAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
// Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
    }
}
