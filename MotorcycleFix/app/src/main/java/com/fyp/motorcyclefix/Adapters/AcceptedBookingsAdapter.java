package com.fyp.motorcyclefix.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.motorcyclefix.Dao.AcceptedBooking;
import com.fyp.motorcyclefix.Dao.Booking;
import com.fyp.motorcyclefix.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AcceptedBookingsAdapter extends RecyclerView.Adapter<AcceptedBookingsAdapter.BookingsCardviewHolder> {

    private ArrayList<AcceptedBooking> mBookings;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onEditNote(int position, Button sendNote);
        void onStartServiceClick(int position, Button completeService, Button startService);
        void onCompleteServiceClick(int position, Button startService, Button completeService);
        void onSendNoteClick(int position, EditText editNote, Button sendBtn);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    //created a static class inheriting recyclerview.viewholder
    public static class BookingsCardviewHolder extends RecyclerView.ViewHolder{

        public TextView riderName, bikeMakeNModel, sType, sDate,
                sDescriptionD, bookingId, sDescriptionS, rRepairS, rRepairD, riderNo;
        public EditText editNote;
        public Button sendNote, startService, completeService;

        public BookingsCardviewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            riderName = itemView.findViewById(R.id.reqRiderName);
            bikeMakeNModel = itemView.findViewById(R.id.reqBikeModel);
            sType = itemView.findViewById(R.id.reqSTypeDynamic);
            sDate = itemView.findViewById(R.id.reqSDateDynamic);
            sDescriptionD = itemView.findViewById(R.id.reqRepairDetialDynamic);
            editNote = itemView.findViewById(R.id.bookingLeaveNote);
            sendNote = itemView.findViewById(R.id.btnLeaveNote);
            startService = itemView.findViewById(R.id.bookingDeclineBooking);
            completeService = itemView.findViewById(R.id.bookingCompleteService);
            bookingId = itemView.findViewById(R.id.bookingIdDynamic);
            riderNo = itemView.findViewById(R.id.riderNumberD);
            sDescriptionS = itemView.findViewById(R.id.repairDetailStatic);
            rRepairS = itemView.findViewById(R.id.acceptedRepairCatStat);
            rRepairD = itemView.findViewById(R.id.acceptedRepairCatDynamic);

            startService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onStartServiceClick(position, completeService, startService);
                        }
                    }
                }
            });

            completeService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onCompleteServiceClick(position, startService, completeService);
                        }
                    }
                }
            });

            sendNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onSendNoteClick(position, editNote, sendNote);
                        }
                    }
                }
            });

            editNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onEditNote(position, sendNote);
                        }
                    }
                }
            });
        }
    }

    public AcceptedBookingsAdapter(ArrayList<AcceptedBooking> bookings) {
        mBookings = bookings;
    }

    @NonNull
    @Override
    public BookingsCardviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mechanic_accepted_bookings_fragment, parent, false);
        BookingsCardviewHolder bookingsCardviewHolder = new BookingsCardviewHolder(view, mListener);

        return bookingsCardviewHolder;
    }
    //passing values to individual card view in thre recycler
    @Override
    public void onBindViewHolder(@NonNull BookingsCardviewHolder holder, int position) {
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
            //if the status is in progress change the card view layout
            if(status.equals("progress")){
                holder.startService.setClickable(false);
                holder.startService.setBackgroundColor(Integer.valueOf(booking.getStartColor()));
                holder.completeService.setBackgroundColor(Integer.valueOf(booking.getEndColor()));
                holder.completeService.setClickable(true);
                holder.completeService.setEnabled(true);
            }
            if(booking.getMessage() != null){
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
    }

    @Override
    public int getItemCount() {
        return mBookings.size();
    }
}
