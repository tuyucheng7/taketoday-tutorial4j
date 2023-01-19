package cn.tuyucheng.taketoday.apache.opennlp;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.junit.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class POSTaggerUnitTest {

	@Test
	public void givenPOSModel_whenPOSTagging_thenPOSAreDetected() throws Exception {

		SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
		String[] tokens = tokenizer.tokenize("John has a sister named Penny.");

		InputStream inputStreamPOSTagger = getClass().getResourceAsStream("/models/en-pos-maxent.bin");
		POSModel posModel = new POSModel(inputStreamPOSTagger);
		POSTaggerME posTagger = new POSTaggerME(posModel);
		String tags[] = posTagger.tag(tokens);
		assertThat(tags).contains("NNP", "VBZ", "DT", "NN", "VBN", "NNP", ".");
	}
}
