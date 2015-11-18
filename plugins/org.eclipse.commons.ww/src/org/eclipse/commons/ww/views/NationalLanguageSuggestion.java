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
package org.eclipse.commons.ww.views;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.commons.ww.Activator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IBundleGroupProvider;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
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
import org.osgi.framework.ServiceReference;

public class NationalLanguageSuggestion implements Suggestion {

	public NationalLanguageSuggestion() {
	}

	@Override
	public String getImageUrl() {
		try {
			URL url = new URL("platform:/plugin/org.eclipse.commons.ww/images/de.png");
			return FileLocator.toFileURL(url).toExternalForm();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getHtml() {
		return String.format(
				"<img align=\"left\" src=\"%1s\"><h3>German Language</h3><p>Your workstation is configured for German language; do you want to install the Eclipse German Language Pack?</p>",
				getImageUrl());
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "Install" };
	}

	@Override
	public void doit(int command) {
		// getInstalledFeatures();
		try {
			install();
		} catch (ProvisionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void install() throws ProvisionException {
		IProvisioningAgent agent = getProvisioningAgent();
		List<IInstallableUnit> units = new ArrayList<IInstallableUnit>();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					queryForIUs(agent, null).forEach(unit -> units.add(unit));
				} catch (ProvisionException | OperationCanceledException | URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		try {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(null);
			dialog.run(true, true, op);
			if (dialog.getReturnCode() == ProgressMonitorDialog.CANCEL)
				return;
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		InstallOperation installOperation = new InstallOperation(new ProvisioningSession(agent), units);
		ProvisioningUI.getDefaultUI().openInstallWizard(units, installOperation, null);

	}
	
	private IQueryResult<IInstallableUnit> queryForIUs(IProvisioningAgent agent, IProgressMonitor monitor) throws ProvisionException, OperationCanceledException, URISyntaxException {
		SubMonitor submonitor = SubMonitor.convert(monitor, 2);

		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		IMetadataRepository metadataRepo = manager.loadRepository(
				new URI(getSoftwareSiteUrl()),
				submonitor);
		
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
