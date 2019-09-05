package com.fyp.motorcyclefix.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fyp.motorcyclefix.Dao.AcceptedBooking;
import com.fyp.motorcyclefix.Dao.Booking;
import com.fyp.motorcyclefix.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PastBookingsAdapter extends RecyclerView.Adapter<PastBookingsAdapter.BookingsCardviewHolder> {

    private ArrayList<AcceptedBooking> mBookings;
    private PastBookingsAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(PastBookingsAdapter.OnItemClickListener listener){
        mListener = listener;
    }

    //created a static class inheriting recyclerview.viewholder
    public static class BookingsCardviewHolder extends RecyclerView.ViewHolder{

        public TextView riderName, bikeMakeNModel, sType, sDate,
                sDescriptionD, bookingId, sDescriptionS, rRepairS, rRepairD, riderNo;
        public EditText editNote;
        public RatingBar rating;

        public BookingsCardviewHolder(@NonNull View itemView, final PastBookingsAdapter.OnItemClickListener listener) {
            super(itemView);
            riderName = itemView.findViewById(R.id.reqRiderNamePast);
            bikeMakeNModel = itemView.findViewById(R.id.reqBikeModelPast);
            sType = itemView.findViewById(R.id.reqSTypeDynamicPast);
            sDate = itemView.findViewById(R.id.reqSDateDynamicPast);
            sDescriptionD = itemView.findViewById(R.id.reqRepairDetialDynamicPast);
            editNote = itemView.findViewById(R.id.bookingLeaveNotePast);
            bookingId = itemView.findViewById(R.id.bookingIdDynamicPast);
            riderNo = itemView.findViewById(R.id.riderNumberD);
            sDescriptionS = itemView.findViewById(R.id.repairDetailStatic);
            rRepairS = itemView.findViewById(R.id.acceptedRepairCatStat);
            rRepairD = itemView.findViewById(R.id.acceptedRepairCatDynamicPast);
            rating = itemView.findViewById(R.id.rating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public PastBookingsAdapter(ArrayList<AcceptedBooking> bookings) {
        mBookings = bookings;
    }

    @NonNull
    @Override
    public BookingsCardviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mechanic_past_booking_cardview, parent, false);
        BookingsCardviewHolder bookingsCardviewHolder = new BookingsCardviewHolder(view, mListener);

        return bookingsCardviewHolder;
    }
    //passing values to individual card view in thre recycler
    @Override
    public void onBindViewHolder(@NonNull PastBookingsAdapter.BookingsCardviewHolder holder, int position) {
        AcceptedBooking booking = mBookings.get(position);
        String category = booking.getRepairCategory();
        String description = booking.getRepairDescription();
        String status = booking.getStatus();
        try {//null check category value
            if (category != null && category.length() > 2){
                holder.rRepairS.setVisibility(View.VISIBLE);
                holder.rRepairD.setVisibility(View.VISIBLE);
                holder.rRepairD.setText(category);
            }//null check description value
            if (description != null && description.length() > 2) {
                holder.sDescriptionS.setVisibility(View.VISIBLE);
                holder.sDescriptionD.setVisibility(View.VISIBLE);
                holder.sDescriptionD.setText(description);
            }

            if(booking.getMessage() != null){
                holder.editNote.setVisibility(View.VISIBLE);
                holder.editNote.setEnabled(false);
                holder.editNote.setText(booking.getMessage());
            }
        }
        catch (Exception e){
            Log.d("acceptedBookings", "Booking: "+e.toString());
        }
        String date1 = String.valueOf(booking.getDate());
        String date = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("E dd MMM", Locale.ENGLISH);
            Date d = new Date(date1);
            date = dateFormat.format(d);
        } catch (Exception e){
            e.printStackTrace();
        }
        holder.bikeMakeNModel.setText(booking.getBikeMakeModel());
        holder.riderName.setText(booking.getRiderName());
        holder.sType.setText(booking.getServiceType());
        holder.sDate.setText(date);
        holder.bookingId.setText(String.valueOf(booking.getBookingNo()));
        holder.riderNo.setText("+94"+booking.getRiderNumber());
        holder.rating.setRating(booking.getRating());
    }

    @Override
    public int getItemCount() {
        return mBookings.size();
    }
}