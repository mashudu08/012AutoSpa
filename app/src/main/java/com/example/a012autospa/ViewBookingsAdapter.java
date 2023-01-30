package com.example.a012autospa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewBookingsAdapter extends RecyclerView.Adapter<ViewBookingsAdapter.ImageViewHolder> {
    private Context mContext;
    private ArrayList<BookingClass> mListings;

    private ViewBookingsAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(ViewBookingsAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public ViewBookingsAdapter(Context context, ArrayList<BookingClass> listings){
        mContext = context;
        mListings = listings;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(mContext).inflate(R.layout.list_bookings, parent, false);
        return new ImageViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        BookingClass viewBooking = mListings.get(position);
        holder.txtModel.setText(viewBooking.getCarModel());
        holder.txtReg.setText(viewBooking.getCarReg());
        holder.txtType.setText(viewBooking.getWashType());
        holder.txtDate.setText(viewBooking.getDate());
        holder.txtTime.setText(viewBooking.getTime());
        holder.txtPrice.setText("R " + String.valueOf(viewBooking.getPrice()));
    }

    @Override
    public int getItemCount() {
        return mListings.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView txtModel, txtReg, txtType, txtDate, txtPrice, txtTime;

        public ImageViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            txtModel = itemView.findViewById(R.id.model);
            txtReg = itemView.findViewById(R.id.reg);
            txtType = itemView.findViewById(R.id.washType);
            txtDate = itemView.findViewById(R.id.date);
            txtPrice = itemView.findViewById(R.id.price);
            txtTime = itemView.findViewById(R.id.time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null){
                        if (position != RecyclerView.NO_POSITION);{

                            listener.onItemClick(position);

                        }
                    }

                }
            });
        }
    }
}
