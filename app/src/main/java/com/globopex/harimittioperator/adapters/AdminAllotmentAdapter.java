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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.activity.AllotmentDetailsActivity;
import com.globopex.harimittioperator.activity.UpdateAllotmentActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Afnan on 05-Jun-18.
 */
public class AdminAllotmentAdapter extends RecyclerView.Adapter<AdminAllotmentAdapter.AdminAllotmentViewHolder> {

    private Activity activity;
    private List<AdminAllotment> adminAllotmentList;

    public AdminAllotmentAdapter(Activity activity, List<AdminAllotment> adminAllotmentList) {
        this.activity = activity;
        this.adminAllotmentList = adminAllotmentList;
    }

    public class AdminAllotmentViewHolder extends RecyclerView.ViewHolder {

        public CardView cv_adminAllotment_card;
        public TextView tv_date_card, tv_month_card, tv_maintainerName_card, tv_memberName_card, tv_status_card;
        public LinearLayout ll_edit_card;

        public AdminAllotmentViewHolder(View view) {
            super(view);

            cv_adminAllotment_card = (CardView) view.findViewById(R.id.cv_allotment_card);
            tv_date_card = (TextView) view.findViewById(R.id.tv_date_card);
            tv_month_card = (TextView) view.findViewById(R.id.tv_month_card);
            tv_maintainerName_card = (TextView) view.findViewById(R.id.tv_name_card);
            tv_memberName_card = (TextView) view.findViewById(R.id.tv_address_card);
            tv_status_card = (TextView) view.findViewById(R.id.tv_status_card);
            ll_edit_card = (LinearLayout) view.findViewById(R.id.ll_edit_card);
        }
    }

    @Override
    public AdminAllotmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.allotment_card, parent, false);

        return new AdminAllotmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdminAllotmentViewHolder holder, final int position) {

        Typeface custom_font_Light = Typeface.createFromAsset(activity.getApplicationContext().getAssets(), "fonts/Pluto Light.ttf");
        Typeface custom_font_Regular = Typeface.createFromAsset(activity.getApplicationContext().getAssets(), "fonts/Pluto Regular.ttf");
        Typeface custom_font_Bold = Typeface.createFromAsset(activity.getApplicationContext().getAssets(), "fonts/Pluto Bold.ttf");
        final AdminAllotment adminAllotment = adminAllotmentList.get(position);

        String dateTime = adminAllotment.getSchedule();
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

        holder.tv_maintainerName_card.setText(adminAllotment.getMaintainerName());
        holder.tv_maintainerName_card.setTypeface(custom_font_Bold);

        holder.tv_memberName_card.setText(adminAllotment.getMemberName());
        holder.tv_memberName_card.setTypeface(custom_font_Regular);

        holder.tv_status_card.setTypeface(custom_font_Regular);
        if (adminAllotment.getStatus().equals("0")) {
            holder.tv_status_card.setText("Pending");
            holder.tv_status_card.setTextColor(Color.RED);

        } else if (adminAllotment.getStatus().equals("1")) {
            holder.tv_status_card.setText("Done");
            holder.tv_status_card.setTextColor(Color.GREEN);

        } else if (adminAllotment.getStatus().equals("2")) {
            holder.tv_status_card.setText("Cancel");
            holder.tv_status_card.setTextColor(Color.YELLOW);
        }

        holder.ll_edit_card.setVisibility(View.VISIBLE);
        holder.ll_edit_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity.getApplicationContext(), UpdateAllotmentActivity.class);
                Bundle bundle = new Bundle();
                // passing array index
                bundle.putInt("allotment_id", adminAllotment.getAllotment_id());
                bundle.putString("maintainer_id", adminAllotment.getMaintainer_id());
                bundle.putString("membership_id", adminAllotment.getMembership_id());
                bundle.putString("maintainerName", adminAllotment.getMaintainerName());
                bundle.putString("memberName", adminAllotment.getMemberName());
                bundle.putString("schedule", adminAllotment.getSchedule());
                bundle.putString("status", adminAllotment.getStatus());
                i.putExtras(bundle);
                activity.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return adminAllotmentList.size();
    }

}
