package uk.ac.ebi.spot.diachron.changes;

import java.util.ArrayList;

/**
 * Created by olgavrou on 24/11/2015.
 */
public class SimpleChange {
    private boolean Is_Optional = false;
    private String subjectName = null;
    private String subjectParameter = null;
    private boolean setSubjectParameter = false;
    private String objectName = null;
    private String objectParameter = null;
    private boolean setObjectParameter = false;
    private String Selection_Filter = null;
    private String Simple_Change = null;
    private String property = null;
    private String Join_Filter = "";
    private String mappingFilter = "";
    private int simpleChangeId = 0;
    private String Simple_Change_Uri = "";

    public boolean isIs_Optional() {
        return Is_Optional;
    }

    public void setIs_Optional(boolean is_Optional) {
        Is_Optional = is_Optional;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectParameter() {
        return subjectParameter;
    }

    public void setSubjectParameter(String subjectParameter) {
        this.subjectParameter = subjectParameter;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectParameter() {
        return objectParameter;
    }

    public void setObjectParameter(String objectParameter) {
        this.objectParameter = objectParameter;
    }

    public String getSelection_Filter() {
        return Selection_Filter;
    }

    public void setSelection_Filter(String selection_Filter) {
        this.Selection_Filter = selection_Filter;
    }

    public String getSimple_Change() {
        return Simple_Change;
    }

    public void setSimple_Change(String simple_Change) {
        this.Simple_Change = simple_Change;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getJoin_Filter() {
        return Join_Filter;
    }

    public void setJoin_Filter(ArrayList<SimpleChange> simpleChanges) {
        String joinFilter = "";
        for(SimpleChange sc : simpleChanges){
            joinFilter = joinFilter + sc.getSimpleChangeId() + ":" + sc.getSimple_Change() + sc.getSubjectParameter() + "=";
        }
        if(joinFilter.endsWith("=")){
            joinFilter = joinFilter.substring(0, joinFilter.length() - 1); //remove the last "="
        }
        this.Join_Filter = joinFilter;
    }

    public String getMappingFilter() {
        return mappingFilter;
    }

    public void setMappingFilter(String mappingFilter) {
        this.mappingFilter = mappingFilter;
    }

    public int getSimpleChangeId() {
        return simpleChangeId;
    }

    public void setSimpleChangeId(int simpleChangeId) {
        this.simpleChangeId = simpleChangeId;
    }

    public boolean isSetSubjectParameter() {
        return setSubjectParameter;
    }

    public void setSetSubjectParameter(boolean setSubjectParameter) {
        this.setSubjectParameter = setSubjectParameter;
    }

    public boolean isSetObjectParameter() {
        return setObjectParameter;
    }

    public void setSetObjectParameter(boolean setObjectParameter) {
        this.setObjectParameter = setObjectParameter;
    }

    public String getSimple_Change_Uri() {
        return Simple_Change_Uri;
    }

    public void setSimple_Change_Uri(String simple_Change_Uri) {
        this.Simple_Change_Uri = simple_Change_Uri;
    }

    @Override
    public String toString() {
        return "{\\\"Simple_Change\\\":\\\"" + getSimple_Change() + "\\\",\\\"Simple_Change_Uri\\\":\\\"" + getSimple_Change_Uri() + "\\\",\\\"Is_Optional\\\":" + Is_Optional + ",\\\"Selection_Filter\\\":[\\\"" + getSelection_Filter() + "\\\"],\\\"Join_Filter\\\":[\\\"" + getJoin_Filter() + "\\\"]}";
    }
}
