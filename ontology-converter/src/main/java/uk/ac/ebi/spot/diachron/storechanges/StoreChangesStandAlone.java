package uk.ac.ebi.spot.diachron.storechanges;

import org.apache.commons.cli.*;

import java.io.IOException;

/**
 * Created by olgavrou on 03/02/2016.
 * Made to call StoreChanges from command line
 */
public class StoreChangesStandAlone {

    private static String onologyName;
    private static String changesSchema;
    private static String oldVersion;
    private static String newVersion;
    private static String version;
    private static String date;

    private static int parseArguments(String[] args) throws IOException {

        CommandLineParser parser = new GnuParser();
        HelpFormatter help = new HelpFormatter();
        Options options = bindOptions();
        int parseArgs = 0;

        try {
            CommandLine cl = parser.parse(options, args, true);

            if (cl.hasOption(""))  {
                // print out mode help
                help.printHelp("diachron", options, true);
                parseArgs += 1;
            } else {
                onologyName = cl.getOptionValue("n");
                changesSchema = cl.getOptionValue("cs");
                oldVersion = cl.getOptionValue("ov");
                newVersion = cl.getOptionValue("nv");
                version = cl.getOptionValue("v");
                date = cl.getOptionValue("d");
            }

        } catch (ParseException e) {
            e.printStackTrace();
            parseArgs = 1;
        }

        return parseArgs;
    }

    private static Options bindOptions() {
        Options options = new Options();
        Option ontologyOption = new Option(
                "n",
                "name",
                true,
                "Short name of the ontology e.g. efo");
        ontologyOption.setRequired(true);

        options.addOption(ontologyOption);
        Option changesSchema = new Option(
                "cs",
                "changesSchema",
                true,
                "changes schema e.g. http://www.diachron-fp7.eu/efo");
        changesSchema.setRequired(true);
        options.addOption(changesSchema);

        Option oldVersion = new Option(
                "ov",
                "oldVersion",
                true,
                "old version e.g. http://www.diachron-fp7.eu/resource/recordset/EFO/1450375796150/3F72F2CCB735199E04A627D5AB935296");
        oldVersion.setRequired(true);
        options.addOption(oldVersion);

        Option newVersion = new Option(
                "nv",
                "newVersion",
                true,
                "new version e.g. http://www.diachron-fp7.eu/resource/recordset/EFO/1450375796150/3F72F2CCB735199E04A627D5AB935296");
        newVersion.setRequired(true);
        options.addOption(newVersion);

        Option version = new Option(
                "v",
                "version",
                true,
                "version e.g. 2.68");
        version.setRequired(true);
        options.addOption(version);

        Option date = new Option(
                "d",
                "date",
                true,
                "date e.g. 2016.01.01");
        date.setRequired(true);
        options.addOption(date);

        return options;
    }


    public static void main(String args[]){

        //****************************************************************************************************************
        try {
            int statusCode = parseArguments(args);

            if (statusCode == 0) {

                    StoreChanges storeChanges = new StoreChanges(onologyName, changesSchema, oldVersion, newVersion, version, date);
                    try {
                        String changes = storeChanges.getChanges();
                        if(changes != null){
                            storeChanges.storeChanges(changes);
                        } else {
                            System.out.println("No changes found for this ontology: " + onologyName);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        storeChanges.terminate();
                    }

            }
            else {
                System.exit(statusCode);
            }
        }
        catch (IOException e) {
            System.err.println("A read/write problem occurred: " + e.getMessage());
            System.exit(1);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Diachron archiver did not complete successfully: " + e.getMessage());
            System.exit(1);
        }
        //****************************************************************************************************************

    }
}
