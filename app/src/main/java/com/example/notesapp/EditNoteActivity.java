package com.example.notesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class EditNoteActivity extends AppCompatActivity {

    private EditText editTitle, editContent;
    private Button btnSaveEdit;
    private boolean useSharedPreferences;
    private String oldTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        editTitle   = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        btnSaveEdit = findViewById(R.id.btnSaveEdit);

        Intent intent = getIntent();
        oldTitle = intent.getStringExtra("noteTitle");
        useSharedPreferences = intent.getBooleanExtra("useSharedPreferences", false);

        if (oldTitle == null || oldTitle.trim().isEmpty()) {
            Toast.makeText(this, "No note selected to edit", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadNoteContent(oldTitle);

        btnSaveEdit.setOnClickListener(v -> saveEditedNote());
    }

    private void loadNoteContent(String title) {
        try {
            if (useSharedPreferences) {
                SharedPreferences sp = getSharedPreferences("NotesPrefs", MODE_PRIVATE);
                String content = sp.getString(title, null);
                if (content == null) {
                    Toast.makeText(this, "Note not found in SharedPreferences", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                editTitle.setText(title);
                editContent.setText(content);
            } else {
                File file = new File(getFilesDir(), title + ".txt");
                if (!file.exists()) {
                    Toast.makeText(this, "Note file not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                }
                editTitle.setText(title);
                editContent.setText(sb.toString().trim());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading note", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveEditedNote() {
        String newTitle = safe(editTitle.getText().toString());
        String newContent = safe(editContent.getText().toString());

        if (newTitle.isEmpty() || newContent.isEmpty()) {
            Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (useSharedPreferences) {
                SharedPreferences sp = getSharedPreferences("NotesPrefs", MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                if (!oldTitle.equals(newTitle)) {
                    ed.remove(oldTitle); // rename behavior
                }
                ed.putString(newTitle, newContent);
                ed.apply();
            } else {
                // rename file if title changed
                if (!oldTitle.equals(newTitle)) {
                    File oldFile = new File(getFilesDir(), oldTitle + ".txt");
                    if (oldFile.exists()) oldFile.delete();
                }
                try (FileOutputStream fos = openFileOutput(newTitle + ".txt", Context.MODE_PRIVATE)) {
                    fos.write(newContent.getBytes());
                }
            }
            Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving note", Toast.LENGTH_SHORT).show();
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
