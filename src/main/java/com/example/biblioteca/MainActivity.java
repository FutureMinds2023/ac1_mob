package com.example.biblioteca;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.biblioteca.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText titleInput, authorInput;
    private Switch readSwitch;
    private Spinner categorySpinner;
    private Button addButton, removeButton, saveButton;
    private ListView bookListView;
    private ArrayList<String> bookList;
    private ArrayAdapter<String> bookAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        titleInput = findViewById(R.id.titleInput);
        authorInput = findViewById(R.id.authorInput);
        readSwitch = findViewById(R.id.readSwitch);
        categorySpinner = findViewById(R.id.categorySpinner);
        addButton = findViewById(R.id.addButton);
        removeButton = findViewById(R.id.removeButton);
        saveButton = findViewById(R.id.saveButton);
        bookListView = findViewById(R.id.bookListView);

        bookList = dbHelper.getAllBooks();
        bookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookList);
        bookListView.setAdapter(bookAdapter);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addButton.setOnClickListener(v -> addBook());
        removeButton.setOnClickListener(v -> removeBook());
        saveButton.setOnClickListener(v -> saveBook());

        bookListView.setOnItemClickListener((parent, view, position, id) -> editBook(position));
        bookListView.setOnItemLongClickListener((parent, view, position, id) -> {
            deleteBook(position);
            return true;
        });
    }

    private void addBook() {
        String title = titleInput.getText().toString();
        String author = authorInput.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();
        String status = readSwitch.isChecked() ? getString(R.string.read) : getString(R.string.not_read);

        if (!title.isEmpty() && !author.isEmpty()) {
            String bookInfo = title + " - " + author + " (" + category + ") - " + status;
            bookList.add(bookInfo);
            bookAdapter.notifyDataSetChanged();
        }
    }

    private void removeBook() {
        if (!bookList.isEmpty()) {
            bookList.remove(bookList.size() - 1);
            bookAdapter.notifyDataSetChanged();
        }
    }

    private void saveBook() {
        String title = titleInput.getText().toString();
        String author = authorInput.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();
        String status = readSwitch.isChecked() ? getString(R.string.read) : getString(R.string.not_read);

        if (!title.isEmpty() && !author.isEmpty()) {
            dbHelper.insertBook(title, author, category, status);
            bookList.add(title + " - " + author + " (" + category + ") - " + status);
            bookAdapter.notifyDataSetChanged();
        }
    }

    private void editBook(int position) {
        String bookDetails = bookList.get(position);
        Intent intent = new Intent(this, EditBookActivity.class);
        intent.putExtra("bookDetails", bookDetails);
        startActivity(intent);
    }

    private void deleteBook(int position) {
        dbHelper.deleteBook(bookList.get(position));
        bookList.remove(position);
        bookAdapter.notifyDataSetChanged();
        Toast.makeText(this, getString(R.string.book_deleted), Toast.LENGTH_SHORT).show();
    }
}
