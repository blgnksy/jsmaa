/*
    This file is part of JSMAA.
    JSMAA is distributed from http://smaa.fi/.

    (c) Tommi Tervonen, 2009-2010.
    (c) Tommi Tervonen, Gert van Valkenhoef 2011.
    (c) Tommi Tervonen, Gert van Valkenhoef, Joel Kuiper, Daan Reid 2012.
    (c) Tommi Tervonen, Gert van Valkenhoef, Joel Kuiper, Daan Reid, Raymond Vermaas 2013-2015.

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
package fi.smaa.jsmaa.gui.jfreechart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;

import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.NamedObject;
import fi.smaa.jsmaa.simulator.ResultsEvent;
import fi.smaa.jsmaa.simulator.SMAAResults;
import fi.smaa.jsmaa.simulator.SMAAResultsListener;

public abstract class SMAADataSet<R extends SMAAResults> implements SMAAResultsListener, Dataset {

	private List<DatasetChangeListener> dataListeners = new ArrayList<DatasetChangeListener>();
	private DatasetGroup group;
	protected R results;
	
	protected NameListener nameListener = new NameListener();

	protected SMAADataSet(R results) {
		super();
		setResults(results);
	}

	synchronized public void setResults(R results) {
		this.results = results;
		results.addResultsListener(this);
		for (Alternative a : results.getAlternatives()) {
			a.addPropertyChangeListener(nameListener);
		}
		fireResultsChanged();
	}
	
	synchronized public void addChangeListener(DatasetChangeListener l) {
		dataListeners.add(l);
	}

	public DatasetGroup getGroup() {
		return group;
	}

	synchronized public void removeChangeListener(DatasetChangeListener l) {
		dataListeners.remove(l);
	}

	public void setGroup(DatasetGroup g) {
		group = g;
	}

	public void resultsChanged(ResultsEvent ev) {
		fireResultsChanged();
	}

	synchronized private void fireResultsChanged() {
		for (DatasetChangeListener l : dataListeners) {
			l.datasetChanged(new DatasetChangeEvent(this, this));
		}
	}
	
	private class NameListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(NamedObject.PROPERTY_NAME)) {
				fireResultsChanged();
			}
		}
	}

}