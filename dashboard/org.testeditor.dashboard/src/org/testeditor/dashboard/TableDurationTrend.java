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
package org.testeditor.dashboard;

import java.awt.Color;
import java.awt.GradientPaint;
import java.sql.Time;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.testeditor.ui.constants.ColorConstants;

/**
 * @author alebedev
 * 
 *         table shows duration trend for in LastRunsTAble selected test case or
 *         suite now limited to display to not more than 30 runs
 */
public class TableDurationTrend {

	@Inject
	private TranslationService translationService;

	/**
	 * The contributor URI.
	 */
	public static final String CONTRIBUTOR_URI = "platform:/plugin/org.testeditor.dashboard";
	/**
	 * Chart composite.
	 */
	private ChartComposite chartComposite;
	/**
	 * Chart for the duration trend.
	 */
	private JFreeChart chart;

	/**
	 * constructor.
	 */
	@Inject
	public TableDurationTrend() {
	}

	/**
	 * creates data set from durations and dates retrieved from results list.
	 * 
	 * @param objektList
	 *            list of all suite GoogleSucheSuite runs <AllRunsResult>
	 * @return dataset for graph
	 */
	private CategoryDataset createDataset(List<AllRunsResult> objektList) {

		// row keys...
		final String series = objektList.get(0).getFilePath().getName();
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		int datesToLoad = objektList.size();
		if (datesToLoad > 30) {
			datesToLoad = 30;
		}
		for (int i = 0; i < datesToLoad; i++) {
			// Duration column keys
			int durationValue = objektList.get(i).getDuration();
			// Date column keys
			String date = objektList.get(i).getDate();
			// create the dataset...
			dataset.addValue(durationValue, series, date);
		}
		return dataset;

	}

	/**
	 * Formatting duration(ms) in h:m:s:ms.
	 * 
	 * @param number
	 *            for time formatting
	 * @return durationFormat
	 */
	private String formatDuration(double number) {
		// TODO: refactor - see formatDuration in MyLabelProvider
		Time time = new Time((long) number);
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		String durationFormat = cal.get(Calendar.HOUR) - 1 + "h:" + cal.get(Calendar.MINUTE) + "m:"
				+ cal.get(Calendar.SECOND) + "s:" + cal.get(Calendar.MILLISECOND) + "ms";
		return durationFormat;
	}

	/**
	 * disposes DurationTable when project, test case or suite change.
	 * 
	 * @param tableToRefresh
	 *            table
	 * @param modelService
	 *            to find part label
	 * @param window
	 *            trimmed window
	 * @param app
	 *            org.eclipse.e4.ide.application
	 */
	@Inject
	@Optional
	public void disposeEvent(@UIEventTopic("DisposeChartTable") String tableToRefresh, EModelService modelService,
			MWindow window, MApplication app) {
		if (chartComposite != null) {
			chartComposite.dispose();
			MPart mPart = (MPart) modelService.find("org.testeditor.ui.part.2", app);
			mPart.setLabel(translationService.translate("%dashboard.table.label.duration", CONTRIBUTOR_URI));
			mPart.setTooltip(translationService.translate("%dashboard.table.label.duration.tooltip.no.test.selected",
					CONTRIBUTOR_URI));
		}
	}

	/**
	 * designs and creates graph from data sets.
	 * 
	 * @param objektList
	 *            list of all suite GoogleSucheSuite runs <AllRunsResult>
	 * @param parent
	 *            composite parent
	 * @param modelService
	 *            to find part label
	 * @param window
	 *            trimmed window
	 * @param app
	 *            org.eclipse.e4.ide.application
	 */
	@SuppressWarnings({ "serial" })
	@Inject
	@Optional
	public void createControls(@UIEventTopic("Testobjektlist") List<AllRunsResult> objektList, Composite parent,
			EModelService modelService, MWindow window, MApplication app) {
		MPart mPart = (MPart) modelService.find("org.testeditor.ui.part.2", app);
		String[] arr = objektList.get(0).getFilePath().getName().split("\\.");
		String filenameSplitted = arr[arr.length - 1];
		mPart.setLabel(translationService.translate("%dashboard.table.label.duration", CONTRIBUTOR_URI) + " "
				+ filenameSplitted);
		mPart.setTooltip(translationService.translate("%dashboard.table.label.duration", CONTRIBUTOR_URI) + " "
				+ objektList.get(0).getFilePath().getName());
		parent.setLayout(new FillLayout());

		// create the chart...
		chart = ChartFactory.createBarChart3D(null, // chart
													// title
				translationService.translate("%dashboard.table.label.duration.axis.dates", CONTRIBUTOR_URI), // domain
				// X axis
				// label
				translationService.translate("%dashboard.table.label.duration.axis.duration", CONTRIBUTOR_URI)
						+ " h:m:s:ms", // range
				// Y axis
				// label
				createDataset(objektList), // data
				PlotOrientation.VERTICAL, // orientation
				false, // include legend
				true, // tooltips?
				false // URLs?
				);

		// get a reference to the plot for further customisation...
		final CategoryPlot plot = chart.getCategoryPlot();
		// y axis right
		plot.setRangeAxisLocation(0, AxisLocation.BOTTOM_OR_RIGHT);
		// set the range axis to display integers only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setNumberFormatOverride(new NumberFormat() { // show duration
																// values in
																// h:m:s:ms
					@Override
					public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
						// return new StringBuffer(String.format("%f", number));
						return new StringBuffer(String.format(formatDuration(number)));
					}

					@Override
					public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
						// return new StringBuffer(String.format("%f", number));
						return new StringBuffer(String.format(formatDuration(number)));
					}

					@Override
					public Number parse(String source, ParsePosition parsePosition) {
						return null;
					}
				});

		// disable bar outlines...
		final BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("{1} "
				+ translationService.translate("%dashboard.table.label.duration.axis.duration", CONTRIBUTOR_URI)
				+ ": {2}ms", NumberFormat.getInstance()));
		renderer.setDrawBarOutline(false);
		renderer.setMaximumBarWidth(.15);
		final CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(1.57));

		Color color = toAwtColor(ColorConstants.COLOR_BLUE);
		final GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, color, 0, 0, color);
		renderer.setSeriesPaint(0, gp0);

		chartComposite = new ChartComposite(parent, SWT.EMBEDDED);
		chartComposite.setSize(800, 800);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		chartComposite.setLayoutData(data);
		chartComposite.setHorizontalAxisTrace(false);
		chartComposite.setVerticalAxisTrace(false);
		chartComposite.setChart(chart);
		chartComposite.pack(true);
		chartComposite.setVisible(true);
		chartComposite.forceRedraw();
		parent.layout();
	}

	/**
	 * composes awt color from swt color RGB.
	 * 
	 * @param color
	 *            SWT
	 * @return java.awt.Color
	 */
	public static java.awt.Color toAwtColor(org.eclipse.swt.graphics.Color color) {
		return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
	}

}
