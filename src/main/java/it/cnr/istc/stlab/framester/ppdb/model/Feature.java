package it.cnr.istc.stlab.framester.ppdb.model;

public class Feature {

	private String featureName, feature;

	public Feature(String featureName, String feature) {
		super();
		this.featureName = featureName;
		this.feature = feature;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

}
