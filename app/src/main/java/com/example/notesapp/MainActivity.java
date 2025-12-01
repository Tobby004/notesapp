package com.example.notesapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etNote;
    private Button btnAdd, btnDelete;
    private ListView lvNotes;

    private ArrayList<String> noteList;
    private ArrayAdapter<String> adapter;
    private SharedPreferences prefs;
    private LinkedHashMap<String, String> notesMap;

    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNote = findViewById(R.id.etNote);
        btnAdd = findViewById(R.id.btnAdd);
        btnDelete = findViewById(R.id.btnDelete);
        lvNotes = findViewById(R.id.lvNotes);

        prefs = getSharedPreferences("MyNotes", MODE_PRIVATE);
        noteList = new ArrayList<>();
        notesMap = new LinkedHashMap<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, noteList);
        lvNotes.setAdapter(adapter);
        lvNotes.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        loadNotes();

        btnAdd.setOnClickListener(v -> addNote());
        btnDelete.setOnClickListener(v -> deleteSelectedNote());

        lvNotes.setOnItemClickListener((parent, view, position, id) -> {
            selectedPosition = position;
            lvNotes.setItemChecked(position, true);
        });
    }

    private void loadNotes() {
        noteList.clear();
        notesMap.clear();

        Map<String, ?> entries = prefs.getAll();
        for (Map.Entry<String, ?> entry : entries.entrySet()) {
            notesMap.put(entry.getKey(), entry.getValue().toString());
        }

        noteList.addAll(notesMap.values());
        adapter.notifyDataSetChanged();
    }

    private void addNote() {
        String note = etNote.getText().toString().trim();
        if (note.isEmpty()) {
            Toast.makeText(this, "Please enter a note", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Note" + System.currentTimeMillis(), note);
        editor.apply();

        etNote.setText("");
        loadNotes();
        Toast.makeText(this, "Note added!", Toast.LENGTH_SHORT).show();
    }

    private void deleteSelectedNote() {
        if (selectedPosition == -1) {
            Toast.makeText(this, "Select a note to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        String keyToDelete = (String) notesMap.keySet().toArray()[selectedPosition];

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(keyToDelete);
        editor.apply();

        selectedPosition = -1;
        loadNotes();
        Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
    }
}
