package org.testeditor.metadata.core.model;

import java.util.ArrayList;
import java.util.List;

import org.testeditor.core.model.teststructure.TestStructure;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class MetaDataTagList {

	private List<MetaDataTag> tags;
	@XStreamOmitField
	private TestStructure testcase;

	public MetaDataTagList() {
		tags = new ArrayList<MetaDataTag>();
	}

	public List<MetaDataTag> getTags() {
		return tags;
	}

	public TestStructure getTestcase() {
		return testcase;
	}

	public void setTestcase(TestStructure testcase) {
		this.testcase = testcase;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result + ((testcase == null) ? 0 : testcase.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaDataTagList other = (MetaDataTagList) obj;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		if (testcase == null) {
			if (other.testcase != null)
				return false;
		} else if (!testcase.equals(other.testcase))
			return false;
		return true;
	}

}
