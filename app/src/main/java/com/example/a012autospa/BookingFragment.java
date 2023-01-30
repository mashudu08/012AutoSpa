package com.example.a012autospa;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BookingFragment extends Fragment {
    private EditText txtCarModel, txtCarReg, txtContactNum;
    private Spinner washType;
    private TextView txtDate, txtTime;
    private Button btnDate, btnTime, btnSaveBooking;
    private CheckBox extra1, extra2, extra3;
    private ImageView back;
    private FirebaseUser user;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private double washPrice;
    private double extraPrice;
    private String extraText = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =   inflater.inflate(R.layout.fragment_booking, container, false);

        txtCarModel = (EditText) rootView.findViewById(R.id.txtCarModel);
        txtCarReg = (EditText) rootView.findViewById(R.id.txtCarReg);
        txtContactNum = (EditText) rootView.findViewById(R.id.txtContactNum);
        washType = (Spinner) rootView.findViewById(R.id.spnWashType);
        txtDate = (TextView) rootView.findViewById(R.id.txtDate);
        btnDate = (Button) rootView.findViewById(R.id.btnDate);
        txtTime = (TextView) rootView.findViewById(R.id.txtTime);
        btnTime = (Button) rootView.findViewById(R.id.btnTime);
        btnSaveBooking = (Button) rootView.findViewById(R.id.btnSaveBooking);
        back = rootView.findViewById(R.id.homeBack);
        extra1 = rootView.findViewById(R.id.extra1);
        extra2 = rootView.findViewById(R.id.extra2);
        extra3 = rootView.findViewById(R.id.extra3);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new HomeFragment()).commit();
            }
        });

        String[] type = new String[]{"-","Wash & Go", "Wash & Go (SUV)", "Full House", "Full House (SUV)", "Wash & Polish", "Engine Wash", "Valet"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, type);
        washType.setAdapter(adapter);

        washType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoryNames = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

        //onclick a time dialog shows
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hourOfDay = 8;
                int minuteOfDay = 0;
                TimePickerDialog dialog = new TimePickerDialog(container.getContext(), R.style.DateNTimePickerTheme,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        setTime(String.valueOf(i), i1);
                    }
                }, hourOfDay, minuteOfDay, true);
                dialog.show();
            }
        });

        //onclick a date dialog shows
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get current date
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(container.getContext(), R.style.DateNTimePickerTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        txtDate.setText(dayOfMonth + "/"+ (month + 1) +"/"+ year);
                    }
                }, year, month, dayOfMonth);

                dialog.show();
            }
        });

        // Saves booking information when you confirm
        btnSaveBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showing process dialog
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Saving booking details...");
                progressDialog.setTitle("New Booking");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                try {
                    confirmBooking();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    private void confirmBooking() throws ParseException {
        //Get all values
        String carModel = txtCarModel.getText().toString();
        String carReg = txtCarReg.getText().toString();
        String contactNum = txtContactNum.getText().toString();
        String spnWashType = washType.getSelectedItem().toString();
        String time = txtTime.getText().toString();
        String date = txtDate.getText().toString();

        try {
            if(carModel.isEmpty()){
                txtCarModel.setError("Field Required!");
                progressDialog.dismiss();
            }
            else if (carReg.isEmpty()){
                txtCarReg.setError("Field Required!");
                progressDialog.dismiss();
            }
            else if (contactNum.isEmpty()){
                txtContactNum.setError("Field Required!");
                progressDialog.dismiss();
            }
            else if (spnWashType.equals("-")){
                ((TextView) washType.getSelectedView()).setError("Field Required!");
                progressDialog.dismiss();
            }
            else if (date.equals("") || date.contains("date")){
                txtDate.setText("*Please choose a date");
                //txtDate.setTextColor(Color.RED);
                progressDialog.dismiss();
            }
            else if (time.equals("") || time.contains("time")){
                txtTime.setText("*Please choose a time");
                //txtTime.setTextColor(Color.RED);
                progressDialog.dismiss();
            }
            else if(validateTime(time) == true && validateDate(date) == true) {
                // database instance
                user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();
                FirebaseDatabase db = com.google.firebase.database.FirebaseDatabase.getInstance();
                DatabaseReference root  = db.getReference().child(userId).child("bookings");
                FirebaseDatabase.getInstance().getReference().keepSynced(true);
                FirebaseAuth auth = FirebaseAuth.getInstance();

                root.child("bookings").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();

                        //assigning price according to chosen wash
                        double price = getPrice(spnWashType) + extras();

                        String extras = extraString(extraText);

                        // Created a collection called booking (From "BookingClass" class) and have the unique user as the child of the collection
                        BookingClass bookingDetails = new BookingClass(carModel, carReg, contactNum, spnWashType, date, time, price, extras);

                        //creates a unique id for booking under each user
                        String bookingId = root.push().getKey();
                        root.child(bookingId).setValue(bookingDetails);

                        Toast.makeText(getActivity(), "Booking details saved!", Toast.LENGTH_SHORT).show();
                        bookingOverview(carModel, carReg, spnWashType, date, time, price, extras);

                        //setting all input values to null
                        txtCarModel.setText("");
                        txtCarReg.setText("");
                        txtContactNum.setText("");
                        txtDate.setText("");
                        txtTime.setText("");
                        extraPrice = 0;
                        extraText = "";
                        extra1.setChecked(false);
                        extra2.setChecked(false);
                        extra3.setChecked(false);
                        washType.setSelection(0);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
        catch (Exception e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void bookingOverview(String carModel, String carReg, String spnWashType, String date, String time, double price, String extras) {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View bkOverview = getLayoutInflater().inflate(R.layout.booking_overviews, null);

        TextView model = bkOverview.findViewById(R.id.model);
        TextView reg = bkOverview.findViewById(R.id.reg);
        TextView washType = bkOverview.findViewById(R.id.type);
        TextView txtDate = bkOverview.findViewById(R.id.date);
        TextView txtTime = bkOverview.findViewById(R.id.time);
        TextView extra = bkOverview.findViewById(R.id.extra);
        TextView txtPrice = bkOverview.findViewById(R.id.price);
        Button ok = bkOverview.findViewById(R.id.btnOk);
        ImageView close = bkOverview.findViewById(R.id.back);
        double total = price;

        model.setText(carModel);
        reg.setText(carReg);
        washType.setText(spnWashType);
        txtDate.setText(date);
        txtTime.setText(time);
        extra.setText(extras);
        txtPrice.setText("R " + total);

        dialogBuilder.setView(bkOverview);
        dialog = dialogBuilder.create();
        dialog.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private double extras() {
        if(extra1.isChecked()){
            extraPrice += 50;
            extraText += extra1.getText().toString() + "\n";
            extraString(extraText);
        }
        if(extra2.isChecked()){
            extraPrice += 25;
            extraText += extra2.getText().toString() + "\n";
            extraString(extraText);
        }
        if(extra3.isChecked()){
            extraPrice += 60;
            extraText += extra2.getText().toString() + "\n";
            extraString(extraText);
        }
        if (!extra1.isChecked() && !extra2.isChecked() && !extra3.isChecked()){
            extraPrice = 0;
            extraString("none");
        }
        return extraPrice;
    }

    private String extraString(String extraText) {
        return extraText;
    }

    private double getPrice(String spnWashType) {
        if(spnWashType.equals("Wash & Go")){
            washPrice = 50;
        }
        else if(spnWashType.equals("Wash & Go (SUV)")){
            washPrice = 70;
        }
        else if(spnWashType.equals("Full House")){
            washPrice = 80;
        }
        else if(spnWashType.equals("Full House (SUV)")){
            washPrice = 100;
        }
        else if(spnWashType.equals("Wash & Polish")){
            washPrice = 180;
        }
        else if(spnWashType.equals("Engine Wash")){
            washPrice = 90;
        }
        else if(spnWashType.equals("Valet")){
            washPrice = 450;
        }

        return washPrice;
    }

    private boolean validateTime(String time) {
        String openTime = "8:00";
        String closingTime = "16:30";
        int comparison = time.compareTo(openTime);
        int comparison2 = time.compareTo(closingTime);

        //validating the date (only within operating hours)
        if(comparison < 0 && comparison2 > 0){
            txtTime.setText("*Choose valid time");
            Toast.makeText(getActivity(), "Time not within operating hours" , Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return false;
        }
        else {
            return true;
        }
    }

    private boolean validateDate(String date) throws ParseException {
        //https://droidbyme.medium.com/compare-date-calendar-or-string-date-c6368d57119e

        //getting current date
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = df.format(Calendar.getInstance().getTime());

        //converting strings to date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date1= sdf.parse(currentDate);
        Date date2 = sdf.parse(date);

        //validating the date (only current date upwards)
        if(date2.compareTo(date1) < 0){
            txtDate.setText("*Choose valid date");
            Toast.makeText(getActivity(), "Date is not valid" , Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return false;
        }
        else {
            return true;
        }
    }

    //Time formatting
    void setTime(String hours, int minutes)
    {
        String strMinutes ="";

        if (minutes < 10) {
            strMinutes = "0" + minutes   ;
        }
        else {
            strMinutes = String.valueOf(minutes);
        }
        txtTime.setText(hours + ":" + strMinutes);
    }
}