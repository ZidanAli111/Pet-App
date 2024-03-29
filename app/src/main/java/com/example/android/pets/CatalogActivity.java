/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {


    private PetDbHelper petDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


         petDbHelper=new PetDbHelper(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.

        // Create and/or open a database to read from it
        SQLiteDatabase db = petDbHelper.getReadableDatabase();

        String[] project={
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT

                };

        Cursor cursor=db.query(PetEntry.TABLE_NAME,
                project,
                null,
                null,
                null,
                null,
                null);


        TextView displayView=(TextView) findViewById(R.id.text_view_pet);
        try {

            displayView.setText("The pets table contains "+cursor.getCount()+" pets.\n\n");
            displayView.append(PetEntry._ID+"-"+
                    PetEntry.COLUMN_PET_NAME+"-"+
                    PetEntry.COLUMN_PET_BREED+"-"+
                    PetEntry.COLUMN_PET_GENDER+"-"+
                    PetEntry.COLUMN_PET_WEIGHT+"\n");


            int idColumnIndex=cursor.getColumnIndex(PetEntry._ID);
            int nameColumnIndex= cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex=cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex=cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex=cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);



            while (cursor.moveToNext())
            {
                int currentId=cursor.getInt(idColumnIndex);
                String currentName=cursor.getString(nameColumnIndex);
                String currentBreed=cursor.getString(breedColumnIndex);
                String currentGender=cursor.getString(genderColumnIndex);
                String currentWeight=cursor.getString(weightColumnIndex);
                displayView.append(("\n"+currentId+"-"+
                        currentName+"-"+
                        currentBreed+"-"+
                        currentGender+"-"+
                        currentWeight));
            }
        }finally {
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }
    private void insertPet(){

        SQLiteDatabase sqLiteDatabase=petDbHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        contentValues.put(PetEntry.COLUMN_PET_NAME,"Toto");
        contentValues.put(PetEntry.COLUMN_PET_BREED,"Terrier");
        contentValues.put(PetEntry.COLUMN_PET_GENDER,PetEntry.GENDER_MALE);
        contentValues.put(PetEntry.COLUMN_PET_WEIGHT,7);

       long newRowId= sqLiteDatabase.insert(PetEntry.TABLE_NAME,null,contentValues);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
