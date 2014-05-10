package net.ussoft.archive.service.impl;
/**
 * 索引服务
 */

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import net.ussoft.archive.model.Sys_doc;
import net.ussoft.archive.service.IIndexerService;
import net.ussoft.archive.util.lucene.Indexer;

@Service
public class IndexerService implements IIndexerService {

	public void createIndex(String tablename, List fieldList, List dataList,
			String openMode) {
		Indexer indexer = new Indexer();
		indexer.CreateIndex(tablename, fieldList, dataList, openMode);

	}

	public String createIndex(String docServerid, List<Sys_doc> docList,
			HashMap<String, String> contentMap, String openMode) {
		Indexer indexer = new Indexer();
		return indexer.CreateIndex(docServerid, docList, contentMap, openMode);
	}

}
