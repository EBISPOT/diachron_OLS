import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import eu.diachron.ebi.model.DiachronJenaModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import uk.ac.ebi.spot.diachron.BioportalOntologyRetriever;


import java.io.*;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Simon Jupp
 * @date 13/03/2014
 * Functional Genomics Group EMBL-EBI
 */
public class GetAllEFOinOWL {

    public static void main(String[] args) {


        final BioportalOntologyRetriever ret = new BioportalOntologyRetriever(args[0]);
        Map<String, String> versionInfo = ret.getAllSubmissionId("EFO");

        for (final String id :versionInfo.keySet()) {

            final String version = versionInfo.get(id);

            System.out.println("reading EFO " + version);

            InputStream stream  = ret.getOntologyBySubmissionId("EFO", id);

            try {

                FileOutputStream fos = new FileOutputStream(new File("/Users/jupp/tmp/diachron/efo-" + version + ".owl"));

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = stream.read(bytes)) != -1) {
                    fos.write(bytes, 0, read);
                }
                System.out.println("Finished writing EFO" + version);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
