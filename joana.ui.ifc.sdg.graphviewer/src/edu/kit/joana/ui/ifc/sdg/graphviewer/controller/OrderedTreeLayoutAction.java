/**
 * This file is part of the Joana IFC project. It is developed at the
 * Programming Paradigms Group of the Karlsruhe Institute of Technology.
 *
 * For further details on licensing please read the information at
 * http://joana.ipd.kit.edu or contact the authors.
 */
package edu.kit.joana.ui.ifc.sdg.graphviewer.controller;

import edu.kit.joana.ui.ifc.sdg.graphviewer.view.CallGraphView;
import edu.kit.joana.ui.ifc.sdg.graphviewer.view.GraphPane;

import java.awt.event.ActionEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jgraph.layout.OrderedTreeLayoutAlgorithm;

public class OrderedTreeLayoutAction extends AbstractGVAction implements ChangeListener {
	private static final long serialVersionUID = -431575862619382771L;
	private GraphPane graphPane = null;

	public OrderedTreeLayoutAction(GraphPane graphPane) {
		super("orderedTreeLayout.name", "orderedTreeLayout.description");
		this.graphPane = graphPane;
		graphPane.addChangeListener(this);
	}

	public void actionPerformed(ActionEvent event) {
		if (!(graphPane.getSelectedJGraph() instanceof CallGraphView))
			return;
		CallGraphView graph = (CallGraphView) graphPane.getSelectedJGraph();
		if (graphPane.isVisible()) {
			graphPane.setVisible(false);
			graph.clearSelection();
			graph.applyLayout(new OrderedTreeLayoutAlgorithm());
			graph.clearSelection();
			graphPane.setVisible(true);
		}
	}

	public void stateChanged(ChangeEvent e) {
		if(this.graphPane.getSelectedIndex()==-1) {
			this.setEnabled(false);
			return;
		}
		if (!(this.graphPane.getSelectedJGraph() instanceof CallGraphView)) {
			this.setEnabled(false);
		} else
			this.setEnabled(true);
	}
}