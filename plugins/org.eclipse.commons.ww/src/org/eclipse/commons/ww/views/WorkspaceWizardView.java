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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class WorkspaceWizardView extends ViewPart {
	public static final String ID = "org.eclipse.commons.ww.views.WorkspaceWizardView";

	List<Suggestion> suggestions = new ArrayList<Suggestion>();

	private Browser browser;

	private LocationListener locationListener;
	
	public WorkspaceWizardView() {
		// TODO Create a service tracker instead.
		suggestions.add(new NationalLanguageSuggestion());
		suggestions.add(new SetDefaultPreferencesSuggestion());
		//suggestions.add(new ExternalLinkSuggestion());
	}

	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		
		locationListener = new LocationListener() {
			
			@Override
			public void changing(LocationEvent event) {
				// TODO Not even close.
				event.doit = false;
				URI uri = null;
				try {
					uri = new URI(event.location);
					
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				final int suggestion = Integer.parseInt(uri.getPath());
				final int command = Integer.parseInt(uri.getQuery());
				
				// FIXME Maybe a workspace job. Or do we need a job?
				browser.getDisplay().asyncExec(new Runnable() {
					
					@Override
					public void run() {						
						suggestions.get(suggestion).doit(command);
					}
				});
			}
			
			@Override
			public void changed(LocationEvent event) {	
			}
		};
		
		refresh();
	}

	public void refresh() {
		browser.removeLocationListener(locationListener);
		browser.setText(this.getHtml());	
		browser.addLocationListener(locationListener);	
	}
	
	private String getHtml() {
		StringBuilder builder = new StringBuilder();
		Formatter formatter = new Formatter(builder);
		
		formatter.format("<link rel=\"stylesheet\" href=\"%1s\">", getCssUrl());
		
		int suggestionIndex = 0;
		for(Suggestion suggestion : suggestions) {
			builder.append("<div class=\"suggestion\">");
			builder.append(suggestion.getHtml());
			
			builder.append("<div class=\"action\"><p>");
			
			int commandIndex = 0;
			for(String command : suggestion.getCommandStrings()) {
				builder.append(String.format("<a href=\"doit:%2$d#%3$d\">%1s</a>", 
						command, suggestionIndex, commandIndex++));
			}
			builder.append("</p></div>");
			
			builder.append("</div>");
			suggestionIndex++;
		}
		
		builder.append(String.format("<div class=\"footer\"><img src=\"%1s\"/></div>", getEclipseLogoUrl()));
		
		formatter.close();
		return builder.toString();
	}

	private String getCssUrl() {
		try {
			URL url = new URL("platform:/plugin/org.eclipse.commons.ww/resources/suggestions.css");
			return FileLocator.toFileURL(url).toExternalForm();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getEclipseLogoUrl() {
		try {
			URL url = new URL("platform:/plugin/org.eclipse.commons.ww/images/eclipse_logo_colour.png");
			return FileLocator.toFileURL(url).toExternalForm();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
}
