package it.cnr.istc.stlab.framester.ppdb;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.jena.ext.com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.istc.stlab.framester.loader.Loader;
import it.cnr.istc.stlab.framester.ppdb.PPDBSerializerConf.PPDBVersion;
import it.cs.unibo.lgu.commons.ontology.OntologyCommons;

public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);

	private static void loadPPDB(String outputFolder, String resultingGraphUri, String jdbcURL, String user, String pass) {

		File outputFolderF = new File(outputFolder);

		for (File f : outputFolderF.listFiles()) {
			if (OntologyCommons.isAnOntologyExtension(f.getAbsolutePath())) {
				Loader l = new Loader(f.getAbsolutePath(), Lists.newArrayList(resultingGraphUri));
				l.loadOnVirtuoso(jdbcURL, user, pass);
				logger.info("{} loaded on {}!", f.getAbsolutePath(), jdbcURL);
			}
		}
	}

	public static void main(String[] args) {
		logger.info("Starting Paraphrase Serialization");
		Configurations configs = new Configurations();
		try {
			Configuration config = configs.properties(new File(args[0]));
			PPDBSerializerConf conf = new PPDBSerializerConf();
			conf.setDataPrefix(config.getString("dataPrefix"));

			int v = config.getInt("version");
			if (v == 1) {
				conf.setVersion(PPDBVersion.PPDBv1);
			} else if (v == 2) {
				conf.setVersion(PPDBVersion.PPDBv2);
			}

			conf.setOutDirectory(config.getString("outDirectory"));
			conf.setInputFile(config.getString("inputFile"));
			conf.setSuffix(config.getString("suffix"));
			conf.setSplitOutFile(config.getBoolean("splitOutFile"));
			conf.setSplittingSize(config.getInt("splittingSize"));

			logger.info("Configuration {}", conf.toString());

			new PPDBSerializer(conf).serialize();

			loadPPDB(config.getString("outDirectory"), config.getString("graphURI"), config.getString("jdbcURL"), config.getString("virtuoso.user"), config.getString("virtuoso.password"));

		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		logger.info("Paraphrase Serialization end");
	}

}
