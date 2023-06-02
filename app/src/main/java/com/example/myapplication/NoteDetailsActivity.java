package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEditText, contentEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    String title,content,docId;
    boolean isEditMode = false;
    ImageButton deleteNoteImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteImageBtn = findViewById(R.id.delete_note_image_btn);

        //przechwytywanie danych
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if (docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);

        if(isEditMode){
            pageTitleTextView.setText("Edytuj wybraną notatkę");
            deleteNoteImageBtn.setVisibility(View.VISIBLE);
        }


        saveNoteBtn.setOnClickListener((v)-> saveNote());

        deleteNoteImageBtn.setOnClickListener((v)-> deleteNoteFromFirebase());

    }

    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();

        if(noteTitle == null || noteTitle.isEmpty()){
            titleEditText.setError("Podaj tytuł");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        setSaveNoteToFirebase(note);

    }

    void setSaveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if(isEditMode){
            //ta linijka edytuje notatkę w bazie

            documentReference = Utility.getCollectionReferenceForNotes().document(docId);

        }else{
            //jesli nie jestesmy w 'isEditMode' zwyczajnie dodajemy nową notatkę do bazy

            documentReference = Utility.getCollectionReferenceForNotes().document();
        }


        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //notatka została dodana
                    Utility.showToast(NoteDetailsActivity.this, "Notatka została dodana pomyślnie.");
                    finish();
                }else {
                    //notatka nie została dodana
                    Utility.showToast(NoteDetailsActivity.this, "Błąd podczas dodawania notatki");

                }
            }
        });
    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);


        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //notatka została usunięta
                    Utility.showToast(NoteDetailsActivity.this, "Notatka została usunięta pomyślnie.");
                    finish();
                }else {
                    //notatka nie została dodana
                    Utility.showToast(NoteDetailsActivity.this, "Błąd podczas usuwania notatki");

                }
            }
        });
    }
}