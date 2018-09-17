package com.example.drake_000.mycarinfo;

/**
 * Created by drake_000 on 12/2/2017.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

public class FetchInfo {

    private static final String CAR_INFO_API =
            "https://vpic.nhtsa.dot.gov/api/vehicles/decodevin/%s?format=json";

    public static JSONObject getJSON(Context context, String vin){
        try {
            URL url = new URL(String.format(CAR_INFO_API, vin));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while( (tmp=reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            if(!data.getString("Message").equals("Results returned successfully")) {
                return null;
            }

            JSONArray results = data.getJSONArray("Results");
            JSONObject make = results.getJSONObject(1);
            if (make.getString("Value").equals("7 - Manufacturer is not registered with NHTSA for sale or importation in the U.S. for use on U.S roads; Please contact the manufacturer directly for more information.")) {
                return null;
                // to występuje gdy sprawdzamy auto którego producent nie produkuje na rynek amerykański,
                // np. Citroen albo Renault i nie ma żadnych danych o nim
            }

            return data;
        } catch (Exception e) {
            return null;
        }
    }
}