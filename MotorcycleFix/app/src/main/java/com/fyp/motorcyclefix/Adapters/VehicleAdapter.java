package com.fyp.motorcyclefix.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.motorcyclefix.Dao.Vehicle;
import com.fyp.motorcyclefix.R;

import java.util.ArrayList;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.CardViewHolder> {

    private ArrayList<Vehicle> vehicleDaos;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public TextView bikeModel;

        public CardViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            bikeModel = itemView.findViewById(R.id.bikeNameModel);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public VehicleAdapter(ArrayList<Vehicle> vehicleDaos) {
        this.vehicleDaos = vehicleDaos;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rider_my_vehicles_fragment, parent, false);

        CardViewHolder cardViewHolder = new CardViewHolder(view, itemClickListener);

        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Vehicle vehicle = vehicleDaos.get(position);

        holder.bikeModel.setText(vehicle.getManufacturer()+" "+ vehicle.getModel());
    }

    @Override
    public int getItemCount() {

        return vehicleDaos.size();
    }
}
