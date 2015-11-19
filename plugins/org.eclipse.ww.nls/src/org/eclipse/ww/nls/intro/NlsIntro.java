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
package org.eclipse.ww.nls.intro;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.eclipse.ww.nls.installer.NationalLanguageInstaller;

public class NlsIntro implements IIntroAction {

	@Override
	public void run(IIntroSite site, Properties params) {
		new NationalLanguageInstaller().install(site.getShell().getDisplay());
	}

}
