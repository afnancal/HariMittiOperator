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
import com.globopex.harimittioperator.activity.AllotmentListActivity;
import com.globopex.harimittioperator.activity.UpdateMaintainerActivity;
import com.globopex.harimittioperator.activity.UpdateMemberActivity;
import com.globopex.harimittioperator.network.DownloadImage;

import java.util.List;

/**
 * Created by Afnan on 04-Jun-18.
 */
public class MaintainerAdapter extends RecyclerView.Adapter<MaintainerAdapter.MaintainerViewHolder> {

    private Activity activity;
    private List<Maintainer> maintainerList;

    public MaintainerAdapter(Activity activity, List<Maintainer> maintainerList) {
        this.activity = activity;
        this.maintainerList = maintainerList;
    }

    public class MaintainerViewHolder extends RecyclerView.ViewHolder {

        public CardView cv_maintainer_card;
        public ImageView iv_maintainer_card;
        public TextView tv_name_mainCard, tv_address_mainCard;

        public MaintainerViewHolder(View view) {
            super(view);

            cv_maintainer_card = (CardView) view.findViewById(R.id.cv_maintainer_card);
            iv_maintainer_card = (ImageView) view.findViewById(R.id.iv_maintainer_card);
            tv_name_mainCard = (TextView) view.findViewById(R.id.tv_name_mainCard);
            tv_address_mainCard = (TextView) view.findViewById(R.id.tv_address_mainCard);
        }
    }

    @Override
    public MaintainerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.maintainer_card, parent, false);

        return new MaintainerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MaintainerViewHolder holder, final int position) {

        DownloadImage downloadImage = new DownloadImage();
        Typeface custom_font_Regular = Typeface.createFromAsset(activity.getAssets(), "fonts/Pluto Regular.ttf");
        Typeface custom_font_Bold = Typeface.createFromAsset(activity.getAssets(), "fonts/Pluto Bold.ttf");
        final Maintainer maintainer = maintainerList.get(position);

        if (!maintainer.getImg_url().equals("")) {
            downloadImage.setDownloadImage(activity, holder.iv_maintainer_card, maintainer.getImg_url());
        } else {
            holder.iv_maintainer_card.setImageResource(R.drawable.ic_user);
        }

        holder.tv_name_mainCard.setText(maintainer.getName());
        holder.tv_name_mainCard.setTypeface(custom_font_Bold);

        holder.tv_address_mainCard.setText(maintainer.getAddress());
        holder.tv_address_mainCard.setTypeface(custom_font_Regular);

        holder.cv_maintainer_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (maintainer.getActivity_name().equals("AdminHomeActivity")) {
                    Intent intent = new Intent(activity, AllotmentListActivity.class);
                    Bundle bundle = new Bundle();
                    // passing array index
                    intent.putExtra("maintainer_id", maintainer.getMaintainer_id());
                    intent.putExtras(bundle);
                    activity.startActivity(intent);

                } else if (maintainer.getActivity_name().equals("AdminHomeActivityForMaintainer")) {
                    Intent intent = new Intent(activity, UpdateMaintainerActivity.class);
                    Bundle bundle = new Bundle();
                    // passing array index
                    bundle.putString("maintainer_id", maintainer.getMaintainer_id());
                    bundle.putString("name", maintainer.getName());
                    bundle.putString("address", maintainer.getAddress());
                    bundle.putString("contact_no", maintainer.getContact_no());
                    bundle.putString("email", maintainer.getEmail());
                    bundle.putString("password", maintainer.getPassword());
                    bundle.putString("gcm_reg", maintainer.getGcm_reg());
                    bundle.putString("img_url", maintainer.getImg_url());
                    intent.putExtras(bundle);
                    activity.startActivity(intent);

                } else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("maintainer_id", maintainer.getMaintainer_id());
                    returnIntent.putExtra("maintainer_name", maintainer.getName());
                    returnIntent.putExtra("gcm_reg", maintainer.getGcm_reg());
                    activity.setResult(Activity.RESULT_OK, returnIntent);
                    activity.finish();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return maintainerList.size();
    }

}
