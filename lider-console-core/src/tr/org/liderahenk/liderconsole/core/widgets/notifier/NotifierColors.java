/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation 
 *******************************************************************************/
package tr.org.liderahenk.liderconsole.core.widgets.notifier;

import org.eclipse.swt.graphics.Color;

import tr.org.liderahenk.liderconsole.core.utils.SWTGraphicUtil;

/**
 * This class is a simple POJO that holds colors used by the Notifier widget
 * 
 */
public class NotifierColors {
	Color titleColor;
	Color textColor;
	Color borderColor;
	Color leftColor;
	Color rightColor;

	void dispose() {
		SWTGraphicUtil.safeDispose(this.titleColor);
		SWTGraphicUtil.safeDispose(this.borderColor);
		SWTGraphicUtil.safeDispose(this.leftColor);
		SWTGraphicUtil.safeDispose(this.rightColor);
		SWTGraphicUtil.safeDispose(this.textColor);
	}
}
