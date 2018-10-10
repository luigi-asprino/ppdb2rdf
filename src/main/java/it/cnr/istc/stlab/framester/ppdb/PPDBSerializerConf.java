package it.cnr.istc.stlab.framester.ppdb;

public class PPDBSerializerConf {

	public enum PPDBVersion {
		PPDBv1, PPDBv2
	}

	private PPDBVersion version;
	private String outDirectory;
	private String inputFile;
	private String suffix;
	private String dataPrefix;
	private boolean splitOutFile;
	private int splittingSize;

	public PPDBSerializerConf() {
		super();
	}

	public PPDBSerializerConf(PPDBVersion version, String outDirectory, String inputFile, String suffix, String dataPrefix) {
		super();
		this.version = version;
		this.outDirectory = outDirectory;
		this.inputFile = inputFile;
		this.suffix = suffix;
		this.dataPrefix = dataPrefix;
	}

	public PPDBVersion getVersion() {
		return version;
	}

	public boolean isSplitOutFile() {
		return splitOutFile;
	}

	public void setSplitOutFile(boolean splitOutFile) {
		this.splitOutFile = splitOutFile;
	}

	public int getSplittingSize() {
		return splittingSize;
	}

	public void setSplittingSize(int splittingSize) {
		this.splittingSize = splittingSize;
	}

	public void setVersion(PPDBVersion version) {
		this.version = version;
	}

	public String getOutDirectory() {
		return outDirectory;
	}

	public void setOutDirectory(String outDirectory) {
		this.outDirectory = outDirectory;
	}

	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getDataPrefix() {
		return dataPrefix;
	}

	public void setDataPrefix(String dataPrefix) {
		this.dataPrefix = dataPrefix;
	}

	@Override
	public String toString() {
		return "PPDBSerializerConf [version=" + version + ", outDirectory=" + outDirectory + ", inputFile=" + inputFile + ", suffix=" + suffix + ", dataPrefix=" + dataPrefix + ", splitOutFile=" + splitOutFile + ", splittingSize=" + splittingSize + "]";
	}

}
