package uk.ac.ebi.spot.diachron;

/**
 * Created by olgavrou on 30/11/2015.
 */
public class ComplexChangeJson {
    private String Dataset_URI = "";
    private ComplexChange CC_Definition = new ComplexChange();

    public String getDataset_URI() {
        return Dataset_URI;
    }

    public void setDataset_URI(String dataset_URI) {
        Dataset_URI = dataset_URI;
    }

    public ComplexChange getCC_Definition() {
        return CC_Definition;
    }

    public void setCC_Definition(ComplexChange CC_Definition) {
        this.CC_Definition = CC_Definition;
    }

    @Override
    public String toString() {
        return "{\"Dataset_URI\":\"" + getDataset_URI() + "\",\"CC_Definition\":\"" + getCC_Definition().toString() + "\"}";
    }
}
