package uk.ac.ebi.spot.diachron.storechanges;

/**
 * Created by olgavrou on 02/02/2016.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import org.diachron.detection.exploit.DefChange;
import org.diachron.detection.exploit.Parameter;

import java.util.Iterator;

public class DetChangeTest extends DefChange implements Comparable<DetChangeTest> {
    private String oldVersion;
    private String newVersion;

    public DetChangeTest(String changeURI, String changeName, String changeDesription, String oldV, String newV) {
        super(changeURI, changeName, changeDesription);
        int start = oldV.lastIndexOf("/");
        this.newVersion = newV.substring(start + 1);
        this.oldVersion = oldV.substring(start + 1);
    }

    public String getNewVersion() {
        return this.newVersion;
    }

    public String getOldVersion() {
        return this.oldVersion;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.changeName + "\n");
        sb.append(this.changeDescription + "\n");
        Iterator var3 = this.parameters.iterator();

        while(var3.hasNext()) {
            Parameter param = (Parameter)var3.next();
            sb.append(param.getParamName() + " : " + param.getParamValue() + "\n");
        }

        sb.append(this.oldVersion + " -> " + this.newVersion + "\n");
        return sb.toString();
    }

    public int compareTo(DetChangeTest o) {
        String oChangeUri = o.changeURI;
        String oOldVersion = o.oldVersion;
        String tOldVersion = this.oldVersion;
        int r = !oOldVersion.equals(tOldVersion)?tOldVersion.compareTo(oOldVersion):this.changeURI.compareTo(oChangeUri);
        return r;
    }
}

