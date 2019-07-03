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

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.CardViewHolder> {

    private ArrayList<TrackingDao> trackingDaos;

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public TextView bikeModel;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            bikeModel = itemView.findViewById(R.id.bikeNameModel);
        }
    }

    public VehicleAdapter(ArrayList<TrackingDao> trackingDaos) {
        this.trackingDaos = trackingDaos;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rider_my_vehicles_fragment, parent, false);

        CardViewHolder cardViewHolder = new CardViewHolder(view);

        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        TrackingDao trackingDao = trackingDaos.get(position);

        holder.bikeModel.setText(trackingDao.getBikeModel());
    }

    @Override
    public int getItemCount() {

        return trackingDaos.size();
    }
}
