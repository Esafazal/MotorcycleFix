package com.fyp.motorcyclefix.Patterns;

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
    private AcceptedBookingsAdapter.OnItemClickListener itemClickListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AcceptedBookingsAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    //created a static class inheriting recyclerview.viewholder
    public static class BookingsCardviewHolder extends RecyclerView.ViewHolder{
        public TextView riderName, bikeMakeNModel, sType, sDate, sDetail, bookingId;
        public Button startBtn, endBtn, sendNoteBtn;
        public EditText note;

        public BookingsCardviewHolder(@NonNull View itemView) {
            super(itemView);
            riderName = itemView.findViewById(R.id.bookingRiderName);
            bikeMakeNModel = itemView.findViewById(R.id.bookingBikeModel);
            sType = itemView.findViewById(R.id.serviceTypeDynamic);
            sDate = itemView.findViewById(R.id.serviceDateDynamic);
            sDetail = itemView.findViewById(R.id.repairDetialDynamic);
            note = itemView.findViewById(R.id.bookingLeaveNote);
            sendNoteBtn = itemView.findViewById(R.id.btnLeaveNote);
            startBtn = itemView.findViewById(R.id.bookingStartService);
            endBtn = itemView.findViewById(R.id.bookingCompleteService);
            bookingId = itemView.findViewById(R.id.bookingIdDynamic);
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

        BookingsCardviewHolder bookingsCardviewHolder = new BookingsCardviewHolder(view);

        return bookingsCardviewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookingsCardviewHolder holder, int position) {

        Booking booking = mBookings.get(position);

        holder.bikeMakeNModel.setText(booking.getVehicleId());
        holder.riderName.setText(booking.getUserId());
        holder.sType.setText(booking.getServiceType());
        holder.sDate.setText(booking.getDateOfService());
        holder.sDetail.setText(booking.getRepairDescription());
        holder.bookingId.setText(String.valueOf(booking.getBookingID()));
    }

    @Override
    public int getItemCount() {
        return mBookings.size();
    }
}
