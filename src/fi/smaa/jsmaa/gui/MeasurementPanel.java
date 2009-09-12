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

package fi.smaa.jsmaa.gui;

import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatter;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;

import fi.smaa.jsmaa.model.CardinalMeasurement;
import fi.smaa.jsmaa.model.ExactMeasurement;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.Interval;
import fi.smaa.jsmaa.model.LogNormalMeasurement;

@SuppressWarnings("serial")
public class MeasurementPanel extends JPanel {
	
	private ValueHolder holder;
	
	public enum MeasurementType {
		EXACT("Exact"),
		INTERVAL("Interval"),
		GAUSSIAN("Gaussian"),
		LOGNORMAL("LogNormal");
		
		private String label;
		
		MeasurementType(String label) {
			this.label = label; 
		}
		
		public String getLabel() {
			return label;
		}
		
		@Override
		public String toString() {
			return label;
		}
	}
	
	public MeasurementPanel(ValueHolder measurementHolder) {
		this.holder = measurementHolder;
		holder.addPropertyChangeListener(new HolderListener());
		
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		rebuildPanel();
	}

	private class HolderListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			rebuildPanel();
		}		
	}

	public void rebuildPanel() {
		removeAll();
		add(buildValuePanel());
		add(buildChooserPanel());
		revalidate();
	}

	private JComboBox buildChooserPanel() {
		ValueModel valueModel = new ChooserValueModel();
		SelectionInList<MeasurementType> selInList = new SelectionInList<MeasurementType>(MeasurementType.values(), valueModel);
		return BasicComponentFactory.createComboBox(selInList);
	}

	private JComponent buildValuePanel() {
		CardinalMeasurement m = (CardinalMeasurement) holder.getValue();
		JComponent measComp = null;
		if (m instanceof ExactMeasurement) {
			ExactMeasurement em = (ExactMeasurement) m;
			JFormattedTextField tf = BasicComponentFactory.createFormattedTextField(
					new PresentationModel<ExactMeasurement>(em).getModel(ExactMeasurement.PROPERTY_VALUE),
					new DefaultFormatter());
			tf.setHorizontalAlignment(JTextField.CENTER);
			tf.setColumns(10);
			measComp = tf;
		} else if (m instanceof Interval) {
			Interval ival = (Interval) m;
			measComp = new IntervalPanel(this, new PresentationModel<Interval>(ival));
		} else if (m instanceof GaussianMeasurement) {
			GaussianMeasurement gm = (GaussianMeasurement) m;
		    measComp = ComponentBuilder.createGaussianMeasurementPanel(
		             new PresentationModel<GaussianMeasurement>(gm));
		} else {
			throw new RuntimeException("unknown measurement type");
		}
		return measComp;
	}
	
	private class ChooserValueModel extends AbstractValueModel {
		public Object getValue() {
			CardinalMeasurement m = (CardinalMeasurement) holder.getValue();
			if (m instanceof ExactMeasurement) {
				return MeasurementType.EXACT;
			} else if (m instanceof Interval) {
				return MeasurementType.INTERVAL;
			} else if (m instanceof LogNormalMeasurement) {
				return MeasurementType.LOGNORMAL;
			} else if (m instanceof GaussianMeasurement) {
				return MeasurementType.GAUSSIAN;
			} else {
				throw new RuntimeException("unknown measurement type");
			}
		}

		public void setValue(Object newValue) {
			MeasurementType type = (MeasurementType) newValue;
			if (type == MeasurementType.EXACT) {
				holder.setValue(new ExactMeasurement(0.0));
			} else if (type == MeasurementType.INTERVAL) {
				holder.setValue(new Interval(0.0, 1.0));
			} else if (type == MeasurementType.LOGNORMAL) {
				holder.setValue(new LogNormalMeasurement(0.0, 0.0));
			} else if (type == MeasurementType.GAUSSIAN) {
				holder.setValue(new GaussianMeasurement(1.0, 0.0));
			} else {
				throw new RuntimeException("unknown measurement type");
			}
		}
		
	}
}