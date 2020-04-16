package uk.ac.nott.cs.g53dia.library;
import java.util.Random;

/**
 * Abstract fallible action
 *
 * @author Brian Logan
 */
/*
 * Copyright (c) 2017 Brian Logan
 * 
 * See the file "license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
public class FallibleAction implements Action {

    /**
     * Default action failure probability
     */
    final static double DEFAULT_FAILURE_PROBABILITY = 0.0015;

    /**
     * Cause actions to fail with given probability.
     * @throws ActionFailedException The action couldn't be Performed.
     * @param tanker The Tanker trying to Perform this action.
     * @param env The Environment that the Tanker inhabits.
     */
    public void execute(Environment env, Tanker tanker) 
        throws ActionFailedException {

	// Clear the failed flag from any previous failure
	tanker.actionFailed = false;

	if (tanker.r.nextDouble() < DEFAULT_FAILURE_PROBABILITY) {
	    tanker.actionFailed = true;
	    throw new ActionFailedException("Fallible: action failed");
	}
    }
}

