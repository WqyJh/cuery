package com.wqy.cuery;

import android.database.Cursor;

/**
 * Created by wqy on 16-12-23.
 */

public class ResultSet {
    private Cursor cursor = null;
    private long rowAffected = 0;
    private long rowId = 0;
    private boolean isEmpty = true;
    
    ResultSet() {
        
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        isEmpty = false;
        this.cursor = cursor;
    }

    public long getRowAffected() {
        return rowAffected;
    }

    public void setRowAffected(long rowAffected) {
        isEmpty = false;
        this.rowAffected = rowAffected;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        isEmpty = false;
        this.rowId = rowId;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
