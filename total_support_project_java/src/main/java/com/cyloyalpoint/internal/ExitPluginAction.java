package com.cyloyalpoint.internal;

import java.awt.event.ActionEvent;
import java.util.Map;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.service.util.CyServiceRegistrar;

public class ExitPluginAction extends AbstractCyAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CyServiceRegistrar cyServiceRegistrar;

	public ExitPluginAction(CyServices cyServices, String parentMenu, String menuName) {
		super(menuName);
		setPreferredMenu(parentMenu);

		this.cyServiceRegistrar = cyServices.getServiceRegistrar();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (Map.Entry<Object, Class<?>> entry : App.services.entrySet()) {
			cyServiceRegistrar.unregisterService(entry.getKey(), entry.getValue());
		}
		App.services.clear();
	}
}
