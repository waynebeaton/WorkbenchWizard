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

public class SetDefaultPreferencesSuggestion implements Suggestion {

	@Override
	public String getImageUrl() {
		try {
			URL url = new URL("platform:/plugin/org.eclipse.commons.ww/images/yes_select.png");
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
		return String.format("<img align=\"left\" src=\"%1s\"><h3>Default Preferences</h3><p>Set up shared default preferences for all Eclipse workspaces on your workstation.</p>", getImageUrl());
	}
	
	public String[] getCommandStrings() {
		return new String[] {"Launch"};
	}

	@Override
	public void doit(int command) {
		// TODO Auto-generated method stub

	}

}
