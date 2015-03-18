package org.codelibs.elasticsearch.kuromoji.neologd;

import static org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.*;
import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.codelibs.elasticsearch.runner.net.Curl;
import org.codelibs.elasticsearch.runner.net.CurlResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class KuromojiNeologdPluginTest {

	private ElasticsearchClusterRunner runner;

	private File[] userDictFiles;

	private int numOfNode = 2;

	private int numOfDocs = 1000;

	@Before
	public void setUp() throws Exception {
		runner = new ElasticsearchClusterRunner();
		runner.onBuild(new ElasticsearchClusterRunner.Builder() {
			@Override
			public void build(final int number, final Builder settingsBuilder) {
				settingsBuilder.put("http.cors.enabled", true);
				settingsBuilder.put("index.number_of_replicas", 0);
				settingsBuilder.put("index.number_of_shards", 1);
			}
		}).build(newConfigs().ramIndexStore().numOfNode(numOfNode));

		userDictFiles = null;
	}

	private void updateDictionary(File file, String content)
			throws IOException, UnsupportedEncodingException,
			FileNotFoundException {
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"))) {
			bw.write(content);
			bw.flush();
		}
	}

	@After
	public void cleanUp() throws Exception {
		runner.close();
		runner.clean();
		if (userDictFiles != null) {
			for (File file : userDictFiles) {
				file.deleteOnExit();
			}
		}
	}

	@Test
	public void test_kuromoji_neologd() throws Exception {
		userDictFiles = new File[numOfNode];
		for (int i = 0; i < numOfNode; i++) {
			String confPath = runner.getNode(i).settings().get("path.conf");
			userDictFiles[i] = new File(confPath, "userdict_ja.txt");
			updateDictionary(userDictFiles[i],
					"東京スカイツリー,東京 スカイツリー,トウキョウ スカイツリー,カスタム名詞");
		}

		runner.ensureYellow();
		Node node = runner.node();

		final String index = "dataset";
		final String type = "item";

		final String indexSettings = "{\"index\":{\"analysis\":{"
				+ "\"tokenizer\":{"//
				+ "\"kuromoji_user_dict\":{\"type\":\"kuromoji_neologd_tokenizer\",\"mode\":\"extended\",\"user_dictionary\":\"userdict_ja.txt\"}"
				+ "},"//
				+ "\"analyzer\":{"
				+ "\"ja_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"kuromoji_user_dict\",\"filter\":[\"kuromoji_neologd_stemmer\"]}"
				+ "}"//
				+ "}}}";
		runner.createIndex(index,
				ImmutableSettings.builder().loadFromSource(indexSettings)
						.build());

		// create a mapping
		final XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()//
				.startObject()//
				.startObject(type)//
				.startObject("properties")//

				// id
				.startObject("id")//
				.field("type", "string")//
				.field("index", "not_analyzed")//
				.endObject()//

				// msg1
				.startObject("msg")//
				.field("type", "string")//
				.field("analyzer", "ja_analyzer")//
				.endObject()//

				.endObject()//
				.endObject()//
				.endObject();
		runner.createMapping(index, type, mappingBuilder);

		final IndexResponse indexResponse1 = runner.insert(index, type, "1",
				"{\"msg\":\"東京スカイツリー\", \"id\":\"1\"}");
		assertTrue(indexResponse1.isCreated());
		runner.refresh();

		assertDocCount(1, index, type, "msg", "東京スカイツリー");

		try (CurlResponse response = Curl.post(node, "/" + index + "/_analyze")
				.param("analyzer", "ja_analyzer").body("東京スカイツリー").execute()) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> tokens = (List<Map<String, Object>>) response
					.getContentAsMap().get("tokens");
			assertEquals("東京", tokens.get(0).get("token").toString());
			assertEquals("スカイツリ", tokens.get(1).get("token").toString());
		}

		try (CurlResponse response = Curl.post(node, "/" + index + "/_analyze")
				.param("analyzer", "ja_analyzer").body("きゃりーぱみゅぱみゅ").execute()) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> tokens = (List<Map<String, Object>>) response
					.getContentAsMap().get("tokens");
			assertEquals("きゃりーぱみゅぱみゅ", tokens.get(0).get("token").toString());
		}

	}

	private void assertDocCount(int expected, final String index,
			final String type, final String field, final String value) {
		final SearchResponse searchResponse = runner.search(index, type,
				QueryBuilders.matchQuery(field, value).type(Type.PHRASE), null,
				0, numOfDocs);
		assertEquals(expected, searchResponse.getHits().getTotalHits());
	}
}
