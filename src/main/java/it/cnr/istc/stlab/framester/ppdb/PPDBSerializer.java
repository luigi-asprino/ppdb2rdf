package it.cnr.istc.stlab.framester.ppdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.istc.stlab.framester.ppdb.model.Feature;
import it.cs.unibo.lgu.commons.file.FileUtils;

public class PPDBSerializer {

	private static Logger logger = LoggerFactory.getLogger(PPDBSerializer.class);

	private PPDBSerializerConf conf;

	public PPDBSerializer(PPDBSerializerConf conf) {
		this.conf = conf;
	}

	public void serialize() {

		BufferedReader br;

		logger.info("Converting {}", conf.getInputFile());
		logger.info("Output folder {}", conf.getOutDirectory());

		try {
			File outDirectory = new File(conf.getOutDirectory());
			if (outDirectory.exists()) {
				FileUtils.deleteFoler(conf.getOutDirectory());
			}
			outDirectory.mkdirs();
			int fileN = 0;

			FileOutputStream fos_pp, fos_phrase, fos_feature, fos_featureType, fos_alignment, fos_entailment;
			if (conf.isSplitOutFile()) {
				fos_pp = new FileOutputStream(new File(conf.getOutDirectory() + "/PPDB_ParaphrasePair_" + fileN + "_" + conf.getSuffix().replace("/", "_") + ".nt"));
				fos_phrase = new FileOutputStream(new File(conf.getOutDirectory() + "/PPDB_Phrase_" + fileN + "_" + conf.getSuffix().replace("/", "_") + ".nt"));
				fos_feature = new FileOutputStream(new File(conf.getOutDirectory() + "/PPDB_Feature_" + fileN + "_" + conf.getSuffix().replace("/", "_") + ".nt"));
				fos_featureType = new FileOutputStream(new File(conf.getOutDirectory() + "/PPDB_FeatureType_" + conf.getSuffix().replace("/", "_") + ".nt"));
				fos_alignment = new FileOutputStream(new File(conf.getOutDirectory() + "/PPDB_Alignment_" + conf.getSuffix().replace("/", "_") + ".nt"));
				fos_entailment = new FileOutputStream(new File(conf.getOutDirectory() + "/PPDB_Entailment_" + conf.getSuffix().replace("/", "_") + ".nt"));

			} else {
				fos_pp = new FileOutputStream(new File(conf.getOutDirectory() + "/PPDB_ParaphrasePair_" + conf.getSuffix().replace("/", "_") + ".nt"));
				fos_phrase = new FileOutputStream(new File(conf.getOutDirectory() + "/PPDB_Phrase_" + conf.getSuffix().replace("/", "_") + ".nt"));
				fos_feature = new FileOutputStream(new File(conf.getOutDirectory() + "/PPDB_Feature_" + conf.getSuffix().replace("/", "_") + ".nt"));
				fos_featureType = new FileOutputStream(new File(conf.getOutDirectory() + "/PPDB_FeatureType_" + conf.getSuffix().replace("/", "_") + ".nt"));
				fos_alignment = new FileOutputStream(new File(conf.getOutDirectory() + "/PPDB_Alignment_" + conf.getSuffix().replace("/", "_") + ".nt"));
				fos_entailment = new FileOutputStream(new File(conf.getOutDirectory() + "/PPDB_Entailment_" + conf.getSuffix().replace("/", "_") + ".nt"));
			}

			Model featureTypeModel = ModelFactory.createDefaultModel();
			Model alignmentModel = ModelFactory.createDefaultModel();
			Model entailmentModel = ModelFactory.createDefaultModel();

			br = new BufferedReader(new FileReader(conf.getInputFile()));
			String line = null;
			int row_number = 0;
			while ((line = br.readLine()) != null) {
				String[] row = line.split("\\|\\|\\|");
				row = trimArray(row);
				switch (conf.getVersion()) {
				case PPDBv1:
					getModelPPDBv1_ParaphrasePair(row, conf.getDataPrefix() + row_number, row_number, conf.getDataPrefix()).write(fos_pp, "NT");
					getModelPPDBv2_Phrase(row, conf.getDataPrefix() + row_number, row_number, conf.getDataPrefix()).write(fos_phrase, "NT");
					getModelPPDBv2_Feature(row, conf.getDataPrefix() + row_number, row_number, conf.getDataPrefix()).write(fos_feature, "NT");
					featureTypeModel.add(getModelPPDBv2_FeautureType(row, conf.getDataPrefix() + row_number, row_number, conf.getDataPrefix()));
					break;
				case PPDBv2:
					getModelPPDBv2_ParaphrasePair(row, conf.getDataPrefix() + row_number, row_number, conf.getDataPrefix()).write(fos_pp, "NT");
					getModelPPDBv2_Phrase(row, conf.getDataPrefix() + row_number, row_number, conf.getDataPrefix()).write(fos_phrase, "NT");
					getModelPPDBv2_Feature(row, conf.getDataPrefix() + row_number, row_number, conf.getDataPrefix()).write(fos_feature, "NT");
					featureTypeModel.add(getModelPPDBv2_FeautureType(row, conf.getDataPrefix() + row_number, row_number, conf.getDataPrefix()));
					alignmentModel.add(getModelPPDBv2_Alignment(row, conf.getDataPrefix() + row_number, row_number, conf.getDataPrefix()));
					entailmentModel.add(getModelPPDBv2_Entailment(row, conf.getDataPrefix() + row_number, row_number, conf.getDataPrefix()));
					break;
				}

				row_number++;
				if (row_number % 10000 == 0) {
					logger.info("Line " + row_number);
				}

				if (conf.isSplitOutFile() && row_number % conf.getSplittingSize() == 0) {
					fileN++;
					fos_pp.flush();
					fos_phrase.flush();
					fos_feature.flush();
					fos_pp.close();
					fos_phrase.close();
					fos_feature.close();
					fos_pp = new FileOutputStream(new File(conf.getOutDirectory() + "/PPDB_ParaphrasePair_" + fileN + "_" + conf.getSuffix().replace("/", "_") + ".nt"));
					fos_phrase = new FileOutputStream(new File(conf.getOutDirectory() + "/PPDB_Phrase_" + fileN + "_" + conf.getSuffix().replace("/", "_") + ".nt"));
					fos_feature = new FileOutputStream(new File(conf.getOutDirectory() + "/PPDB_Feature_" + fileN + "_" + conf.getSuffix().replace("/", "_") + ".nt"));
				}

			}
			featureTypeModel.write(fos_featureType, "NT");
			alignmentModel.write(fos_alignment, "NT");
			entailmentModel.write(fos_entailment, "NT");

			fos_pp.flush();
			fos_phrase.flush();
			fos_feature.flush();
			fos_pp.close();
			fos_phrase.close();
			fos_feature.close();

			fos_featureType.flush();
			fos_featureType.close();

			fos_alignment.flush();
			fos_alignment.close();

			fos_entailment.flush();
			fos_entailment.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String[] trimArray(String[] arr) {
		String[] result = new String[arr.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = arr[i].trim();
		}
		return result;
	}

	private static String urlify(String prefix, String s) throws UnsupportedEncodingException {
		String result = prefix + URLEncoder.encode(s.replaceAll("-", ""), "UTF-8");
		return result;
	}

	private static String prefixParaphraseOntology = "http://w3id.org/ppdb/ontology/";
	private static String paraphrasePairClassURI = prefixParaphraseOntology + "ParaphrasePair";
	private static String phraseClassURI = prefixParaphraseOntology + "Phrase";
	private static String contentURI = prefixParaphraseOntology + "content";
	private static String constituent = prefixParaphraseOntology + "constituent";
	private static String hasPhrase_1URI = prefixParaphraseOntology + "hasPhrase_1";
	private static String hasPhrase_2URI = prefixParaphraseOntology + "hasPhrase_2";
	private static String hasParaphraseURI = prefixParaphraseOntology + "hasParaphrase";
	private static String hasEntailmentRelationURI = prefixParaphraseOntology + "hasEntailmentRelation";
	private static String EntailmentRelationURI = prefixParaphraseOntology + "EntailmentRelation";
	private static String FeatureTypeURI = prefixParaphraseOntology + "FeatureType";
	private static String FeatureURI = prefixParaphraseOntology + "Feature";
	private static String featureValueDatypePropertyURI = prefixParaphraseOntology + "featureValue";
	private static String hasFeatureTypeURI = prefixParaphraseOntology + "hasFeatureType";
	private static String hasFeatureURI = prefixParaphraseOntology + "hasFeature";
	private static String hasAlignmentURI = prefixParaphraseOntology + "hasAlignment";
	private static String AlignmentURI = prefixParaphraseOntology + "Alignment";

	private static Model getModelPPDBv2_Phrase(String[] row, String uriParaphrasePair, int id, String prefixParaphrasedata) throws UnsupportedEncodingException {

		Model result = ModelFactory.createDefaultModel();

		String uriPhrase1 = urlify(prefixParaphrasedata, row[1]);
		result.add(result.createResource(uriPhrase1), RDF.type, result.createResource(phraseClassURI));
		result.add(result.createResource(uriPhrase1), result.createProperty(contentURI), result.createLiteral(row[1]));
		result.add(result.createResource(uriPhrase1), RDFS.label, result.createLiteral(row[1]));

		String uriPhrase2 = urlify(prefixParaphrasedata, row[2]);
		result.add(result.createResource(uriPhrase2), RDF.type, result.createResource(phraseClassURI));
		result.add(result.createResource(uriPhrase2), result.createProperty(contentURI), result.createLiteral(row[2]));
		result.add(result.createResource(uriPhrase2), RDFS.label, result.createLiteral(row[2]));

		result.add(result.createResource(uriPhrase1), result.createProperty(hasParaphraseURI), result.createResource(uriPhrase2));
		result.add(result.createResource(uriPhrase2), result.createProperty(hasParaphraseURI), result.createResource(uriPhrase1));

		return result;
	}

	private static Model getModelPPDBv2_ParaphrasePair(String[] row, String uriParaphrasePair, int id, String prefixParaphrasedata) throws UnsupportedEncodingException {

		Model result = ModelFactory.createDefaultModel();

		result.add(result.createResource(uriParaphrasePair), RDF.type, result.createResource(paraphrasePairClassURI));
		result.add(result.createResource(uriParaphrasePair), result.createProperty(constituent), result.createLiteral(row[0]));

		String uriPhrase1 = urlify(prefixParaphrasedata, row[1]);
		result.add(result.createResource(uriParaphrasePair), result.createProperty(hasPhrase_1URI), result.createResource(uriPhrase1));

		String uriPhrase2 = urlify(prefixParaphrasedata, row[2]);
		result.add(result.createResource(uriParaphrasePair), result.createProperty(hasPhrase_2URI), result.createResource(uriPhrase2));

		for (Feature f : getFeatures(row[3].trim())) {
			String featureValueUri = urlify(prefixParaphrasedata, id + "_" + f.getFeatureName());
			result.add(result.createResource(uriParaphrasePair), result.createProperty(hasFeatureURI), result.createResource(featureValueUri));
		}

		String uriAlignment = urlify(prefixParaphrasedata, row[4]);
		result.add(result.createResource(uriParaphrasePair), result.createProperty(hasAlignmentURI), result.createResource(uriAlignment));

		String uriEntailmentRelation = urlify(prefixParaphrasedata, row[5].trim());
		result.add(result.createResource(uriParaphrasePair), result.createProperty(hasEntailmentRelationURI), result.createResource(uriEntailmentRelation));

		return result;
	}

	private static List<Feature> getFeatures(String featureString) {
		List<Feature> result = new ArrayList<Feature>();
		try {
			for (String s : featureString.split(" ")) {
				String[] f = s.split("=");
				Feature feature = new Feature(f[0], f[1]);
				result.add(feature);
			}
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			// in PPDB 1.0 sometimes feature are not valid, e.g. "Alignment= "
		}
		return result;

	}

	private static Model getModelPPDBv1_ParaphrasePair(String[] row, String uriParaphrasePair, int id, String prefixParaphrasedata) throws UnsupportedEncodingException {

		Model result = ModelFactory.createDefaultModel();

		result.add(result.createResource(uriParaphrasePair), RDF.type, result.createResource(paraphrasePairClassURI));
		result.add(result.createResource(uriParaphrasePair), result.createProperty(constituent), result.createLiteral(row[0]));

		String uriPhrase1 = urlify(prefixParaphrasedata, row[1]);
		result.add(result.createResource(uriParaphrasePair), result.createProperty(hasPhrase_1URI), result.createResource(uriPhrase1));

		String uriPhrase2 = urlify(prefixParaphrasedata, row[2]);
		result.add(result.createResource(uriParaphrasePair), result.createProperty(hasPhrase_2URI), result.createResource(uriPhrase2));

		for (Feature f : getFeatures(row[3].trim())) {
			String featureValueUri = urlify(prefixParaphrasedata, id + "_" + f.getFeatureName());
			result.add(result.createResource(uriParaphrasePair), result.createProperty(hasFeatureURI), result.createResource(featureValueUri));
		}

		return result;
	}

	private static Model getModelPPDBv2_Alignment(String[] row, String uriParaphrasePair, int id, String prefixParaphrasedata) throws UnsupportedEncodingException {

		Model result = ModelFactory.createDefaultModel();

		String uriAlignment = urlify(prefixParaphrasedata, row[4]);
		result.add(result.createResource(uriAlignment), RDF.type, result.createResource(AlignmentURI));
		result.add(result.createResource(uriAlignment), RDFS.label, result.createLiteral(row[4]));

		return result;
	}

	private static Model getModelPPDBv2_Feature(String[] row, String uriParaphrasePair, int id, String prefixParaphrasedata) throws UnsupportedEncodingException {

		Model result = ModelFactory.createDefaultModel();

		for (Feature f : getFeatures(row[3].trim())) {
			String featureTypeUri = urlify(prefixParaphrasedata, f.getFeatureName());

			String featureValueUri = urlify(prefixParaphrasedata, id + "_" + f.getFeatureName());
			result.add(result.createResource(featureValueUri), RDF.type, result.createResource(FeatureURI));
			result.add(result.createResource(featureValueUri), RDFS.label, result.createLiteral(f.getFeatureName() + " = " + f.getFeature()));
			result.add(result.createResource(featureValueUri), result.createProperty(featureValueDatypePropertyURI), result.createLiteral(f.getFeature()));
			result.add(result.createResource(featureValueUri), result.createProperty(hasFeatureTypeURI), result.createResource(featureTypeUri));

		}

		return result;
	}

	private static Model getModelPPDBv2_FeautureType(String[] row, String uriParaphrasePair, int id, String prefixParaphrasedata) throws UnsupportedEncodingException {

		Model result = ModelFactory.createDefaultModel();

		for (Feature f : getFeatures(row[3].trim())) {
			String featureUri = urlify(prefixParaphrasedata, f.getFeatureName());
			result.add(result.createResource(featureUri), RDF.type, result.createResource(FeatureTypeURI));
			result.add(result.createResource(featureUri), RDFS.label, result.createLiteral(f.getFeatureName()));

		}

		return result;
	}

	private static Model getModelPPDBv2_Entailment(String[] row, String uriParaphrasePair, int id, String prefixParaphrasedata) throws UnsupportedEncodingException {

		Model result = ModelFactory.createDefaultModel();

		String uriEntailmentRelation = urlify(prefixParaphrasedata, row[5].trim());
		result.add(result.createResource(uriEntailmentRelation), RDF.type, result.createResource(EntailmentRelationURI));
		result.add(result.createResource(uriEntailmentRelation), RDFS.label, result.createLiteral(row[5]));

		return result;
	}
}
