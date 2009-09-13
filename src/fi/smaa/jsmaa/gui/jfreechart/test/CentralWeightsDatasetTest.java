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

package fi.smaa.jsmaa.gui.jfreechart.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.smaa.jsmaa.SMAA2Results;
import fi.smaa.jsmaa.gui.jfreechart.CentralWeightsDataset;
import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.ScaleCriterion;

public class CentralWeightsDatasetTest {
	
	private CentralWeightsDataset data;
	private SMAA2Results res;
	private Alternative a1;
	private Alternative a2;
	private List<Alternative> alts;
	private double[] weights;
	private Integer[] ranksHits;
	private ScaleCriterion c1;
	private ScaleCriterion c2;
	private List<Criterion> crit;

	@Before
	public void setUp() {
		a1 = new Alternative("a1");
		a2 = new Alternative("a2");
		alts = new ArrayList<Alternative>();
		alts.add(a1);
		alts.add(a2);
		
		c1 = new ScaleCriterion("c1");
		c2 = new ScaleCriterion("c1");
		
		crit = new ArrayList<Criterion>();
		crit.add(c1);
		crit.add(c2);
				
		res = new SMAA2Results(alts, crit, 1);
		weights = new double[]{0.2, 0.8};
		ranksHits = new Integer[] { 1, 0 };
		
		res.update(ranksHits, weights);
		
		data = new CentralWeightsDataset(res);
	}
	
	@Test
	public void testGetRowIndex() {
		assertEquals(1, data.getRowIndex(a2));
		assertEquals(-1, data.getRowIndex(new Alternative("aaa")));
	}
	
	@Test
	public void testGetRowKey() {
		assertEquals(a2, data.getRowKey(1));		
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public void testGetRowKeyThrows() {
		data.getRowKey(4);
	}
	
	@Test
	public void testGetRowKeys() {
		assertEquals(alts, data.getRowKeys());
	}
	
	@Test
	public void testGetValue() {
		assertEquals(Double.NaN, data.getValue(a1, c1));
		assertEquals(Double.NaN, data.getValue(a1, c2));
		assertEquals(new Double(0.2), data.getValue(a2, c1));
		assertEquals(new Double(0.8), data.getValue(a2, c2));
	}
	
	@Test
	public void testGetRowCount() {
		assertEquals(alts.size(), data.getRowCount());
	}
	
	@Test
	public void testGetValueInts() {
		assertEquals(Double.NaN, data.getValue(0, 0));
		assertEquals(Double.NaN, data.getValue(0, 1));
		assertEquals(new Double(0.2), data.getValue(1, 0));
		assertEquals(new Double(0.8), data.getValue(1, 1));
	}
	
	@Test
	public void testGetColumnIndex() {
		assertEquals(1, data.getColumnIndex(c2));
		assertEquals(-1, data.getColumnIndex(new ScaleCriterion("cccc")));
	}
	
	@Test
	public void testGetColumnKey() {
		assertEquals(c2, data.getColumnKey(1));
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public void testGetColumnKeyThrows() {
		data.getColumnKey(5);
	}
	
	@Test
	public void testGetColumnKeys() {
		assertEquals(crit, data.getColumnKeys());
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(crit.size(), data.getColumnCount());
	}
}
