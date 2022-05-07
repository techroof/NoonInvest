package com.techroof.nooninvest.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techroof.nooninvest.ModelClass.Notifications;
import com.techroof.nooninvest.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.ViewAdapter> {
    private static final String TAG = "RecyclerViewAdapter";
    public String flag = "true";

    private ArrayList<Notifications> notificationsArrayList;
    private Context context;



    public NotificationRecyclerViewAdapter(ArrayList<Notifications> notificationsArrayList, Context context) {
        this.notificationsArrayList = notificationsArrayList;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: 1");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_recyclerview_notifications, parent, false);
        return new ViewAdapter(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAdapter holder, int position) {


        Notifications ld = notificationsArrayList.get(position);


            Log.d(TAG, "onBindViewHolder: called");
            holder.textViewname.setText("Your " +ld.getProfitedAmount()+ " amount has been profited");


    }


    @Override
    public int getItemCount() {
        return notificationsArrayList.size();
    }

    public class ViewAdapter extends RecyclerView.ViewHolder {
        TextView  textViewname;
        CardView cardViewview;

        public ViewAdapter(@NonNull View itemView) {
            super(itemView);

            textViewname=itemView.findViewById(R.id.tv_name_notifiations);
            cardViewview = itemView.findViewById(R.id.parentlayout_notifications);

        }
    }
}
