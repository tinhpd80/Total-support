package com.cyloyalpoint.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.property.CyProperty;
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
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.undo.UndoSupport;

public class CyServices {

	private CyApplicationManager applicationManager;
	private CySwingApplication swingApplication;
	private CyServiceRegistrar serviceRegistrar;
	private TaskManager<?, ?> taskManager;
	private DialogTaskManager dialogTaskManager;
	private CyNetworkManager networkManager;
	private CyNetworkViewManager networkViewManager;
	private CyNetworkReaderManager networkViewReaderManager;
	private CyNetworkTableManager networkTableManager;
	private CyNetworkNaming networkNaming;
	private CyNetworkFactory networkFactory;
	private CyTableFactory tableFactory;
	private CyLayoutAlgorithmManager layoutAlgorithmManager;
	private CyNetworkViewFactory networkViewFactory;
	private CyRootNetworkManager rootNetworkManager;
	private VisualStyleFactory visualStyleFactory;
	private VisualMappingFunctionFactory discreteMappingFunctionFactory;
	private VisualMappingFunctionFactory passthroughMappingFunctionFactory;
	private VisualMappingManager visualMappingManager;
	private LoadVizmapFileTaskFactory loadVizmapFileTaskFactory;
	private UnHideAllEdgesTaskFactory unHideAllEdgesTaskFactory;
	private OpenBrowser openBrowser;
	private CyProperty<Properties> cyProperty;
	private UndoSupport undoSupport;
	private StreamUtil streamUtil;

	public CyApplicationManager getApplicationManager() {
		return applicationManager;
	}

	public void setApplicationManager(CyApplicationManager applicationManager) {
		this.applicationManager = applicationManager;
	}

	public CySwingApplication getSwingApplication() {
		return swingApplication;
	}

	public void setSwingApplication(CySwingApplication swingApplication) {
		this.swingApplication = swingApplication;
	}

	public CyServiceRegistrar getServiceRegistrar() {
		return serviceRegistrar;
	}

	public void setServiceRegistrar(CyServiceRegistrar serviceRegistrar) {
		this.serviceRegistrar = serviceRegistrar;
	}

	public TaskManager<?, ?> getTaskManager() {
		return taskManager;
	}

	public void setTaskManager(TaskManager<?, ?> taskManager) {
		this.taskManager = taskManager;
	}
	
	public DialogTaskManager getDialogTaskManager() {
		return dialogTaskManager;
	}

	public void setDialogTaskManager(DialogTaskManager dialogTaskManager) {
		this.dialogTaskManager = dialogTaskManager;
	}

	public CyNetworkManager getNetworkManager() {
		return networkManager;
	}

	public void setNetworkManager(CyNetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	public CyNetworkViewManager getNetworkViewManager() {
		return networkViewManager;
	}

	public void setNetworkViewManager(CyNetworkViewManager networkViewManager) {
		this.networkViewManager = networkViewManager;
	}

	public CyNetworkReaderManager getNetworkViewReaderManager() {
		return networkViewReaderManager;
	}

	public void setNetworkViewReaderManager(CyNetworkReaderManager networkViewReaderManager) {
		this.networkViewReaderManager = networkViewReaderManager;
	}

	public CyNetworkTableManager getNetworkTableManager() {
		return networkTableManager;
	}

	public void setNetworkTableManager(CyNetworkTableManager networkTableManager) {
		this.networkTableManager = networkTableManager;
	}

	public CyNetworkNaming getNetworkNaming() {
		return networkNaming;
	}

	public void setNetworkNaming(CyNetworkNaming networkNaming) {
		this.networkNaming = networkNaming;
	}

	public CyNetworkFactory getNetworkFactory() {
		return networkFactory;
	}

	public void setNetworkFactory(CyNetworkFactory networkFactory) {
		this.networkFactory = networkFactory;
	}

	public CyTableFactory getTableFactory() {
		return tableFactory;
	}

	public void setTableFactory(CyTableFactory tableFactory) {
		this.tableFactory = tableFactory;
	}

	public CyLayoutAlgorithmManager getLayoutAlgorithmManager() {
		return layoutAlgorithmManager;
	}

	public void setLayoutAlgorithmManager(CyLayoutAlgorithmManager layoutAlgorithmManager) {
		this.layoutAlgorithmManager = layoutAlgorithmManager;
	}

	public CyNetworkViewFactory getNetworkViewFactory() {
		return networkViewFactory;
	}

	public void setNetworkViewFactory(CyNetworkViewFactory networkViewFactory) {
		this.networkViewFactory = networkViewFactory;
	}

	public CyRootNetworkManager getRootNetworkManager() {
		return rootNetworkManager;
	}

	public void setRootNetworkManager(CyRootNetworkManager rootNetworkManager) {
		this.rootNetworkManager = rootNetworkManager;
	}

	public VisualStyleFactory getVisualStyleFactory() {
		return visualStyleFactory;
	}

	public void setVisualStyleFactory(VisualStyleFactory visualStyleFactory) {
		this.visualStyleFactory = visualStyleFactory;
	}

	public VisualMappingFunctionFactory getDiscreteMappingFunctionFactory() {
		return discreteMappingFunctionFactory;
	}

	public void setDiscreteMappingFunctionFactory(VisualMappingFunctionFactory discreteMappingFunctionFactory) {
		this.discreteMappingFunctionFactory = discreteMappingFunctionFactory;
	}

	public VisualMappingFunctionFactory getPassthroughMappingFunctionFactory() {
		return passthroughMappingFunctionFactory;
	}

	public void setPassthroughMappingFunctionFactory(VisualMappingFunctionFactory passthroughMappingFunctionFactory) {
		this.passthroughMappingFunctionFactory = passthroughMappingFunctionFactory;
	}

	public VisualMappingManager getVisualMappingManager() {
		return visualMappingManager;
	}

	public void setVisualMappingManager(VisualMappingManager visualMappingManager) {
		this.visualMappingManager = visualMappingManager;
	}

	public LoadVizmapFileTaskFactory getLoadVizmapFileTaskFactory() {
		return loadVizmapFileTaskFactory;
	}

	public void setLoadVizmapFileTaskFactory(LoadVizmapFileTaskFactory loadVizmapFileTaskFactory) {
		this.loadVizmapFileTaskFactory = loadVizmapFileTaskFactory;
	}

	public UnHideAllEdgesTaskFactory getUnHideAllEdgesTaskFactory() {
		return unHideAllEdgesTaskFactory;
	}

	public void setUnHideAllEdgesTaskFactory(UnHideAllEdgesTaskFactory unHideAllEdgesTaskFactory) {
		this.unHideAllEdgesTaskFactory = unHideAllEdgesTaskFactory;
	}

	public OpenBrowser getOpenBrowser() {
		return openBrowser;
	}

	public void setOpenBrowser(OpenBrowser openBrowser) {
		this.openBrowser = openBrowser;
	}

	public CyProperty<Properties> getCyProperty() {
		return cyProperty;
	}

	public void setCyProperty(CyProperty<Properties> cyProperty) {
		this.cyProperty = cyProperty;
	}

	public UndoSupport getUndoSupport() {
		return undoSupport;
	}

	public void setUndoSupport(UndoSupport undoSupport) {
		this.undoSupport = undoSupport;
	}

	public StreamUtil getStreamUtil() {
		return streamUtil;
	}

	public void setStreamUtil(StreamUtil streamUtil) {
		this.streamUtil = streamUtil;
	}
}
