package org.testeditor.metadata.core.model;

import java.util.ArrayList;
import java.util.List;

import org.testeditor.core.model.teststructure.TestProject;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class MetaDataStore {

	private List<MetaDataValueList> list;
	@XStreamOmitField
	private TestProject project;

	public MetaDataStore() {
		list = new ArrayList<MetaDataValueList>();
	}

	public List<MetaDataValueList> getList() {
		return list;
	}

	public TestProject getProject() {
		return project;
	}

	public void setProject(TestProject project) {
		this.project = project;
	}

}
