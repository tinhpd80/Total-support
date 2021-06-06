package com.cyloyalpoint.internal;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.task.hide.UnHideAllEdgesTaskFactory;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CyActivator extends AbstractCyActivator {

	private static final Logger LOGGER = LoggerFactory.getLogger(CyActivator.class);

	public CyActivator() {
		super();
		LOGGER.info("Creating cyLoyalPoint bundle activator...");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start(BundleContext context) throws Exception {
		LOGGER.info("Starting cyLoyalPoint app...");

		// Start plug-in in separate thread
		final ExecutorService service = Executors.newSingleThreadExecutor();
		service.submit(() -> {
			CyApplicationManager cyApplicationManager = getService(context, CyApplicationManager.class);

			CySwingApplication cySwingApplication = getService(context, CySwingApplication.class);

			CyServiceRegistrar cyServiceRegistrar = getService(context, CyServiceRegistrar.class);

			TaskManager<?, ?> taskManager = getService(context, TaskManager.class);

			DialogTaskManager dialogTaskManager = getService(context, DialogTaskManager.class);

			CyNetworkFactory cyNetworkFactory = getService(context, CyNetworkFactory.class);

			CyNetworkManager cyNetworkManager = getService(context, CyNetworkManager.class);

			CyNetworkNaming cyNetworkNaming = getService(context, CyNetworkNaming.class);

			CyRootNetworkManager cyRootNetworkManager = getService(context, CyRootNetworkManager.class);

			CyTableFactory cyTableFactory = getService(context, CyTableFactory.class);

			CyNetworkTableManager cyNetworkTableManager = getService(context, CyNetworkTableManager.class);

			LoadVizmapFileTaskFactory loadVizmapFileTaskFactory = getService(context, LoadVizmapFileTaskFactory.class);

			VisualMappingManager visualMappingManager = getService(context, VisualMappingManager.class);

			CyNetworkViewFactory cyNetworkViewFactory = getService(context, CyNetworkViewFactory.class);

			CyNetworkViewManager cyNetworkViewManager = getService(context, CyNetworkViewManager.class);

			CyLayoutAlgorithmManager cyLayoutAlgorithmManager = getService(context, CyLayoutAlgorithmManager.class);

			VisualStyleFactory visualStyleFactory = getService(context, VisualStyleFactory.class);

			UndoSupport undoSupport = getService(context, UndoSupport.class);

			OpenBrowser openBrowser = getService(context, OpenBrowser.class);

			UnHideAllEdgesTaskFactory unHideAllEdgesTaskFactory = getService(context, UnHideAllEdgesTaskFactory.class);

			StreamUtil streamUtil = getService(context, StreamUtil.class);

			VisualMappingFunctionFactory discreteMappingFunctionFactory = getService(context,
					VisualMappingFunctionFactory.class, "(mapping.type=discrete)");

			VisualMappingFunctionFactory passthroughMappingFunctionFactory = getService(context,
					VisualMappingFunctionFactory.class, "(mapping.type=passthrough)");

			CyProperty<Properties> cyProperties = getService(context, CyProperty.class,
					"(cyPropertyName=cytoscape3.props)");

			// create the cyServices, keeps references to all CytoScape core model classes
			CyServices cyServices = new CyServices();

			// create the cyModel, keeps track of application state
			CyModel cyModel = new CyModel();

			// create the CyAppManager
			CyAppManager cyAppManager = new CyAppManager(cyModel, cyServices);

			// add the CytoScape service references to cyServices
			cyServices.setApplicationManager(cyApplicationManager);
			cyServices.setSwingApplication(cySwingApplication);
			cyServices.setServiceRegistrar(cyServiceRegistrar);
			cyServices.setTaskManager(taskManager);
			cyServices.setDialogTaskManager(dialogTaskManager);
			cyServices.setNetworkFactory(cyNetworkFactory);
			cyServices.setNetworkManager(cyNetworkManager);
			cyServices.setNetworkNaming(cyNetworkNaming);
			cyServices.setRootNetworkManager(cyRootNetworkManager);
			cyServices.setTableFactory(cyTableFactory);
			cyServices.setNetworkTableManager(cyNetworkTableManager);
			cyServices.setLoadVizmapFileTaskFactory(loadVizmapFileTaskFactory);
			cyServices.setVisualMappingManager(visualMappingManager);
			cyServices.setNetworkViewFactory(cyNetworkViewFactory);
			cyServices.setNetworkViewManager(cyNetworkViewManager);
			cyServices.setLayoutAlgorithmManager(cyLayoutAlgorithmManager);
			cyServices.setVisualStyleFactory(visualStyleFactory);
			cyServices.setUndoSupport(undoSupport);
			cyServices.setOpenBrowser(openBrowser);
			cyServices.setUnHideAllEdgesTaskFactory(unHideAllEdgesTaskFactory);
			cyServices.setStreamUtil(streamUtil);
			cyServices.setDiscreteMappingFunctionFactory(discreteMappingFunctionFactory);
			cyServices.setPassthroughMappingFunctionFactory(passthroughMappingFunctionFactory);
			cyServices.setCyProperty(cyProperties);

			// Loyal Point Panel
			LoyalPointPanelAction loyalPointPanelAction = new LoyalPointPanelAction(cyServices,
					ServiceProperties.APPS_MENU + CyModel.MENU_NAME, "1) Compute Loyal Point");
			registerService(context, loyalPointPanelAction, CyAction.class, new Properties());

			// About Task
			AboutTaskFactory aboutTaskFactory = new AboutTaskFactory();
			Properties aboutTaskProperties = new Properties();
			aboutTaskProperties.put(ServiceProperties.PREFERRED_MENU, ServiceProperties.APPS_MENU + CyModel.MENU_NAME);
			aboutTaskProperties.put(ServiceProperties.TITLE, "2) About");
			registerService(context, aboutTaskFactory, TaskFactory.class, aboutTaskProperties);

			// Exit Plug-in
			ExitPluginAction exitPluginAction = new ExitPluginAction(cyServices,
					ServiceProperties.APPS_MENU + CyModel.MENU_NAME, "3) Exit Plugin");
			registerService(context, exitPluginAction, CyAction.class, new Properties());
		});
	}
}
