/*
	This file is part of JSMAA.
	(c) Tommi Tervonen, 2009	

    JSMAA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JSMAA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JSMAA.  If not, see <http://www.gnu.org/licenses/>.
*/

package fi.smaa.jsmaa.electre;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.OutrankingCriterion;

public class ElectreTri {
	
	private List<Alternative> alts;
	private List<OutrankingCriterion> crit;
	private Map<Alternative, Map<Criterion, Double>> measurements;
	private Map<Alternative, Map<Criterion, Double>> categoryUpperBounds;
	private boolean optimistic;
	private double lambda;
	private double[] weights;
	private List<Alternative> categories;

	public ElectreTri(List<Alternative> alts, List<OutrankingCriterion> crit,
			List<Alternative> categories,
			Map<Alternative, Map<Criterion, Double>> measurements,
			Map<Alternative, Map<Criterion, Double>> categoryUpperBounds,
			double[] weights,
			double lambda,
			boolean optimistic) {
		assert(weights.length == crit.size());
		this.measurements = measurements;
		this.categoryUpperBounds = categoryUpperBounds;
		this.lambda = lambda;
		this.optimistic = optimistic;
		this.weights = weights;
		this.categories = categories;
		this.alts = alts;
		this.crit = crit;
	}	
	
	/**
	 * map is : alternative -> category
	 * @return
	 */
	public Map<Alternative, Alternative> compute() {
		Map<Alternative, Alternative> resMap = new HashMap<Alternative, Alternative>();
		
		for (Alternative a : alts) {
			if (optimistic) {
				// from the top upper bound
				for (int i=categories.size()-2;i>=0;i--) {
					Alternative cat = categories.get(i);
					if (!preferred(categoryUpperBounds.get(cat),
							measurements.get(a))) {
						resMap.put(a, categories.get(i+1));
						break;
					}
					if (i ==0) {
						resMap.put(a, cat);
					}
				}
			} else {
				// from the lowest upper bound
				for (int i=0;i<categories.size()-1;i++) {
					Alternative cat = categories.get(i);
					if (!outranks(measurements.get(a),
							categoryUpperBounds.get(cat))) {
						resMap.put(a, categories.get(i));
						break;
					}
					if (i == categories.size()-1) {
						resMap.put(a, cat);
					}
				}
			}
		}
		return resMap;
	}
	
	private boolean outranks(Map<Criterion, Double> alt,
			Map<Criterion, Double> toAlt) {
		assert(alt.entrySet().size() == toAlt.entrySet().size());
		assert(alt.entrySet().size() == weights.length);
		
		double concordance = 0.0;
		for (int i=0;i<crit.size();i++) {
			OutrankingCriterion c = crit.get(i);
			Double altVal = alt.get(c);
			Double toAltVal = toAlt.get(c);
			concordance += OutrankingFunction.concordance(c, altVal, toAltVal) * weights[i];
		}
		return concordance >= lambda;
	}

	private boolean preferred(Map<Criterion, Double> alt,
			Map<Criterion, Double> toAlt) {
		assert(alt.entrySet().size() == toAlt.entrySet().size());
		assert(alt.entrySet().size() == weights.length);		

		return (outranks(alt, toAlt) && !outranks(toAlt, alt));
	}

	public void setRule(boolean optimistic) {
		this.optimistic = optimistic;
	}
}
