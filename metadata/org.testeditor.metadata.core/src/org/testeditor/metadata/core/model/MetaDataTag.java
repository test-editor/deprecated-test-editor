package org.testeditor.metadata.core.model;

public class MetaDataTag {

	private MetaDataValue metaDataValue;

	public MetaDataTag() {
	}

	public MetaDataValue getMetaDataValue() {
		return metaDataValue;
	}

	public void setMetaDataValue(MetaDataValue metaDataValue) {
		this.metaDataValue = metaDataValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((metaDataValue == null) ? 0 : metaDataValue.hashCode());
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
		MetaDataTag other = (MetaDataTag) obj;
		if (metaDataValue == null) {
			if (other.metaDataValue != null)
				return false;
		} else if (!metaDataValue.equals(other.metaDataValue))
			return false;
		return true;
	}

}
