package com.globopex.harimittioperator.navigationDrawer;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.globopex.harimittioperator.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Afnan on 19-Dec-16.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {

    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Typeface custom_font_Light = Typeface.createFromAsset(context.getAssets(), "fonts/Pluto Light.ttf");

        NavDrawerItem current = data.get(position);
        holder.iv_row_icon.setImageResource(current.getIcon());
        holder.title.setTypeface(custom_font_Light);
        holder.title.setText(current.getTitle());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_row_icon;
        TextView title;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_row_icon = (ImageView) itemView.findViewById(R.id.iv_row_icon);
            title = (TextView) itemView.findViewById(R.id.tv_row_title);
        }
    }
}
