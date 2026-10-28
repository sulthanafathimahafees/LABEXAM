
package com.example.busfarecalculator;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etDistance, etRate, etPassengers, etDeleteId;
    Button btnCalculate, btnViewRecords, btnDeleteRecord;
    TextView tvResult, tvRecords;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDistance = findViewById(R.id.etDistance);
        etRate = findViewById(R.id.etRate);
        etPassengers = findViewById(R.id.etPassengers);
        etDeleteId = findViewById(R.id.etDeleteId);

        btnCalculate = findViewById(R.id.btnCalculate);
        btnViewRecords = findViewById(R.id.btnViewRecords);
        btnDeleteRecord = findViewById(R.id.btnDeleteRecord);

        tvResult = findViewById(R.id.tvResult);
        tvRecords = findViewById(R.id.tvRecords);

        databaseHelper = new DatabaseHelper(this);

        btnCalculate.setOnClickListener(v -> calculateFare());

        btnViewRecords.setOnClickListener(v -> displayRecords());

        btnDeleteRecord.setOnClickListener(v -> {
            String idText = etDeleteId.getText().toString().trim();

            if (idText.isEmpty()) {
                Toast.makeText(this, "Enter a record ID",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int id = Integer.parseInt(idText);

                if (id <= 0) {
                    Toast.makeText(this, "Enter a valid record ID",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (databaseHelper.deleteRecord(id)) {
                    Toast.makeText(this, "Record deleted successfully",
                            Toast.LENGTH_SHORT).show();
                    etDeleteId.setText("");
                    displayRecords();
                } else {
                    Toast.makeText(this, "Record ID not found",
                            Toast.LENGTH_SHORT).show();
                }

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter a valid record ID",
                        Toast.LENGTH_SHORT).show();
            }
        });

        displayRecords();
    }

    private void calculateFare() {
        String distanceText = etDistance.getText().toString().trim();
        String rateText = etRate.getText().toString().trim();
        String passengersText = etPassengers.getText().toString().trim();

        if (distanceText.isEmpty() || rateText.isEmpty()
                || passengersText.isEmpty()) {
            Toast.makeText(this, "Please enter all details",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double distance = Double.parseDouble(distanceText);
            double rate = Double.parseDouble(rateText);
            int passengers = Integer.parseInt(passengersText);

            if (distance <= 0 || rate <= 0 || passengers <= 0) {
                Toast.makeText(this, "Enter positive values",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            double farePerPassenger = distance * rate;
            double totalFare = farePerPassenger * passengers;

            tvResult.setText(
                    "Fare per passenger: ₹"
                            + String.format("%.2f", farePerPassenger)
                            + "\nTotal fare: ₹"
                            + String.format("%.2f", totalFare));

            boolean saved = databaseHelper.insertFare(
                    distance, rate, passengers, totalFare);

            if (saved) {
                Toast.makeText(this, "Fare saved successfully",
                        Toast.LENGTH_SHORT).show();
                displayRecords();
            } else {
                Toast.makeText(this, "Could not save fare",
                        Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid numeric values",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void displayRecords() {
        Cursor cursor = databaseHelper.getAllRecords();
        StringBuilder records = new StringBuilder();

        try {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(
                            cursor.getColumnIndexOrThrow("id"));
                    double distance = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("distance"));
                    double rate = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("rate"));
                    int passengers = cursor.getInt(
                            cursor.getColumnIndexOrThrow("passengers"));
                    double total = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("total"));

                    records.append("Record ID: ").append(id)
                            .append("\nDistance: ").append(distance)
                            .append(" km\nRate: ₹").append(rate)
                            .append("\nPassengers: ").append(passengers)
                            .append("\nTotal: ₹")
                            .append(String.format("%.2f", total))
                            .append("\n--------------------\n");

                } while (cursor.moveToNext());

                tvRecords.setText(records.toString());
            } else {
                tvRecords.setText("No saved records.");
            }
        } finally {
            cursor.close();
        }
    }
}
