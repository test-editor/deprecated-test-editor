/*******************************************************************************
 * Copyright (c) 2012 - 2015 Signal Iduna Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Signal Iduna Corporation - initial API and implementation
 * akquinet AG
 *******************************************************************************/
package org.testeditor.ui.parts.editor.view.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;

/**
 * 
 * Painter for the images and the tables.
 * 
 * @author llipinski
 */
public class PaintImageAndTableListener implements PaintObjectListener {

	private List<Grid> tables = new ArrayList<Grid>();
	private List<Integer> tableOffsets = new ArrayList<Integer>();

	private List<Button> buttons = new ArrayList<Button>();
	private List<Integer> buttonOffsets = new ArrayList<Integer>();

	private static final Logger LOGGER = Logger.getLogger(PaintImageAndTableListener.class);
	private StyledText styledText;

	/**
	 * constructor with the styledText context.
	 * 
	 * @param styledText
	 *            StyledText
	 */
	public PaintImageAndTableListener(StyledText styledText) {
		this.styledText = styledText;
	}

	@Override
	public void paintObject(PaintObjectEvent event) {
		StyleRange style = event.style;
		int start = style.start;

		if (buttonOffsets.contains(start)) {
			Button button = buttons.get(buttonOffsets.indexOf(start));
			int x = event.x;
			int y = event.y + event.ascent - style.metrics.ascent;
			int height = button.getBounds().height;
			int width = button.getBounds().width;
			try {
				button.setBounds(new Rectangle(x, y, width, height));
				button.setVisible(true);
			} catch (Exception illegalArgumentException) {
				LOGGER.error("Error drawing image :: FAILED", illegalArgumentException);
			}
			return;
		}
		if (tableOffsets.contains(start)) {
			Grid table = tables.get(tableOffsets.indexOf(start));
			int x = event.x;
			int y = event.y + event.ascent - style.metrics.ascent;
			int height = table.getBounds().height;
			int width = styledText.getSize().x - 21;
			table.setBounds(new Rectangle(0, 0, width, height));
			table.getParent().setBounds(new Rectangle(x, y, width, height));
			table.setVisible(true);
			table.getParent().setVisible(true);
			return;
		}
	}

	/**
	 * adds a button to the buttonArray to show it in the styledText.
	 * 
	 * @param button
	 *            Button
	 * @param offset
	 *            the offset of the button in the styledText
	 */
	public void addButton(Button button, int offset) {
		buttonOffsets.add(offset);
		buttons.add(button);
	}

	/**
	 * adds a table to the tableArray to show it in the styledText.
	 * 
	 * @param grid
	 *            nebula grid
	 * @param tableOffset
	 *            the offset of the table in the styledText
	 */
	public void addTable(Grid grid, int tableOffset) {
		tableOffsets.add(tableOffset);
		tables.add(grid);
	}

	/**
	 * disposes all tables.
	 */
	public void disposeAllTables() {
		for (Grid table : tables) {
			if (!table.isDisposed() && !table.getParent().isDisposed()) {
				table.getParent().dispose();
			}
			if (!table.isDisposed()) {
				table.dispose();
			}
		}
		tables = new ArrayList<Grid>();
		tableOffsets = new ArrayList<Integer>();
	}

	/**
	 * removes all buttons from the store.
	 */
	public void disposeAllButtons() {
		for (Button button : buttons) {
			button.dispose();
		}
		buttonOffsets = new ArrayList<Integer>();
		buttons = new ArrayList<Button>();
	}

	/**
	 * returns the imageOffsets as List<Integer> .
	 * 
	 * @return List<Integer> of the imageOffsets
	 */
	public List<Integer> getButtonOffsets() {
		return buttonOffsets;
	}

	/**
	 * returns the images.
	 * 
	 * @return the {@link Image[]}
	 */
	public List<Button> geButtons() {
		return buttons;
	}

	/**
	 * 
	 * @return the count of the stored images
	 */
	public int getButtonCount() {
		return buttons.size();
	}

}
