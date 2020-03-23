package com.example.notetoself;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private JSONSerializer serializer;
    private ArrayList<Note> noteList;
//    ArrayList<Note> listNote = new ArrayList<>();
    RecyclerView recyclerView;
    NoteAdapter adapter;
    private boolean showDividers;
    private SharedPreferences prefs;

    public void createNewNote(Note note){
        noteList.add(note);
        adapter.notifyDataSetChanged();
    }
    public void saveNotes() {
        try {
            serializer.save(noteList);
        } catch (Exception e) {
            Log.e("Error saving Notes", "", e);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        saveNotes();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serializer = new JSONSerializer("NoteToSelf.json", getApplicationContext());
        try {
            noteList = serializer.load();
            Log.i("load notes", "" + noteList.size());
        } catch (Exception e) {
            noteList = new ArrayList<>();
            Log.e("Error loading notes: ", "", e);
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogNewNote dialog = new DialogNewNote();
                dialog.show(getSupportFragmentManager(), "");
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new NoteAdapter(this, noteList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void showNote(int index) {
        DialogShowNote dialog = new DialogShowNote();
        dialog.sendNoteSelected(noteList.get(index));
        dialog.show(getSupportFragmentManager(), "");
    }
    @Override
    protected void onResume(){
        super.onResume();
        prefs = getSharedPreferences("Note to self", MODE_PRIVATE);
        showDividers = prefs.getBoolean("dividers", true);
        if (showDividers){
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        }else
            if (recyclerView.getItemDecorationCount()>0){
                recyclerView.removeItemDecorationAt(0);

        }
    }
}
