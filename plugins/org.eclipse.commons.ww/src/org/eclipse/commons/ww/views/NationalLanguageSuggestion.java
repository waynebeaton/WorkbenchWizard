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
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.epp.mpc.ui.MarketplaceUrlHandler;
import org.eclipse.epp.mpc.ui.MarketplaceUrlHandler.SolutionInstallationInfo;

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
		return String.format("<img align=\"left\" src=\"%1s\"><h3>German Language</h3><p>Your workstation is configured for German language; do you want to install the Eclipse German Language Pack?</p>", getImageUrl());
	}
	
	@Override
	public String[] getCommandStrings() {
		return new String[] {"Install"};
	}
	
	@Override
	public void doit(int command) {
		SolutionInstallationInfo info = MarketplaceUrlHandler.createSolutionInstallInfo(getMarketplaceUrl());
		if (info != null) {
			MarketplaceUrlHandler.triggerInstall(info);
		}
	}

	private String getMarketplaceUrl() {
		return "http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=2599661";
	}

}
