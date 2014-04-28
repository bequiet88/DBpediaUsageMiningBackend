package de.unimannheim.dws.algorithms;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.NormalizableDistance;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.neighboursearch.PerformanceStats;
import weka.core.TechnicalInformationHandler;

/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/**
 * <!-- options-start --> Valid options are:
 * <p/>
 * 
 * <pre>
 * -D
 *  Turns off the normalization of attribute 
 *  values in distance calculation.
 * </pre>
 * 
 * <pre>
 * -R &lt;col1,col2-col4,...&gt;
 *  Specifies list of columns to used in the calculation of the 
 *  distance. 'first' and 'last' are valid indices.
 *  (default: first-last)
 * </pre>
 * 
 * <pre>
 * -V
 *  Invert matching sense of column indices.
 * </pre>
 * 
 * <!-- options-end -->
 * 
 * @author Jochen Huelss
 * @version $Revision: 1.2 $
 */

public class CustomPairWiseDistance extends NormalizableDistance implements
		TechnicalInformationHandler {

	/** for serialization. */
	private static final long serialVersionUID = 6783782554224000243L;

	/**
	 * Constructs an Manhattan Distance object, Instances must be still set.
	 */
	public CustomPairWiseDistance() {
		super();
	}

	/**
	 * Constructs an Manhattan Distance object and automatically initializes the
	 * ranges.
	 * 
	 * @param data
	 *            the instances the distance function should work on
	 */
	public CustomPairWiseDistance(Instances data) {
		super(data);
	}

	/**
	 * Returns a string describing this object.
	 * 
	 * @return a description of the evaluator suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String globalInfo() {
		return "Implements the distance of two semantic web properties based on their pair count "
				+ "in the USEWOD 2014 dataset. The distance between two properties is the inverse "
				+ "of their count.\n\n"
				+ "For more information, see:\n\n"
				+ getTechnicalInformation().toString();
	}

	/**
	 * Returns an instance of a TechnicalInformation object, containing detailed
	 * information about the technical background of this class, e.g., paper
	 * reference or book this class is based on.
	 * 
	 * @return the technical information about this class
	 */
	public TechnicalInformation getTechnicalInformation() {
		TechnicalInformation result;

		result = new TechnicalInformation(Type.MISC);
		result.setValue(Field.AUTHOR, "Jochen");
		result.setValue(Field.TITLE, "Pair-wise Property Distances");
		result.setValue(Field.URL, "");

		return result;
	}

	/**
	 * Updates the current distance calculated so far with the new difference
	 * between two attributes. The difference between the attributes was
	 * calculated with the difference(int,double,double) method.
	 * 
	 * @param currDist
	 *            the current distance calculated so far
	 * @param diff
	 *            the difference between two new attributes
	 * @return the update distance
	 * @see #difference(int, double, double)
	 */
	protected double updateDistance(double currDist, double diff) {
		double result;

		result = currDist;
		result += Math.abs(diff);

		return result;
	}

	/**
	 * Returns the revision string.
	 * 
	 * @return the revision
	 */
	public String getRevision() {
		return RevisionUtils.extract("$Revision: 1.2 $");
	}

	/**
	 * Calculates the distance between two instances. Offers speed up (if the
	 * distance function class in use supports it) in nearest neighbour search
	 * by taking into account the cutOff or maximum distance. Depending on the
	 * distance function class, post processing of the distances by
	 * postProcessDistances(double []) may be required if this function is used.
	 * 
	 * @param first
	 *            the first instance
	 * @param second
	 *            the second instance
	 * @param cutOffValue
	 *            If the distance being calculated becomes larger than
	 *            cutOffValue then the rest of the calculation is discarded.
	 * @param stats
	 *            the performance stats object
	 * @return the distance between the two given instances or
	 *         Double.POSITIVE_INFINITY if the distance being calculated becomes
	 *         larger than cutOffValue.
	 */
	@Override
	public double distance(Instance first, Instance second, double cutOffValue,
			PerformanceStats stats) {
		double distance = 0;

		Integer value1 = (new Double(first.valueSparse(0))).intValue();
		Integer value2 = (new Double(second.valueSparse(0))).intValue();

		validate();

		distance = DistanceMatrix$.MODULE$.getDistance(value1, value2)
				.doubleValue();

		if (distance > cutOffValue) {
			return Double.POSITIVE_INFINITY;
		}

		if (stats != null) {
			stats.incrCoordCount();
		}

		return distance;
	}

}
