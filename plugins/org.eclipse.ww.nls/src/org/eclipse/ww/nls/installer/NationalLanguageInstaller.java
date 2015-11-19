/*******************************************************************************
 * Copyright (c) 2015 Eclipse Foundation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wayne Beaton (Eclipse Foundation)- initial API and implementation
 *******************************************************************************/
package org.eclipse.ww.nls.installer;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IBundleGroupProvider;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ww.nls.Activator;
import org.osgi.framework.ServiceReference;

public class NationalLanguageInstaller {

	public void install(Display display) {
		display.asyncExec(new Runnable() {
			
			@Override
			public void run() {
				doInstall(display);
			}
		});
	}
	
	private void doInstall(Display display) {
		// TODO add more robust handling of error conditions.
		IProvisioningAgent agent;
		try {
			agent = getProvisioningAgent();
		} catch (ProvisionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		List<IInstallableUnit> units = new ArrayList<IInstallableUnit>();

		IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					queryForIUs(agent, monitor).forEach(unit -> units.add(unit));
				} catch (ProvisionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		try {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(null);
			dialog.run(true, true, op);
			if (dialog.getReturnCode() == ProgressMonitorDialog.CANCEL) return;
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
				
		InstallOperation installOperation = new InstallOperation(new ProvisioningSession(agent), units);
		
		ProvisioningUI.getDefaultUI().openInstallWizard(units, installOperation, null);
	}
	
	private IQueryResult<IInstallableUnit> queryForIUs(IProvisioningAgent agent, IProgressMonitor monitor) throws ProvisionException {
		SubMonitor submonitor = SubMonitor.convert(monitor, 2);

		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		IMetadataRepository metadataRepo = null;
		try {
			metadataRepo = manager.loadRepository(new URI(getSoftwareSiteUrl()), submonitor);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (submonitor.isCanceled()) return null;
		
		submonitor.worked(1);

		IQueryResult<IInstallableUnit> query = metadataRepo.query(getFeaturesQuery(), submonitor);
		
		submonitor.worked(1);
		
		return query;

	}

	private String getSoftwareSiteUrl() {
		return "http://download.eclipse.org/technology/babel/update-site/R0.13.0/mars";
	}

	private IQuery<IInstallableUnit> getFeaturesQuery() {
		List<IQuery<IInstallableUnit>> featureQueries = new ArrayList<IQuery<IInstallableUnit>>();
		for (IBundleGroupProvider provider : Platform.getBundleGroupProviders()) {
			for (IBundleGroup feature : provider.getBundleGroups()) {
				final String featureId = feature.getIdentifier();
				// TODO Use actual locale information in the name.
				// TODO Query for the language and country-specific or the language-specific features
				// TODO Exclude existing nl features from the query
				featureQueries.add(QueryUtil.createIUQuery(featureId + ".nl_de"));
			}
		}
		IQuery<IInstallableUnit> query = QueryUtil.createCompoundQuery(featureQueries, false);
		return QueryUtil.createLatestQuery(query);
	}

	private IProvisioningAgent getProvisioningAgent() throws ProvisionException {
		ServiceReference<?> reference = Activator.getContext()
				.getServiceReference(IProvisioningAgentProvider.SERVICE_NAME);
		IProvisioningAgentProvider agentProvider = null;
		if (reference == null)
			return null;
		agentProvider = (IProvisioningAgentProvider) Activator.getContext().getService(reference);
		return agentProvider.createAgent(null);
	}
}
