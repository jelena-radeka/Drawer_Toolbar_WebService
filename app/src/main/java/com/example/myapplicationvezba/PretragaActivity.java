package com.example.myapplicationvezba;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplicationvezba.model.Example;
import com.example.myapplicationvezba.model.Filmovi;
import com.example.myapplicationvezba.model.MyService;
import com.example.myapplicationvezba.model.Search;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.Contacts.SettingsColumns.KEY;

public class PretragaActivity extends AppCompatActivity  implements  MyAdapter2.OnItemClickListener {

    private RecyclerView recyclerView;
    private MyAdapter2 adapter;
    private RecyclerView.LayoutManager layoutManager;
    Button btnSearch;
    EditText movieName;
    int position = 0;
    private DatabaseHelper databaseHelper;
    public static String KEY = "KEY";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pretraga);
       setupToolbar();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        int position = getIntent().getExtras().getInt("position", 0);

        btnSearch = findViewById(R.id.btn_search);
        movieName = findViewById(R.id.ime_filma);
        recyclerView = findViewById(R.id.rvLista);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMovieByName(movieName.getText().toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meni_favorits, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.omiljeni_lista:
                startActivity(new Intent(PretragaActivity.this, OmiljeniActivity.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_pretraga);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.show();
        }
    }

    private void getMovieByName(String name) {
        Map<String, String> query = new HashMap<>();
        query.put("apikey", "APIKEY");
        query.put("s", name.trim());

        Call<Example> call = MyService.apiInterface().getMovieByName(query);
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {

                if (response.code() == 200) {
                    try {
                        Example searches = response.body();

                        ArrayList<Search> search = new ArrayList<>();

                        for (Search e : searches.getSearch()) {

                            if (e.getType().equals("movie") || e.getType().equals("series")) {
                                search.add(e);
                            }
                        }

                        layoutManager = new LinearLayoutManager(PretragaActivity.this);
                        recyclerView.setLayoutManager(layoutManager);

                        adapter = new MyAdapter2(PretragaActivity.this, search, PretragaActivity.this);
                        recyclerView.setAdapter(adapter);
                        btnSearch.setVisibility(View.GONE);

                        Toast.makeText(PretragaActivity.this, "Prikaz filmova/serija.", Toast.LENGTH_SHORT).show();

                    } catch (NullPointerException e) {
                        Toast.makeText(PretragaActivity.this, "Ne postoji film/serija sa tim nazivom", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(PretragaActivity.this, "Greska sa serverom", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Toast.makeText(PretragaActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onItemClick(int position) {
        Search filmovi = adapter.get(position);
        Intent i = new Intent(PretragaActivity.this, DetaljiActivity.class);
        i.putExtra(KEY, filmovi.getImdbID());
        i.putExtra("id", filmovi.getTitle());
        startActivity(i);
    }

}

