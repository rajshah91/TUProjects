package com.oppscience.sgevt.ged.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oppscience.sgevt.ged.model.ScotDataSheet;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@SuppressWarnings("all")
public class ScotService {

	final static ObjectMapper mapper = new ObjectMapper();

	final String indexName = "scot";

	private final RestHighLevelClient client;

	protected static final Logger logger = LoggerFactory.getLogger(ScotService.class);

	public ScotService(@Qualifier("esClient") RestHighLevelClient client) {
		this.client = client;
	}

	public void saveScot(ScotDataSheet scotDataSheet) {
		try {
			IndexRequest indexRequest = new IndexRequest(indexName).id(scotDataSheet.getSirenCP());
			// transform pojo to ES object
			final byte[] bytes = mapper.writeValueAsBytes(scotDataSheet);
			indexRequest.source(bytes, XContentType.JSON);
			client.index(indexRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<ScotDataSheet> findAll(int from,int size) {
		try {
			SearchRequest srequest = new SearchRequest(indexName);
			srequest.source().query(QueryBuilders.matchAllQuery());
			srequest.source().from(from);
			srequest.source().size(size);
			SearchResponse results = client.search(srequest, RequestOptions.DEFAULT);
			return getSearchResult(results);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ScotDataSheet findById(String sirenCp) {
		try {
			GetResponse docResponse = client.get(new GetRequest(indexName).id(sirenCp), RequestOptions.DEFAULT);
			return docResponse.isExists() ? mapper.readValue(docResponse.getSourceAsBytes(), ScotDataSheet.class) : null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deleteById(String sirenCp) {
		try {
			client.delete(new DeleteRequest().index(indexName).id(sirenCp), RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean existsById(String sirenCp) {
		return findById(sirenCp) != null;
	}

	private List<ScotDataSheet> getSearchResult(SearchResponse response) {
//		for (SearchHit hit : results.getHits().getHits()) {
//			System.out.println("hit = " + hit.getSourceAsMap());
//		}
		SearchHit[] searchHit = response.getHits().getHits();
		List<ScotDataSheet> scotDocuments = new ArrayList<>();
		if (searchHit.length > 0) {
			Arrays.stream(searchHit)
					.forEach(hit -> scotDocuments.add(mapper.convertValue(hit.getSourceAsMap(), ScotDataSheet.class)));
		}
		return scotDocuments;
	}
}
