package com.fyp.motorcyclefix.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.motorcyclefix.Dao.WorkshopDao;
import com.fyp.motorcyclefix.R;

import java.util.ArrayList;
import java.util.List;

public class WorkshopsAdapter extends RecyclerView.Adapter<WorkshopsAdapter.CardViewHolder> implements Filterable {

    private List<WorkshopDao> workshopDaos;
    private List<WorkshopDao> workshopList;
    private WorkshopsAdapter.OnItemClickListener itemClickListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(WorkshopsAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public ImageView workshopImg;
        public TextView workshopName;
        public TextView speciality;

        public CardViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            workshopImg = itemView.findViewById(R.id.workshopImg);
            workshopName = itemView.findViewById(R.id.workshopName);
            speciality = itemView.findViewById(R.id.specialized);

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

    public WorkshopsAdapter(List<WorkshopDao> workshopDaos) {
        this.workshopDaos = workshopDaos;
        workshopList = new ArrayList<>(workshopDaos);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.rider_workshop_recycler, parent, false);

        return new CardViewHolder(view, itemClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        WorkshopDao currentItem = workshopDaos.get(position);

        holder.workshopImg.setImageResource(currentItem.getWorkshopImg());
        holder.workshopName.setText(currentItem.getWorkshopName());
        holder.speciality.setText(currentItem.getSpecalized());

    }

    @Override
    public int getItemCount() {

        return workshopDaos.size();
    }


    public Filter getFilter() {
        return workshopFilter;
    }

    private Filter workshopFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<WorkshopDao> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(workshopList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (WorkshopDao item : workshopList) {
                    if (item.getSpecalized().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            workshopDaos.clear();
            workshopDaos.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}