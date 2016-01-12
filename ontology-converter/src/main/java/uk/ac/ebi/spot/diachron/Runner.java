package uk.ac.ebi.spot.diachron;

import org.apache.commons.cli.*;

import java.io.*;
import java.util.Map;

/**
 * @author Simon Jupp
 * @date 18/12/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class Runner {

    private static String name;
    private static String apiKey;
    private static String host;
    private static String versionRegex;
    private static String archive;
    private static int count;
    private static File outputFile;

    public static void main(String[] args) {

        try {
            int statusCode = parseArguments(args);

            if (statusCode == 0) {

                AthensOWLToDiachronConverter converter = new AthensOWLToDiachronConverter();
                if (versionRegex != null) {
                    converter.setVersionRegexFilter(versionRegex);
                }

                if (name != null) {
                    converter.convertAndArchive(name ,apiKey, count);
                }
                else {
                    //converter.convert(name ,apiKey, count, outputFile);
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


    }

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
            }
            else {
                if (cl.hasOption("o")) {
                    outputFile = new File(cl.getOptionValue("o"));
                }
                else {
                    outputFile = new File(".");
                }

                if (cl.hasOption("h")) {
                    host = cl.getOptionValue("h");
                }
                else {
                    host = null;
                }

                if (cl.hasOption("r")) {
                    versionRegex = cl.getOptionValue("r");
                }
                else {
                    versionRegex = null;
                }

                if (cl.hasOption("a")) {
                    archive = cl.getOptionValue("a");
                }
                else {
                    archive = null;
                }

                if (cl.getOptionValue("c") != null) {
                    count = Integer.parseInt(cl.getOptionValue("c"));
                }
                else {
                    count = -1;
                }
                name = cl.getOptionValue("n");
                apiKey = cl.getOptionValue("apiKey");

            }

        } catch (ParseException e) {
            e.printStackTrace();
            parseArgs = 1;
        }

        return parseArgs;
    }

    private static Options bindOptions() {
        Options options = new Options();

        // help
        Option helpOption = new Option("h", "help", false, "Print the help");
        options.addOption(helpOption);

        // add input options
        OptionGroup inputGroup = new OptionGroup();
        inputGroup.setRequired(true);

        Option ontologyOption = new Option(
                "n",
                "name",
                true,
                "Short name of the ontology e.g. EFO");
        ontologyOption.setRequired(true);
        options.addOption(ontologyOption);


        Option hostOption = new Option(
                "h",
                "host",
                true,
                "If archiving to Diachron provide the diachron integration server");
        hostOption.setRequired(false);
        options.addOption(hostOption);

        Option archiveOption = new Option(
                "a",
                "archive",
                true,
                "If archiving to Diachron provide the diachron archive server");
        archiveOption.setRequired(false);
        options.addOption(archiveOption);

        Option versionOption = new Option(
                "r",
                "versionRegex",
                true,
                "Regular expression to filter out versions you want to archive");
        versionOption.setRequired(false);
        options.addOption(versionOption);

        Option apiOption = new Option(
                "k",
                "apiKey",
                true,
                "Bioportal API Key");
        apiOption.setRequired(true);
        options.addOption(apiOption);

        // add output file arguments
        Option outputOption = new Option(
                "o",
                "output",
                true,
                "Output - directory where files will be written");
        outputOption.setArgName("output");
        outputOption.setRequired(true);
        options.addOption(outputOption);

        Option countOption = new Option(
                "c",
                "count",
                true,
                "Count - Number of version to download form bioportal");
        countOption.setArgName("float");
        countOption.setRequired(false);
        options.addOption(countOption);

//        options.addOptionGroup(inputGroup);
        return options;
    }

}
