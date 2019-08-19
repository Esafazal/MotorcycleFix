package com.fyp.motorcyclefix.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.motorcyclefix.Dao.Booking;
import com.fyp.motorcyclefix.R;

import java.util.ArrayList;

public class AcceptedBookingsAdapter extends RecyclerView.Adapter<AcceptedBookingsAdapter.BookingsCardviewHolder> {

    private ArrayList<Booking> mBookings;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onEditNote(int position, Button sendNote);
        void onStartServiceClick(int position, Button completeService, Button startService);
        void onCompleteServiceClick(int position, Button startService, Button completeService);
        void onSendNoteClick(int position, EditText editNote);


    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    //created a static class inheriting recyclerview.viewholder
    public static class BookingsCardviewHolder extends RecyclerView.ViewHolder{

        public TextView riderName, bikeMakeNModel, sType, sDate, sDescriptionD, bookingId, sDescriptionS, rRepairS, rRepairD;
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
//            sDescriptionS = itemView.findViewById(R.id.repairDetailStatic);
//            rRepairS = itemView.findViewById(R.id.acceptedRepairCatStat);
//            rRepairD = itemView.findViewById(R.id.acceptedRepairCatDynamic);

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
                            listener.onSendNoteClick(position, editNote);
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

    public AcceptedBookingsAdapter(ArrayList<Booking> bookings) {
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

    @Override
    public void onBindViewHolder(@NonNull BookingsCardviewHolder holder, int position) {

        Booking booking = mBookings.get(position);

//        String category = "";
//        String description = "";
//        category = booking.getRepairCategory();
//        description = booking.getRepairDescription();
//
//        try {
//            if (!category.equals("")){
//                holder.rRepairS.setVisibility(View.VISIBLE);
//                holder.rRepairD.setVisibility(View.VISIBLE);
//            }
//            if (!description.equals("")) {
//                holder.sDescriptionS.setVisibility(View.VISIBLE);
//                holder.sDescriptionD.setVisibility(View.VISIBLE);
//            }
//        }
//        catch (Exception e){
//            Log.d("acceptedBookings", "Booking: "+e.toString());
//        }

        holder.bikeMakeNModel.setText(booking.getVehicleId());
        holder.riderName.setText(booking.getUserId());
        holder.sType.setText(booking.getServiceType());
        holder.sDate.setText(booking.getDateOfService());
        holder.sDescriptionD.setText(booking.getRepairDescription());
        holder.bookingId.setText(String.valueOf(booking.getBookingID()));
    }

    @Override
    public int getItemCount() {
        return mBookings.size();
    }
}
