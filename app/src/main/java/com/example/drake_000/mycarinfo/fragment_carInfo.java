package com.example.drake_000.mycarinfo;

/**
 * Created by drake_000 on 12/2/2017.
 */
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.List;

public class fragment_carInfo extends Fragment {
    TextView makeField;
    TextView modelField;
    TextView yearField;
    TextView powerField;
    TextView countryField;
    TextView carTitleField;
    TextView typeField;

    private GoogleMap map;

    Handler handler;

    public fragment_carInfo() {
        handler = new Handler();
    }

    private LatLng getLocationFromAddress(String strAddress) {
    //pobiera koordynaty na bazie adresu
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

            return p1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_carinfo, container, false);
        makeField = (TextView) rootView.findViewById(R.id.make_field);
        modelField = (TextView) rootView.findViewById(R.id.model_field);
        yearField = (TextView) rootView.findViewById(R.id.year_field);
        powerField = (TextView) rootView.findViewById(R.id.power_field);
        countryField = (TextView) rootView.findViewById(R.id.country_field);
        carTitleField = (TextView) rootView.findViewById(R.id.car_title);
        typeField = (TextView) rootView.findViewById(R.id.car_type);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        String vin = args.getString("vin");
        updateCarData(vin);
    }

    public void updateCarData(final String vin) {
        new Thread() {
            public void run() {
                final JSONObject json = FetchInfo.getJSON(getActivity(), vin);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.vin_not_valid),
                                    Toast.LENGTH_LONG).show();
                            getFragmentManager().popBackStack();

                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderCar(json);
                        }
                    });
                }
            }
        }.start();
    }

    private String checkIfNull(String carInfoString) {
        if (carInfoString.equals("null")) {
            return getString(R.string.no_info_available);
        }
        return carInfoString;
    }

    public void renderCar(JSONObject json) {
        try {
            JSONArray results = json.getJSONArray("Results");

            JSONObject make = results.getJSONObject(5);
            JSONObject model = results.getJSONObject(7);
            JSONObject year = results.getJSONObject(8);
            JSONObject power = results.getJSONObject(69);
            JSONObject country = results.getJSONObject(13);
            JSONObject city = results.getJSONObject(9);
            JSONObject state = results.getJSONObject(15);
            JSONObject type = results.getJSONObject(22); //karoseria

            // sprawdzamy czy wszystkie pola są jakimś cudem puste i nie wszedl nam exception JSONowy - jesli tak to wyrzucamy blad bo nie ma niczego do wyświetlenia użytecznego
            if (make.getString("Value").equals("null") && model.getString("Value").equals("null")
                    && year.getString("Value").equals("null") && power.getString("Value").equals("null")
                    && country.getString("Value").equals("null") && city.getString("Value").equals("null")
                    && state.getString("Value").equals("null") && type.getString("Value").equals("null")) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(),
                                getActivity().getString(R.string.vin_not_valid),
                                Toast.LENGTH_LONG).show();
                        getFragmentManager().popBackStack();
                    }
                });
            }

            int typeId = type.getInt("ValueId");

            if (typeId == 7) { //suv
                typeField.setText(getString(R.string.suv));
            }
            else if (typeId == 13) { //sedan
                typeField.setText(getString(R.string.sedan));
            }
            else if (typeId == 1) { //kabriolet
                typeField.setText(getString(R.string.cabrio));
            }
            else if (typeId == 3) { //coupe
                typeField.setText(getString(R.string.coupe));
            }
            else if (typeId == 9) { //van / minivan
                typeField.setText(getString(R.string.van));
            }
            else if (typeId == 60) { //pickup
                typeField.setText(getString(R.string.pickup));
            }
            else if (typeId == 15) { //kombi
                typeField.setText(getString(R.string.combi));
            }
            else {
                typeField.setText(getString(R.string.othertype));
            }

            String carTitle = checkIfNull(make.getString("Value")) + " " + checkIfNull(model.getString("Value")) + " " + checkIfNull(year.getString("Value"));

            String producerAddress = city.getString("Value") + " " + state.getString("Value") + " " + country.getString("Value");;

            makeField.setText(checkIfNull(make.getString("Value")));
            modelField.setText(checkIfNull(model.getString("Value")));
            yearField.setText(checkIfNull(year.getString("Value")));
            powerField.setText(checkIfNull(power.getString("Value")));
            countryField.setText(checkIfNull(country.getString("Value")));

            carTitleField.setText(carTitle);

            map = ((SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map)).getMap();

            LatLng plantLocation = getLocationFromAddress(producerAddress);
            if (plantLocation != null) {
                Marker marker = map.addMarker(new MarkerOptions().position(plantLocation)
                        .title(getString(R.string.production_location)));

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(plantLocation, 15));
                map.animateCamera(CameraUpdateFactory.zoomTo(10), 500, null);
            }

        } catch (Exception e) {
            Log.e("MyCarInfo", "Something is wrong with JSON, not found something in json probably");
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(getActivity(),
                            getActivity().getString(R.string.vin_not_valid),
                            Toast.LENGTH_LONG).show();
                    getFragmentManager().popBackStack();
                }
            });
        }
    }
}