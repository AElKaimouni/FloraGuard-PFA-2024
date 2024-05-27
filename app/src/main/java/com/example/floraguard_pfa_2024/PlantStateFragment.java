package com.example.floraguard_pfa_2024;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.enums.Anchor;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlantStateFragment extends Fragment {

    private static final String TAG = "PlantStateFragment";
    private AnyChartView anychart;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FirebaseStorage storage;
    private static final String ARG_PLANT_NAME = "plant_name";
    private static final String ARG_IMAGE_URL = "image_url";
    private Button reload;
    private TextView plantNameTextView;


    private String plantName;
    private String imageUrl;
    private ImageView plantImageView;

    public PlantStateFragment() {
        // Required empty public constructor
    }

    public static PlantStateFragment newInstance(String plantName, String imageUrl) {
        PlantStateFragment fragment = new PlantStateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLANT_NAME, plantName);
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            plantName = getArguments().getString(ARG_PLANT_NAME);
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plant_state, container, false);
        anychart = view.findViewById(R.id.ChartView);
        reload = view.findViewById(R.id.monitoring);
        anychart.setProgressBar(view.findViewById(R.id.progress_bar));
        storage = FirebaseStorage.getInstance();

        setChatView();

        // Set plant name and image
        plantNameTextView = view.findViewById(R.id.txt_plant_name);
        plantImageView = view.findViewById(R.id.img_plant);

        plantNameTextView.setText(plantName);
        Glide.with(this).load(imageUrl).into(plantImageView);
        reload.setOnClickListener(v -> {
            goToMainActivity();
        });

        return view;
    }

    private void setChatView() {
        OnSuccessListener<String> onSuccessListener = new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String downloadUrl) {
                // Handle successful retrieval of download URL
                Log.d(TAG, "Download URL: " + downloadUrl);
            }
        };

        OnFailureListener onFailureListener = new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // Handle failure
                Log.e(TAG, "Error getting download URL: " + e.getMessage());
            }
        };

        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.title("Humidity and Temperature Trends");

        cartesian.yAxis(0).title("Value");
        cartesian.xAxis(0).title("Timestamp");

        getLastTenRecordsFromFirestore();

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anychart.setChart(cartesian);
    }

    public void getLastTenRecordsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("State")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Listen failed.", e);
                            return;
                        }

                        List<DocumentSnapshot> documents = snapshot.getDocuments();
                        updateChart(documents);
                    }
                });
    }


    private void updateChart(List<DocumentSnapshot> documents) {
        Cartesian cartesian = AnyChart.line();


        List<DataEntry> humidityEntries = new ArrayList<>();
        List<DataEntry> temperatureEntries = new ArrayList<>();

        for (DocumentSnapshot document : documents) {
            Timestamp timestamp = (Timestamp) document.get("timestamp");
            double humidity = Double.parseDouble(document.getString("h"));
            double temperature = Double.parseDouble(document.getString("t"));
            Date date = timestamp.toDate();

            // Extract seconds from the Date object
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int seconds = calendar.get(Calendar.SECOND);


            humidityEntries.add(new ValueDataEntry(seconds, humidity));
            temperatureEntries.add(new ValueDataEntry(seconds, temperature));
        }

        Line humiditySeries = cartesian.line(humidityEntries);
        humiditySeries.name("Humidity");
        humiditySeries.color("#FF5733");
        humiditySeries.hovered().markers().enabled(true);
        humiditySeries.tooltip().position("right").anchor(Anchor.LEFT_CENTER).offsetX(5d).offsetY(5d);

        Line temperatureSeries = cartesian.line(temperatureEntries);
        temperatureSeries.name("Temperature");
        temperatureSeries.color("#3366FF");
        temperatureSeries.hovered().markers().enabled(true);
        temperatureSeries.tooltip().position("right").anchor(Anchor.LEFT_CENTER).offsetX(5d).offsetY(5d);

        anychart.setChart(cartesian);
    }
    private void goToMainActivity() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);
        // If you want to remove this fragment from the back stack:
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
