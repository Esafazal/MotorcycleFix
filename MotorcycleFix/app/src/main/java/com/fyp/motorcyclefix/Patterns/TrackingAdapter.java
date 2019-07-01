package com.fyp.motorcyclefix.Patterns;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.motorcyclefix.Dao.TrackingDao;
import com.fyp.motorcyclefix.R;

import java.util.ArrayList;

public class TrackingAdapter extends RecyclerView.Adapter<TrackingAdapter.CardViewHolder> {

    private ArrayList<TrackingDao> trackingDaos;

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public TextView bookingID;
        public TextView serviceType;
        public TextView bikeModel;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingID = itemView.findViewById(R.id.bookingID);
            serviceType = itemView.findViewById(R.id.serviceType);
            bikeModel = itemView.findViewById(R.id.bikeModel);
        }
    }

    public TrackingAdapter(ArrayList<TrackingDao> trackingDaos) {

        this.trackingDaos = trackingDaos;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rider_tracking_recycler_cardview, parent, false);

        CardViewHolder cardViewHolder = new CardViewHolder(view);

        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        TrackingDao trackingDao = trackingDaos.get(position);

        holder.bookingID.setText(trackingDao.getBookingID());
        holder.serviceType.setText(trackingDao.getServiceType());
        holder.bikeModel.setText(trackingDao.getBikeModel());
    }

    @Override
    public int getItemCount() {
        return trackingDaos.size();
    }
}


