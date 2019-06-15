package com.example.marius.path.data_model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReverseGeocoding {
    Map<String,String> plus_code = new HashMap<>();
    List<ReverseGeocodingResults> results = new ArrayList<>();
    String status;
    String error_message;

    public Map<String, String> getPlus_code() {
        return plus_code;
    }

    public String getStatus() {
        return status;
    }

    public String getError_message() {
        return error_message;
    }

    public List<ReverseGeocodingResults> getResults() {
        return results;
    }

    @Override
    public String toString() {
        return "ReverseGeocoding{" +
                "plus_code=" + plus_code +
                ", results=" + results +
                ", status='" + status + '\'' +
                ", error_message='" + error_message + '\'' +
                '}';
    }
}
