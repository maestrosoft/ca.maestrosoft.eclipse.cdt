/*******************************************************************************
 * Copyright (c) 2005, 2009 Intel Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intel Corporation - Initial API and implementation
 *******************************************************************************/
package ca.maestrosoft.eclipse.cdt.plugin.studio.option.ui;


/**
 * @noextend This class is not intended to be subclassed by clients.
 */
public interface IFileListChangeListener {
	void fileListChanged(MaestroFileListControl fileList, String oldValue[], String newValue[]);
}
