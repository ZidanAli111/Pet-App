package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.pets.data.PetContract.PetEntry;

public class PetProvider extends ContentProvider {


    private static final int PETS = 100;

    private static final int PETS_ID = 101;

    private PetDbHelper petDbHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATHS_PETS, PETS);

        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATHS_PETS + "/#", PETS_ID);
    }

    @Override
    public boolean onCreate() {

        petDbHelper = new PetDbHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = petDbHelper.getReadableDatabase();


        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {

            case PETS:
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case PETS_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);


        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match=sUriMatcher.match(uri);
        switch (match){

            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PETS_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw  new IllegalArgumentException("Unknown URI:"+uri+"with match:"+match);



        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);


        switch (match) {

            case PETS:
                return insertPet(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for this uri:" + uri);


        }
    }

    private Uri insertPet(Uri uri, ContentValues contentValues) {

        String name = contentValues.getAsString(PetEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }



        Integer gender = contentValues.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        if (gender == null || !PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet require valid weight");
        }


        Integer weight = contentValues.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet require valid weight");

        }


        SQLiteDatabase sqLiteDatabase = petDbHelper.getWritableDatabase();

        long id = sqLiteDatabase.insert(PetContract.PetEntry.TABLE_NAME, null, contentValues);

        if (id == -1) {
            Log.e("SIGMA", "Failed to insert row for:" + uri);
            return null;

        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {


        SQLiteDatabase sqLiteDatabase=petDbHelper.getWritableDatabase();
        final int match=sUriMatcher.match(uri);
        switch (match)
        {
            case PETS:
                return sqLiteDatabase.delete(PetEntry.TABLE_NAME,selection,selectionArgs);

            case PETS_ID:
                selection=PetEntry._ID+"=?";
                selectionArgs=new String[]{
                        String.valueOf(ContentUris.parseId(uri))};
                return  sqLiteDatabase.delete(PetEntry.TABLE_NAME,selection,selectionArgs);

            default:
                throw new IllegalArgumentException("Deletion is not supported for:"+uri);
        }

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match=sUriMatcher.match(uri);
        switch (match)
        {
            case PETS:
                return updatePet(uri,contentValues,selection,selectionArgs);

            case PETS_ID:
                selection=PetEntry._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri,contentValues,selection,selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for:"+uri);


        }
    }

    private int updatePet(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        if (contentValues.containsKey(PetEntry.COLUMN_PET_NAME))
        {
            String name=contentValues.getAsString(PetEntry.COLUMN_PET_NAME);
            if (name==null)
            {
                throw new IllegalArgumentException("Pet requires a name");

            }
        }

        if (contentValues.containsKey(PetEntry.COLUMN_PET_GENDER))
        {
            Integer gender=contentValues.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if (gender==null||!PetEntry.isValidGender(gender))
            {
                throw new IllegalArgumentException("Pet requires a valid gender");

            }
        }

    if (contentValues.containsKey(PetEntry.COLUMN_PET_WEIGHT))
    {
        Integer weight=contentValues.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if (weight!=null&&weight<0)
        {
            throw new IllegalArgumentException("Pet requires a valid weight");
        }

    }

    if (contentValues.size()==0)
        return 0;


    SQLiteDatabase sqLiteDatabase=petDbHelper.getWritableDatabase();
    return sqLiteDatabase.update(PetEntry.TABLE_NAME,contentValues,selection,selectionArgs);


    }


}
