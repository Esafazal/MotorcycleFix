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
    //variables
    private List<WorkshopDao> workshopDaos;
    private List<WorkshopDao> workshopList;
    private WorkshopsAdapter.OnItemClickListener itemClickListener;

    //interface for item click listner with one method
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(WorkshopsAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        //variables for widgets
        public ImageView workshopImg;
        public TextView workshopName;
        public TextView speciality;

        public CardViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            //initilization
            workshopImg = itemView.findViewById(R.id.workshopImg);
            workshopName = itemView.findViewById(R.id.workshopName);
            speciality = itemView.findViewById(R.id.specialized);
            //click listner impl
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
    //constructor
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
        //return item size
        return workshopDaos.size();
    }


    public Filter getFilter() {
        return workshopFilter;
    }
    //adapter method filters list of workshops based on the make, model specilization or workshop name
    private Filter workshopFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<WorkshopDao> filteredList = new ArrayList<>();
            //get workshop arrayList to filter if constraint isn't null
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(workshopList);
            } else {
                //if null, convert user input to lowercaase  and revmove spaces
                String filterPattern = constraint.toString().toLowerCase().trim();
                //loop workshopList and filter according to user input String
                for (WorkshopDao item : workshopList) {
                    //checks for filter pattern of make, model and workshop name
                    if (item.getSpecalized().toLowerCase().contains(filterPattern)
                            || item.getWorkshopName().toLowerCase().contains(filterPattern)) {
                        //add the list of matching workshops to the filteredList object and display user
                        filteredList.add(item);
                    }
                }
            }
            //create a new object of the filterResults and pass in filtered list
            FilterResults results = new FilterResults();
            results.values = filteredList;
            //send results to the adapter
            return results;
        }

        //method to publish filtered results to adapter
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //empty the list
            workshopDaos.clear();
            //add new list of results
            workshopDaos.addAll((List) results.values);
            //method call to notify change
            notifyDataSetChanged();
        }
    };
}