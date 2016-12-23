package com.wqy.example;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by wqy on 16-12-22.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private RecyclerView recyclerView;
    private Context context;
    private Cursor cursor;

    public RecyclerViewAdapter(Context context, Cursor c, RecyclerView recyclerView) {
        this.context = context;
        this.cursor = c;
        this.recyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }
        holder.id.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(DBContract.User._ID))));
        holder.username.setText(cursor.getString(cursor.getColumnIndex(DBContract.User.USERNAME)));
        holder.password.setText(cursor.getString(cursor.getColumnIndex(DBContract.User.PASSWORD)));
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void swapCursor(Cursor c) {
        Cursor old = cursor;
        cursor = c;
        this.notifyDataSetChanged();
        old.close();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        cursor.close();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView username;
        public TextView password;

        public ViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.item_id);
            username = (TextView) itemView.findViewById(R.id.item_username);
            password = (TextView) itemView.findViewById(R.id.item_password);
        }
    }
}
