package uk.ac.ebi.spot.diachron;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.jena.atlas.json.JSON;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by olgavrou on 03/11/2015.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class ComplexChange {

    private Double Priority;
    private String Complex_Change;
    private ArrayList<JSONObject> Complex_Change_Parameters = new ArrayList<>();
    private ArrayList<SimpleChange>  Simple_Changes = new ArrayList<>();
    private String Description = "";

    public String returnComplexChangeParameters(ArrayList<JSONObject> complexChangeParams){
        String ccP = "[";
        for (JSONObject cc : complexChangeParams){
            String ccc = cc.toString();
            ccc = ccc.replace("\"","\\\"");
            ccP = ccP + ccc + ",";
        }
        if(ccP.endsWith(",")){
            ccP = ccP.substring(0, ccP.length() - 1); //remove the last ","
        }
        ccP = ccP + "]";
        return ccP;
    }

    public void setPriority(Double priority) {
        Priority = priority;
    }

    public Double getPriority() {
        return Priority;
    }

    public String getComplex_Change() {
        return Complex_Change;
    }

    public void setComplex_Change(String complex_Change) {
        Complex_Change = complex_Change;
    }

    public ArrayList<JSONObject> getComplex_Change_Parameters() {
        return Complex_Change_Parameters;
    }

    public void setComplex_Change_Parameters(ArrayList<JSONObject> complex_Change_Parameters) {
        Complex_Change_Parameters = complex_Change_Parameters;
    }

    public ArrayList<SimpleChange> getSimple_Changes() {
        return Simple_Changes;
    }

    public void setSimple_Changes(ArrayList<SimpleChange> simple_Changes) {
        Simple_Changes = simple_Changes;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    @Override
    public String toString() {
        return "{\\\"Complex_Change\\\":\\\"" + getComplex_Change() + "\\\",\\\"Priority\\\":" + getPriority() + ",\\\"Simple_Changes\\\":" + getSimple_Changes().toString() + ",\\\"Complex_Change_Parameters\\\":" + returnComplexChangeParameters(getComplex_Change_Parameters()) + "}";
    }
}

