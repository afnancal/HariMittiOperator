package com.globopex.harimittioperator.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.activity.UpdateAdminActivity;
import com.globopex.harimittioperator.network.DownloadImage;

import java.util.List;

/**
 * Created by Afnan on 28-Jun-18.
 */
public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {

    private Activity activity;
    private List<Admin> adminList;

    public AdminAdapter(Activity activity, List<Admin> adminList) {
        this.activity = activity;
        this.adminList = adminList;
    }

    public class AdminViewHolder extends RecyclerView.ViewHolder {

        public CardView cv_admin_card;
        public ImageView iv_admin_card;
        public TextView tv_id_adminCard, tv_name_adminCard, tv_address_adminCard;

        public AdminViewHolder(View view) {
            super(view);

            cv_admin_card = (CardView) view.findViewById(R.id.cv_maintainer_card);
            iv_admin_card = (ImageView) view.findViewById(R.id.iv_maintainer_card);
            tv_id_adminCard = (TextView) view.findViewById(R.id.tv_id_mainCard);
            tv_name_adminCard = (TextView) view.findViewById(R.id.tv_name_mainCard);
            tv_address_adminCard = (TextView) view.findViewById(R.id.tv_address_mainCard);
        }
    }

    @Override
    public AdminViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.maintainer_card, parent, false);

        return new AdminViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdminViewHolder holder, final int position) {

        DownloadImage downloadImage = new DownloadImage();
        Typeface custom_font_Regular = Typeface.createFromAsset(activity.getAssets(), "fonts/Pluto Regular.ttf");
        Typeface custom_font_Bold = Typeface.createFromAsset(activity.getAssets(), "fonts/Pluto Bold.ttf");
        final Admin admin = adminList.get(position);

        if (!admin.getImg_url().equals("")) {
            downloadImage.setDownloadImage(activity, holder.iv_admin_card, admin.getImg_url());
        } else {
            holder.iv_admin_card.setImageResource(R.drawable.ic_user);
        }

        holder.tv_id_adminCard.setVisibility(View.VISIBLE);
        holder.tv_id_adminCard.setText("Member Id: " + admin.getAdmin_id());
        holder.tv_id_adminCard.setTypeface(custom_font_Regular);

        holder.tv_name_adminCard.setText(admin.getName());
        holder.tv_name_adminCard.setTypeface(custom_font_Bold);

        holder.tv_address_adminCard.setText(admin.getAddress());
        holder.tv_address_adminCard.setTypeface(custom_font_Regular);

        holder.cv_admin_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (admin.getActivity_name().equals("AdminHomeActivity")) {
                    Intent intent = new Intent(activity, UpdateAdminActivity.class);
                    Bundle bundle = new Bundle();
                    // passing array index
                    bundle.putString("admin_id", admin.getAdmin_id());
                    bundle.putString("name", admin.getName());
                    bundle.putString("address", admin.getAddress());
                    bundle.putString("contact_no", admin.getContact_no());
                    bundle.putString("email", admin.getEmail());
                    bundle.putString("password", admin.getPassword());
                    bundle.putString("gcm_reg", admin.getGcm_reg());
                    bundle.putString("img_url", admin.getImg_url());
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return adminList.size();
    }

}
