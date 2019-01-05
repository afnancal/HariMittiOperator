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
import com.globopex.harimittioperator.activity.UpdateMemberActivity;
import com.globopex.harimittioperator.network.DownloadImage;

import java.util.List;

/**
 * Created by Afnan on 04-Jun-18.
 */
public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private Activity activity;
    private List<Member> memberList;

    public MemberAdapter(Activity activity, List<Member> memberList) {
        this.activity = activity;
        this.memberList = memberList;
    }

    public class MemberViewHolder extends RecyclerView.ViewHolder {

        public CardView cv_member_card;
        public ImageView iv_member_card;
        public TextView tv_id_mainCard, tv_name_mainCard, tv_address_mainCard;

        public MemberViewHolder(View view) {
            super(view);

            cv_member_card = (CardView) view.findViewById(R.id.cv_maintainer_card);
            iv_member_card = (ImageView) view.findViewById(R.id.iv_maintainer_card);
            tv_id_mainCard = (TextView) view.findViewById(R.id.tv_id_mainCard);
            tv_name_mainCard = (TextView) view.findViewById(R.id.tv_name_mainCard);
            tv_address_mainCard = (TextView) view.findViewById(R.id.tv_address_mainCard);
        }
    }

    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.maintainer_card, parent, false);

        return new MemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MemberViewHolder holder, final int position) {

        DownloadImage downloadImage = new DownloadImage();
        Typeface custom_font_Regular = Typeface.createFromAsset(activity.getAssets(), "fonts/Pluto Regular.ttf");
        Typeface custom_font_Bold = Typeface.createFromAsset(activity.getAssets(), "fonts/Pluto Bold.ttf");
        final Member member = memberList.get(position);

        if (!member.getImg_url().equals("")) {
            downloadImage.setDownloadImage(activity, holder.iv_member_card, member.getImg_url());
        } else {
            holder.iv_member_card.setImageResource(R.drawable.ic_user);
        }

        holder.tv_id_mainCard.setVisibility(View.VISIBLE);
        holder.tv_id_mainCard.setText("Member Id: " + member.getMembership_id());
        holder.tv_id_mainCard.setTypeface(custom_font_Regular);

        holder.tv_name_mainCard.setText(member.getName());
        holder.tv_name_mainCard.setTypeface(custom_font_Bold);

        holder.tv_address_mainCard.setText(member.getAddress());
        holder.tv_address_mainCard.setTypeface(custom_font_Regular);

        holder.cv_member_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (member.getActivity_name().equals("AdminHomeActivity")) {
                    Intent intent = new Intent(activity, UpdateMemberActivity.class);
                    Bundle bundle = new Bundle();
                    // passing array index
                    bundle.putString("membership_id", member.getMembership_id());
                    bundle.putString("name", member.getName());
                    bundle.putString("address", member.getAddress());
                    bundle.putString("contact_no", member.getContact_no());
                    bundle.putString("email", member.getEmail());
                    bundle.putString("password", member.getPassword());
                    bundle.putString("gcm_reg", member.getGcm_reg());
                    bundle.putString("img_url", member.getImg_url());
                    intent.putExtras(bundle);
                    activity.startActivity(intent);

                } else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("membership_id", member.getMembership_id());
                    returnIntent.putExtra("member_name", member.getName());
                    returnIntent.putExtra("gcm_reg", member.getGcm_reg());
                    activity.setResult(Activity.RESULT_OK, returnIntent);
                    activity.finish();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

}
