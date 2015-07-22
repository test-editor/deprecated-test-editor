package org.testeditor.aml.dsl.e4.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.testeditor.aml.dsl.ui.internal.AmlActivator;

import com.google.inject.Injector;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.testeditor.aml.dsl.e4.ui"; //$NON-NLS-1$

	private static Activator plugin;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getInstance() {
		return plugin;
	}
	
	public Injector getInjector() {
		return AmlActivator.getInstance().getInjector(AmlActivator.ORG_TESTEDITOR_AML_DSL_AML);
	}

}
