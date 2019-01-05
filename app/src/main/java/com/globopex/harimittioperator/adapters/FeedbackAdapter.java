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
import com.globopex.harimittioperator.activity.AdminFeedbackActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Afnan on 22-Jun-18.
 */
public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private Activity activity;
    private List<Feedback> feedbackList;

    public FeedbackAdapter(Activity activity, List<Feedback> feedbackList) {
        this.activity = activity;
        this.feedbackList = feedbackList;
    }

    public class FeedbackViewHolder extends RecyclerView.ViewHolder {

        public CardView cv_feedback_card;
        public TextView tv_date_card, tv_month_card, tv_memberId_card, tv_maintName_card, tv_status_card;

        public FeedbackViewHolder(View view) {
            super(view);

            cv_feedback_card = (CardView) view.findViewById(R.id.cv_allotment_card);
            tv_date_card = (TextView) view.findViewById(R.id.tv_date_card);
            tv_month_card = (TextView) view.findViewById(R.id.tv_month_card);
            tv_memberId_card = (TextView) view.findViewById(R.id.tv_name_card);
            tv_maintName_card = (TextView) view.findViewById(R.id.tv_address_card);
            tv_status_card = (TextView) view.findViewById(R.id.tv_status_card);
        }
    }

    @Override
    public FeedbackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.allotment_card, parent, false);

        return new FeedbackViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FeedbackViewHolder holder, final int position) {

        Typeface custom_font_Light = Typeface.createFromAsset(activity.getApplicationContext().getAssets(), "fonts/Pluto Light.ttf");
        Typeface custom_font_Regular = Typeface.createFromAsset(activity.getApplicationContext().getAssets(), "fonts/Pluto Regular.ttf");
        Typeface custom_font_Bold = Typeface.createFromAsset(activity.getApplicationContext().getAssets(), "fonts/Pluto Bold.ttf");
        final Feedback feedback = feedbackList.get(position);

        String dateTime = feedback.getAction_on();
        String[] separated1 = dateTime.split(" ");
        String dateNdMonth = separated1[0];
        SimpleDateFormat spf = new SimpleDateFormat("yyyy/MM/dd");
        Date newDate = null;
        try {
            newDate = spf.parse(dateNdMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("dd/MMM/yyyy");
        dateNdMonth = spf.format(newDate);
        // Split date and month
        String[] separated = dateNdMonth.split("/");
        String date = separated[0];
        String month = separated[1];

        holder.tv_date_card.setText(date);
        holder.tv_date_card.setTypeface(custom_font_Light);

        holder.tv_month_card.setText(month);
        holder.tv_month_card.setTypeface(custom_font_Bold);

        holder.tv_memberId_card.setText("Member Id: " + feedback.getMembership_id());
        holder.tv_memberId_card.setTypeface(custom_font_Regular);

        holder.tv_maintName_card.setText(feedback.getMaintainer_name());
        holder.tv_maintName_card.setTypeface(custom_font_Regular);

        holder.tv_status_card.setTypeface(custom_font_Regular);
        if (feedback.getStatus() == 0) {
            holder.tv_status_card.setText("Pending");
            holder.tv_status_card.setTextColor(Color.RED);

        } else if (feedback.getStatus() == 1) {
            holder.tv_status_card.setText("Done");
            holder.tv_status_card.setTextColor(Color.GREEN);

        } else if (feedback.getStatus() == 2) {
            holder.tv_status_card.setText("Cancel");
            holder.tv_status_card.setTextColor(Color.YELLOW);
        }

        holder.cv_feedback_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity.getApplicationContext(), AdminFeedbackActivity.class);
                Bundle bundle = new Bundle();
                // passing array index
                bundle.putString("maintainer_id", feedback.getMaintainer_id());
                bundle.putString("membership_id", feedback.getMembership_id());
                bundle.putString("plant_img1", feedback.getPlant_img1());
                bundle.putString("plant_img1", feedback.getPlant_img1());
                bundle.putString("plant_img2", feedback.getPlant_img2());
                bundle.putString("plant_img3", feedback.getPlant_img3());
                bundle.putString("plant_img4", feedback.getPlant_img4());
                bundle.putString("plant_img5", feedback.getPlant_img5());
                bundle.putString("description", feedback.getDescription());
                bundle.putString("audio_file_url", feedback.getAudio_file_url());
                bundle.putInt("status", feedback.getStatus());
                bundle.putString("payment_method", feedback.getPayment_method());
                bundle.putString("chk_img_url", feedback.getChk_img_url());
                bundle.putString("amount", feedback.getAmount());
                bundle.putString("action_on", feedback.getAction_on());
                i.putExtras(bundle);
                activity.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

}
