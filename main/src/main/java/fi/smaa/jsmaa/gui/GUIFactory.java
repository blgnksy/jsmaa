package fi.smaa.jsmaa.gui;

import javax.swing.JMenuBar;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.JTree;

import fi.smaa.jsmaa.gui.ViewBuilder;
import fi.smaa.jsmaa.gui.presentation.LeftTreeModel;

/**
 * Abstract factory for getting (right pane) views for objects, and other GUI components.
 * 
 * @author Tommi Tervonen
 */
public interface GUIFactory {
	public ViewBuilder buildView(Object o);
	public JMenuBar getMenuBar();
	public JTree getTree();
	public JToolBar getTopToolBar();
	public LeftTreeModel getTreeModel();
	public JToolBar getBottomToolBar();
	public JProgressBar getProgressBar();
}