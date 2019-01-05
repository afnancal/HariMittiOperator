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
import com.globopex.harimittioperator.activity.UpdateAdminActivity;
import com.globopex.harimittioperator.activity.UpdateMaintainerActivity;
import com.globopex.harimittioperator.activity.UpdateMemberActivity;
import com.globopex.harimittioperator.network.DownloadImage;

import java.util.List;

/**
 * Created by Afnan on 05-Jul-18.
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchListViewHolder> {

    private Activity activity;
    private List<SearchList> searchLists;

    public SearchListAdapter(Activity activity, List<SearchList> searchLists) {
        this.activity = activity;
        this.searchLists = searchLists;
    }

    public class SearchListViewHolder extends RecyclerView.ViewHolder {

        public CardView cv_searchList_card;
        public ImageView iv_searchList_card;
        public TextView tv_id_searchListCard, tv_name_searchListCard, tv_address_searchListCard;

        public SearchListViewHolder(View view) {
            super(view);

            cv_searchList_card = (CardView) view.findViewById(R.id.cv_maintainer_card);
            iv_searchList_card = (ImageView) view.findViewById(R.id.iv_maintainer_card);
            tv_id_searchListCard = (TextView) view.findViewById(R.id.tv_id_mainCard);
            tv_name_searchListCard = (TextView) view.findViewById(R.id.tv_name_mainCard);
            tv_address_searchListCard = (TextView) view.findViewById(R.id.tv_address_mainCard);
        }
    }

    @Override
    public SearchListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.maintainer_card, parent, false);

        return new SearchListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchListViewHolder holder, final int position) {

        DownloadImage downloadImage = new DownloadImage();
        Typeface custom_font_Regular = Typeface.createFromAsset(activity.getAssets(), "fonts/Pluto Regular.ttf");
        Typeface custom_font_Bold = Typeface.createFromAsset(activity.getAssets(), "fonts/Pluto Bold.ttf");
        final SearchList searchList = searchLists.get(position);

        if (!searchList.getImg_url().equals("")) {
            downloadImage.setDownloadImage(activity, holder.iv_searchList_card, searchList.getImg_url());
        } else {
            holder.iv_searchList_card.setImageResource(R.drawable.ic_user);
        }

        if (searchList.getActivity_name().equals("Member")) {
            holder.tv_id_searchListCard.setVisibility(View.VISIBLE);
            holder.tv_id_searchListCard.setText("Member Id: " + searchList.getId());
            holder.tv_id_searchListCard.setTypeface(custom_font_Regular);
        }

        holder.tv_name_searchListCard.setText(searchList.getName());
        holder.tv_name_searchListCard.setTypeface(custom_font_Bold);

        holder.tv_address_searchListCard.setText(searchList.getAddress());
        holder.tv_address_searchListCard.setTypeface(custom_font_Regular);

        holder.cv_searchList_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchList.getActivity_name().equals("AdminHomeActivity")) {
                    Intent intent = new Intent(activity, AllotmentListActivity.class);
                    Bundle bundle = new Bundle();
                    // passing array index
                    intent.putExtra("maintainer_id", searchList.getId());
                    intent.putExtras(bundle);
                    activity.startActivity(intent);

                } else if (searchList.getActivity_name().equals("AdminHomeActivityForMaintainer")) {
                    Intent intent = new Intent(activity, UpdateMaintainerActivity.class);
                    Bundle bundle = new Bundle();
                    // passing array index
                    bundle.putString("maintainer_id", searchList.getId());
                    bundle.putString("name", searchList.getName());
                    bundle.putString("address", searchList.getAddress());
                    bundle.putString("contact_no", searchList.getContact_no());
                    bundle.putString("email", searchList.getEmail());
                    bundle.putString("password", searchList.getPassword());
                    bundle.putString("gcm_reg", searchList.getGcm_reg());
                    bundle.putString("img_url", searchList.getImg_url());
                    intent.putExtras(bundle);
                    activity.startActivity(intent);

                } else if (searchList.getActivity_name().equals("Member")) {
                    Intent intent = new Intent(activity, UpdateMemberActivity.class);
                    Bundle bundle = new Bundle();
                    // passing array index
                    bundle.putString("membership_id", searchList.getId());
                    bundle.putString("name", searchList.getName());
                    bundle.putString("address", searchList.getAddress());
                    bundle.putString("contact_no", searchList.getContact_no());
                    bundle.putString("email", searchList.getEmail());
                    bundle.putString("password", searchList.getPassword());
                    bundle.putString("gcm_reg", searchList.getGcm_reg());
                    bundle.putString("img_url", searchList.getImg_url());
                    intent.putExtras(bundle);
                    activity.startActivity(intent);

                } else if (searchList.getActivity_name().equals("Admin")) {
                    Intent intent = new Intent(activity, UpdateAdminActivity.class);
                    Bundle bundle = new Bundle();
                    // passing array index
                    bundle.putString("admin_id", searchList.getId());
                    bundle.putString("name", searchList.getName());
                    bundle.putString("address", searchList.getAddress());
                    bundle.putString("contact_no", searchList.getContact_no());
                    bundle.putString("email", searchList.getEmail());
                    bundle.putString("password", searchList.getPassword());
                    bundle.putString("gcm_reg", searchList.getGcm_reg());
                    bundle.putString("img_url", searchList.getImg_url());
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return searchLists.size();
    }

}
