package com.dija.go4lunch;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import androidx.cursoradapter.widget.CursorAdapter;

public class SuggestionCursorAdapter extends CursorAdapter {

    public SuggestionCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    @Override
    public void changeCursor(Cursor newCursor) {
        newCursor.close();
    }
}
