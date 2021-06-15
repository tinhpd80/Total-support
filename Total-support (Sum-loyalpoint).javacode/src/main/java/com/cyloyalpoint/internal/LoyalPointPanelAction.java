package com.cyloyalpoint.internal;

import java.awt.event.ActionEvent;
import java.util.Properties;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.service.util.CyServiceRegistrar;

public class LoyalPointPanelAction extends AbstractCyAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final CySwingApplication cySwingApplication;
	private final CyServiceRegistrar cyServiceRegistrar;
	private final CytoPanel cytoPanelSouth;
	private LoyalPointPanel loyalPointPanel;

	public LoyalPointPanelAction(CyServices cyServices, String parentMenu, String panelName) {
		super(panelName);
		setPreferredMenu(parentMenu);

		this.cySwingApplication = cyServices.getSwingApplication();
		this.cyServiceRegistrar = cyServices.getServiceRegistrar();
		this.cytoPanelSouth = this.cySwingApplication.getCytoPanel(CytoPanelName.SOUTH);
		this.loyalPointPanel = new LoyalPointPanel(cyServices.getApplicationManager(), cyServices.getSwingApplication(),
				cyServices.getTaskManager(), cyServices.getNetworkFactory());
	}

	public void actionPerformed(ActionEvent e) {
		cyServiceRegistrar.registerService(loyalPointPanel, CytoPanelComponent.class, new Properties());

		App.services.put(loyalPointPanel, CytoPanelComponent.class);

		if (cytoPanelSouth.getState() == CytoPanelState.HIDE) {
			cytoPanelSouth.setState(CytoPanelState.DOCK);
		}

		int index = cytoPanelSouth.indexOfComponent(loyalPointPanel);
		if (index == -1) {
			return;
		}

		cytoPanelSouth.setSelectedIndex(index);
	}

}
