package uk.ac.ebi.spot.diachron.utils;

/**
 * @author Simon Jupp
 * @date 12/01/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class DiachronException extends Throwable {
    public DiachronException(String s) {
                super(s);
            }
    public DiachronException(String s, Exception e) {
                super(s, e);
            }

    }
