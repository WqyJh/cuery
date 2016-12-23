package com.wqy.cuery;

import android.database.Cursor;

/**
 * Created by wqy on 16-12-23.
 */

public class ResultSet {
    private Cursor cursor = null;
    private long rowAffected = 0;
    private long rowInserted = 0;
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

    public long getRowInserted() {
        return rowInserted;
    }

    public void setRowInserted(long rowInserted) {
        isEmpty = false;
        this.rowInserted = rowInserted;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
