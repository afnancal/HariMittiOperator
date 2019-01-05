package com.globopex.harimittioperator.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.activity.AllotmentDetailsActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Afnan on 02-May-18.
 */
public class AllotmentAdapter extends RecyclerView.Adapter<AllotmentAdapter.AllotmentViewHolder> {

    private Activity activity;
    private List<Allotment> allotmentList;

    public AllotmentAdapter(Activity activity, List<Allotment> allotmentList) {
        this.activity = activity;
        this.allotmentList = allotmentList;
    }

    public class AllotmentViewHolder extends RecyclerView.ViewHolder {

        public CardView cv_allotment_card;
        public TextView tv_date_card, tv_month_card, tv_name_card, tv_address_card, tv_status_card;

        public AllotmentViewHolder(View view) {
            super(view);

            cv_allotment_card = (CardView) view.findViewById(R.id.cv_allotment_card);
            tv_date_card = (TextView) view.findViewById(R.id.tv_date_card);
            tv_month_card = (TextView) view.findViewById(R.id.tv_month_card);
            tv_name_card = (TextView) view.findViewById(R.id.tv_name_card);
            tv_address_card = (TextView) view.findViewById(R.id.tv_address_card);
            tv_status_card = (TextView) view.findViewById(R.id.tv_status_card);
        }
    }

    @Override
    public AllotmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.allotment_card, parent, false);

        return new AllotmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AllotmentViewHolder holder, final int position) {

        Typeface custom_font_Light = Typeface.createFromAsset(activity.getApplicationContext().getAssets(), "fonts/Pluto Light.ttf");
        Typeface custom_font_Regular = Typeface.createFromAsset(activity.getApplicationContext().getAssets(), "fonts/Pluto Regular.ttf");
        Typeface custom_font_Bold = Typeface.createFromAsset(activity.getApplicationContext().getAssets(), "fonts/Pluto Bold.ttf");
        final Allotment allotment = allotmentList.get(position);

        String dateTime = allotment.getSchedule();
        String[] separated1 = dateTime.split(" ");
        String dateNdMonth = separated1[0];
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = spf.parse(dateNdMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("dd-MMM-yyyy");
        dateNdMonth = spf.format(newDate);
        // Split date and month
        String[] separated = dateNdMonth.split("-");
        String date = separated[0];
        String month = separated[1];

        holder.tv_date_card.setText(date);
        holder.tv_date_card.setTypeface(custom_font_Light);

        holder.tv_month_card.setText(month);
        holder.tv_month_card.setTypeface(custom_font_Bold);

        holder.tv_name_card.setText(allotment.getName());
        holder.tv_name_card.setTypeface(custom_font_Bold);

        holder.tv_address_card.setText(allotment.getAddress());
        holder.tv_address_card.setTypeface(custom_font_Regular);

        holder.tv_status_card.setTypeface(custom_font_Regular);
        if (allotment.getStatus().equals("0")) {
            holder.tv_status_card.setText("Pending");
            holder.tv_status_card.setTextColor(Color.RED);

        } else if (allotment.getStatus().equals("1")) {
            holder.tv_status_card.setText("Done");
            holder.tv_status_card.setTextColor(Color.GREEN);

        } else if (allotment.getStatus().equals("2")) {
            holder.tv_status_card.setText("Cancel");
            holder.tv_status_card.setTextColor(Color.YELLOW);
        }

        holder.cv_allotment_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity.getApplicationContext(), AllotmentDetailsActivity.class);
                Bundle bundle = new Bundle();
                // passing array index
                bundle.putInt("allotment_id", allotment.getAllotment_id());
                bundle.putString("maintainer_id", allotment.getMaintainer_id());
                bundle.putString("membership_id", allotment.getMembership_id());
                bundle.putString("name", allotment.getName());
                bundle.putString("address", allotment.getAddress());
                bundle.putString("contact_no", allotment.getContact_no());
                bundle.putString("email", allotment.getEmail());
                bundle.putString("img_url", allotment.getImg_url());
                bundle.putString("schedule", allotment.getSchedule());
                bundle.putString("status", allotment.getStatus());
                i.putExtras(bundle);
                activity.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return allotmentList.size();
    }

}
