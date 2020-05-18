package com.example.myapplicationvezba;

import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.example.myapplicationvezba.model.Filmovi;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.List;

public class OmiljeniActivity extends AppCompatActivity implements AdapterLista.OnItemClickListener {

    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private AdapterLista adapterLista;
    private List<Filmovi> filmovi;
    private SharedPreferences prefs;

    public static String KEY = "KEY";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omiljeni);
      //  setupToolbar();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        recyclerView = findViewById(R.id.rvRepertoarLista);
        //setupToolbar();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        try {

            filmovi = getDataBaseHelper().getFilmoviDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        adapterLista = new AdapterLista(this, filmovi, this);
        recyclerView.setAdapter(adapterLista);


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_delete, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    public void setupToolbar() {
//        Toolbar toolbar = findViewById(R.id.toolbar_delete);
//        setSupportActionBar(toolbar);
//        toolbar.setTitleTextColor(Color.WHITE);
//        final ActionBar actionBar = getSupportActionBar();
//
//        if (actionBar != null) {
//            actionBar.show();
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.delete_film:
//                deleteFilmove();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void deleteFilmove() {

        int filmZaBrisanje = getIntent().getExtras().getInt("id", 0);
        try {
            getDataBaseHelper().getFilmoviDao().deleteById(filmZaBrisanje);
            finish();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String tekstNotifikacije = "Film je obrisan";

        Toast.makeText(OmiljeniActivity.this, tekstNotifikacije, Toast.LENGTH_LONG).show();

    }
    @Override
    public void onItemClick(int position) {
//        Filmovi film = adapterLista.get(position);
//
//        Intent i = new Intent(OmiljeniActivity.this, DetaljiOmiljeni.class);
//        i.putExtra(KEY, film.getmImdbId());
//        i.putExtra("id", film.getmId());
//        startActivity(i);
       // deleteFilmove();

    }

    private void refresh() {

        RecyclerView recyclerView = findViewById(R.id.rvRepertoarLista);
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            List<Filmovi> film = null;
            try {

                film = getDataBaseHelper().getFilmoviDao().queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            AdapterLista adapter = new AdapterLista(this, film, this);
            recyclerView.setAdapter(adapter);

        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

    }

    public DatabaseHelper getDataBaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }


    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
}
