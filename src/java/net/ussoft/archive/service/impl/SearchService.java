package net.ussoft.archive.service.impl;

/**
 * 搜索服务实现类
 * @author guodh
 */

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.Sys_templetfield;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.ISearchService;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.lucene.IKAnalyzer;

@Service
public class SearchService implements ISearchService {
	
	@Resource
	private TreeDao treeDao;
	@Resource
	private HttpServletRequest request;
	
	private String indexDir = "/LUCENE";

	/*
	 * 全文检索
	 */
	
	@SuppressWarnings("unchecked")
	public HashMap searchNumber(String keyword, String docserverid, String treeid) {
		//索引文件
		String path = indexDir + "/" + docserverid;
		IndexSearcher indexSearch = null;

		String fileIndexDir = request.getSession().getServletContext().getRealPath(path) + File.separator;
		try {
			Directory dir = new SimpleFSDirectory(new File(fileIndexDir));
			IndexReader reader = IndexReader.open(dir);
			//创建 IndexSearcher对象
			indexSearch = new IndexSearcher(reader);
			//创建一个分词器,和创建索引时用的分词器要一致
			// Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);  //默认的分词（汉字单个分。英文按词分）
			Analyzer analyzer = new IKAnalyzer();
			Query query = new TermQuery(new Term("treeid", treeid));
			//检索的字段
			String[] fields = {"content","creater","docoldname","docext","createtime"};
			QueryParser qp1 = new MultiFieldQueryParser(Version.LUCENE_36,fields, analyzer);
			//设置检索的条件.OR_OPERATOR表示"或"
			qp1.setDefaultOperator(QueryParser.AND_OPERATOR);
			Query query1 = qp1.parse(keyword);
			BooleanQuery m_BooleanQuery = new BooleanQuery();// 得到一个组合检索对象
			//权限字段
//			setSearchAuthor(m_BooleanQuery, fMap, analyzer);
			m_BooleanQuery.add(query1, BooleanClause.Occur.MUST);
			m_BooleanQuery.add(query, BooleanClause.Occur.MUST);
			// 搜索结果 TopDocs里面有scoreDocs[]数组，里面保存着索引值
			TopDocs hits = indexSearch.search(m_BooleanQuery, 30);
			// hits.totalHits表示一共搜到多少个
			System.out.println(treeid+"找到了" + hits.totalHits + "个");
			
			HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
			resultMap.put(treeid, hits.totalHits);
			
			indexSearch.close();
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				indexSearch.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/*
	 * 全文检索
	 */
	
	@SuppressWarnings("unchecked")
	public HashMap search(String keyword, String docserverid, List<Sys_tree> treeList,int currentPage,
			int pageSize) {
		
		if (currentPage <= 0) {
			currentPage = 1;
        }
        if (pageSize <= 0) {
            pageSize = 10;
        }
        //生成返回map
		HashMap resultMap = new HashMap();
		// 声明返回用的list
		List resultList = new ArrayList();
		
		int start = (currentPage - 1) * pageSize;

		String path = indexDir + "/" + docserverid;
		IndexSearcher indexSearch = null;

		String fileIndexDir = request.getSession().getServletContext().getRealPath(path) + File.separator;
		try {
			if (!(new File(fileIndexDir).isDirectory())) {
				resultMap.put("DATA", resultList);
				resultMap.put("ROWCOUNT", 0);
				resultMap.put("PAGES", 0);
				return resultMap;
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		try {
			Directory dir = new SimpleFSDirectory(new File(fileIndexDir));

			IndexReader reader = IndexReader.open(dir);
			// 创建 IndexSearcher对象
			indexSearch = new IndexSearcher(reader);

			// 创建一个分词器,和创建索引时用的分词器要一致
			Analyzer analyzer = new IKAnalyzer();
			// 得到一个组合检索对象
			BooleanQuery m_BooleanQuery = new BooleanQuery();
			BooleanQuery all_BooleanQuery = new BooleanQuery();

			//循环得到当前帐户能访问的节点
			for (Sys_tree tree : treeList) {
				Query query = new TermQuery(new Term("treeid",tree.getId()));
				m_BooleanQuery.add(query, BooleanClause.Occur.SHOULD);
			}
			all_BooleanQuery.add(m_BooleanQuery,BooleanClause.Occur.MUST);
			
			String[] fields = {"content","creater","docoldname","docext","createtime"};

			if (null != keyword && !"".equals(keyword)) {
				QueryParser qp1 = new MultiFieldQueryParser(Version.LUCENE_36,
						fields, analyzer);// 检索content列
				qp1.setDefaultOperator(QueryParser.AND_OPERATOR);
				Query query1 = qp1.parse(keyword);
//				m_BooleanQuery.add(query1, BooleanClause.Occur.MUST);
				all_BooleanQuery.add(query1, BooleanClause.Occur.MUST);
				
			}
			
			SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
					"<span style='color:red'>", "</span>");
//			Highlighter highlighter = new Highlighter(simpleHTMLFormatter,
//					new QueryScorer(m_BooleanQuery));
			Highlighter highlighter = new Highlighter(simpleHTMLFormatter,
					new QueryScorer(all_BooleanQuery));
			
			

			int hm = start + pageSize;
            TopScoreDocCollector res = TopScoreDocCollector.create(hm, false);
//			indexSearch.search(m_BooleanQuery, res);
			indexSearch.search(all_BooleanQuery, res);
			
			int rowCount = res.getTotalHits();
			int pages = (rowCount - 1) / pageSize + 1; //计算总页数
			TopDocs hits = res.topDocs(start, pageSize);
			
			// 搜索结果 TopDocs里面有scoreDocs[]数组，里面保存着索引值
//			TopDocs hits = indexSearch.search(m_BooleanQuery, 30);
			// hits.totalHits表示一共搜到多少个
			System.out.println("找到了" + hits.totalHits + "个");

			// 循环hits.scoreDocs数据，并使用indexSearch.doc方法把Document还原
			for (int i = 0; i < hits.scoreDocs.length; i++) {
				ScoreDoc sdoc = hits.scoreDocs[i];
				Document doc = indexSearch.doc(sdoc.doc);
				
				HashMap map = new HashMap();
				
				map.put("docid", doc.get("id"));
				map.put("treeid", doc.get("treeid"));
				map.put("fileid", doc.get("fileid"));
				map.put("tableid", doc.get("tableid"));
				
				//加入所属档案树名称，供前台显示用
				Sys_tree tree = treeDao.get(doc.get("treeid"));
				map.put("treename", tree.getTreename());
				
				//高亮文件名
				highlighter.setTextFragmenter(new SimpleFragmenter(doc.get("docoldname").length()));
				TokenStream tkDocOldName = analyzer.tokenStream("docoldname",new StringReader(doc.get("docoldname")));
				String hDocOldName = highlighter.getBestFragment(tkDocOldName, doc.get("docoldname"));
				if (null != hDocOldName && !"".equals(hDocOldName)) {
					map.put("docoldname", hDocOldName);
				}
				else {
					map.put("docoldname", doc.get("docoldname"));
				}
				//高亮文件扩展名
				highlighter.setTextFragmenter(new SimpleFragmenter(doc.get("docext").length()));
				TokenStream tkDocExt = analyzer.tokenStream("docext",new StringReader(doc.get("docext")));
				String hDocExt = highlighter.getBestFragment(tkDocExt, doc.get("docext"));
				if (null != hDocExt && !"".equals(hDocExt)) {
					map.put("docext", hDocExt);
				}
				else {
					map.put("docext", doc.get("docext"));
				}
				
				map.put("doclength", doc.get("doclength"));
				
				//高亮创建者
				highlighter.setTextFragmenter(new SimpleFragmenter(doc.get("creater").length()));
				TokenStream tkCreater = analyzer.tokenStream("creater",new StringReader(doc.get("creater")));
				String hCreater = highlighter.getBestFragment(tkCreater, doc.get("creater"));
				if (null != hCreater && !"".equals(hCreater)) {
					map.put("creater", hCreater);
				}
				else {
					map.put("creater", doc.get("creater"));
				}
				
				//高亮创建时间
				highlighter.setTextFragmenter(new SimpleFragmenter(doc.get("createtime").length()));
				TokenStream tkCreatetime = analyzer.tokenStream("createtime",new StringReader(doc.get("createtime")));
				String hCreatetime = highlighter.getBestFragment(tkCreatetime, doc.get("createtime"));
				if (null != hCreatetime && !"".equals(hCreatetime)) {
					map.put("createtime", hCreatetime);
				}
				else {
					map.put("createtime", doc.get("createtime"));
				}
				
				//高亮文件摘要
				highlighter.setTextFragmenter(new SimpleFragmenter(200));
				TokenStream tksummary = analyzer.tokenStream("content",new StringReader(doc.get("content")));
				String hsummary = highlighter.getBestFragment(tksummary, doc.get("content"));
				if (null != hsummary && !"".equals(hsummary)) {
					map.put("summary", hsummary);
				}
				else {
//					map.put("summary", doc.get("content"));
					map.put("summary", "");
				}
				
				//高亮文件内容
				highlighter.setTextFragmenter(new SimpleFragmenter(doc.get("content").length()));
				TokenStream tkContent = analyzer.tokenStream("content",new StringReader(doc.get("content")));
				String hContent = highlighter.getBestFragment(tkContent, doc.get("content"));
				if (null != hContent && !"".equals(hContent)) {
					map.put("content", hContent);
				}
				else {
					map.put("content", doc.get("content"));
				}
				resultList.add(map);
			}
			indexSearch.close();
			resultMap.put("DATA", resultList);
			resultMap.put("ROWCOUNT", rowCount);
			resultMap.put("PAGES", pages);
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				indexSearch.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public HashMap searchNumber(String tableName, String[] fields,
			String searchTxt, String treeid,HashMap<String, String> fMap) {
		String path = indexDir + "/" + tableName;
		IndexSearcher indexSearch = null;
		String tableIndexDir = request.getSession().getServletContext().getRealPath(path) + File.separator;

		try {
			Directory dir = new SimpleFSDirectory(new File(tableIndexDir));

			IndexReader reader = IndexReader.open(dir);
			// 创建 IndexSearcher对象
			indexSearch = new IndexSearcher(reader);

			// 创建一个分词器,和创建索引时用的分词器要一致
			// Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);  //默认的分词（汉字单个分。英文按词分）
			Analyzer analyzer = new IKAnalyzer();
			
			Query query = new TermQuery(new Term("treeid", treeid));
			
			QueryParser qp1 = new MultiFieldQueryParser(Version.LUCENE_36,fields, analyzer);
			// 设置检索的条件.OR_OPERATOR表示"或"
			qp1.setDefaultOperator(QueryParser.AND_OPERATOR);
			Query query1 = qp1.parse(searchTxt);

			BooleanQuery m_BooleanQuery = new BooleanQuery();// 得到一个组合检索对象

			//权限字段
			setSearchAuthor(m_BooleanQuery, fMap, analyzer);
			
			m_BooleanQuery.add(query1, BooleanClause.Occur.MUST);
			m_BooleanQuery.add(query, BooleanClause.Occur.MUST);
//			m_BooleanQuery.add(q, BooleanClause.Occur.MUST);
			// 搜索结果 TopDocs里面有scoreDocs[]数组，里面保存着索引值
			TopDocs hits = indexSearch.search(m_BooleanQuery, 30);
			// hits.totalHits表示一共搜到多少个
//			System.out.println("找到了" + hits.totalHits + "个");
			HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
			resultMap.put(treeid, hits.totalHits);
			indexSearch.close();
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				indexSearch.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public HashMap searchNumber(String tableName,BooleanQuery booleanQuery, String treeid) {
		try {  
//        	String path = "E:\\Program Files\\apache-tomcat-6.0.29\\webapps\\archive1\\LUCENE\\A_C50BD8FC_01";
			
			String path = indexDir + "/" + tableName;
			String tableIndexDir = request.getSession().getServletContext().getRealPath(path) + File.separator;
			
			File file = new File(tableIndexDir);
            Directory mdDirectory = FSDirectory.open(file);  
            IndexReader reader = IndexReader.open(mdDirectory);  
            IndexSearcher searcher = new IndexSearcher(reader);  
            
            
            TopDocs tops = searcher.search(booleanQuery, null, 10);  
            
            int count = tops.totalHits;  
            System.out.println("totalHits="+count);  //数量
            
            HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
			resultMap.put(treeid, tops.totalHits);
			
//            ScoreDoc[] docs = tops.scoreDocs;  
//            for(int i=0;i<docs.length;i++){  
//                Document doc = searcher.doc(docs[i].doc);  
//                //int id = Integer.parseInt(doc.get("id"));  
//                String title = doc.get("gddw");  
//                String tm = doc.get("tm");  
////                String publishTime = doc.get("publishTime");  
////                String source = doc.get("source");  
////                String category = doc.get("category");  
////                float reputation = Float.parseFloat(doc.get("reputation"));  
//                  
//                System.out.println("归档单位："+title+"  题名："+tm);  
//            }  
              
            reader.close();  
            searcher.close();  
            return resultMap;
            
        } catch (CorruptIndexException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.yapu.archive.service.itf.ISearchService#search(java.lang.String, java.util.List, java.lang.String, int, int)
	 */
	
	@SuppressWarnings("unchecked")
	public HashMap search(String tableName,List<Sys_templetfield> tmpList, String searchTxt,int currentPage,int pageSize)
			throws IOException {
		
		if (currentPage <= 0) {
			currentPage = 1;
        }
        if (pageSize <= 0) {
            pageSize = 20;
        }
      //生成返回map
		HashMap resultMap = new HashMap();
		// 声明返回用的list
		List resultList = new ArrayList();
		
        int start = (currentPage - 1) * pageSize;

		String path = indexDir + "/" + tableName;
		IndexSearcher indexSearch = null;

		String tableIndexDir = request.getSession().getServletContext().getRealPath(path) + File.separator;

		try {
			if (!(new File(tableIndexDir).isDirectory())) {
				resultMap.put("DATA", resultList);
				resultMap.put("ROWCOUNT", 0);
				resultMap.put("PAGES", 0);
				return resultMap;
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		//临时list。用来创建查询字段数组
		List<String> searchFieldsList = new ArrayList<String>();
		
		List<String> fieldsList = new ArrayList<String>();
		
		for (int i=0;i<tmpList.size();i++) {
			fieldsList.add(tmpList.get(i).getEnglishname().toLowerCase());
			if (tmpList.get(i).getIssearch() == 1) {
				searchFieldsList.add(tmpList.get(i).getEnglishname().toLowerCase());
			}
		}
		//生成查询字段
		String[] fields = new String[searchFieldsList.size()];
		searchFieldsList.toArray(fields);
		
		try {
			Directory dir = new SimpleFSDirectory(new File(tableIndexDir));

			IndexReader reader = IndexReader.open(dir);
			// 创建 IndexSearcher对象
			indexSearch = new IndexSearcher(reader);

			// 创建一个分词器,和创建索引时用的分词器要一致
			// Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
			Analyzer analyzer = new IKAnalyzer();

			// 创建QueryParser对象,第一个参数表示Lucene的版本,第二个表示搜索Field的字段,第三个表示搜索使用分词器
			QueryParser queryParser = new MultiFieldQueryParser(
					Version.LUCENE_36, fields, analyzer);
			queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
			// 生成Query对象
			Query query = queryParser.parse(searchTxt); // 查询这个词
			
			BooleanQuery m_BooleanQuery = new BooleanQuery();// 得到一个组合检索对象
			m_BooleanQuery.add(query, BooleanClause.Occur.MUST);
			SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
					"<span style='color:red'>", "</span>");
			Highlighter highlighter = new Highlighter(simpleHTMLFormatter,
					new QueryScorer(m_BooleanQuery));

			// 搜索结果 TopDocs里面有scoreDocs[]数组，里面保存着索引值
			int hm = start + pageSize;
            TopScoreDocCollector res = TopScoreDocCollector.create(hm, false);
			indexSearch.search(m_BooleanQuery, res);
//			TopDocs hits = indexSearch.search(m_BooleanQuery, 30);
			int rowCount = res.getTotalHits();
			int pages = (rowCount - 1) / pageSize + 1; //计算总页数
			TopDocs hits = res.topDocs(start, pageSize);
			
			// hits.totalHits表示一共搜到多少个
			System.out.println("找到了" + hits.totalHits + "个");

			// 循环hits.scoreDocs数据，并使用indexSearch.doc方法把Document还原，再拿出对应的字段的值
			for (int i = 0; i < hits.scoreDocs.length; i++) {
				ScoreDoc sdoc = hits.scoreDocs[i];
				Document doc = indexSearch.doc(sdoc.doc);
				
				//设置高亮
				HashMap map = new HashMap();
				for (int j = 0; j < tmpList.size(); j++) {
					Sys_templetfield field = tmpList.get(j);
					if (field.getIssearch() == 1) {
						String str = doc.get(field.getEnglishname().toLowerCase());
						highlighter.setTextFragmenter(new SimpleFragmenter(str.length()));
						TokenStream tk = analyzer.tokenStream(field.getEnglishname().toLowerCase(),new StringReader(str));
						String htext = highlighter.getBestFragment(tk, str);
						if (null != htext && !"".equals(htext)) {
							map.put(field.getEnglishname().toLowerCase(),htext);
						}
						else {
							map.put(field.getEnglishname().toLowerCase(),doc.get(field.getEnglishname().toLowerCase()));
						}
					}
					else {
						map.put(field.getEnglishname().toLowerCase(),doc.get(field.getEnglishname().toLowerCase()));
					}
//					map.put(fieldsList.get(j).toString(),
//							doc.get(fieldsList.get(j)));
				}
				resultList.add(map);
			}
			indexSearch.close();
			
			resultMap.put("DATA", resultList);
			resultMap.put("ROWCOUNT", rowCount);
			resultMap.put("PAGES", pages);
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				indexSearch.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.yapu.archive.service.itf.ISearchService#search(java.lang.String, java.util.List, java.lang.String, java.lang.String, int, int)
	 */
	@SuppressWarnings("unchecked")
	public HashMap search(String tableName, List<Sys_templetfield> tmpList, String searchTxt, String treeid,int currentPage,int pageSize,HashMap<String, String> fMap) {

		if (currentPage <= 0) {
			currentPage = 1;
        }
        if (pageSize <= 0) {
            pageSize = 20;
        }
      //生成返回map
		HashMap resultMap = new HashMap();
		// 声明返回用的list
		List resultList = new ArrayList();
		
		int start = (currentPage - 1) * pageSize;

		String path = indexDir + "/" + tableName;
		IndexSearcher indexSearch = null;

		String tableIndexDir = request.getSession().getServletContext().getRealPath(path) + File.separator;

		try {
			if (!(new File(tableIndexDir).isDirectory())) {
				resultMap.put("DATA", resultList);
				resultMap.put("ROWCOUNT", 0);
				resultMap.put("PAGES", 0);
				return resultMap;
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		List<String> searchFieldsList = new ArrayList<String>();
		List<String> fieldsList = new ArrayList<String>();
		
		for (int i=0;i<tmpList.size();i++) {
			fieldsList.add(tmpList.get(i).getEnglishname().toLowerCase());
			if (tmpList.get(i).getIssearch() == 1) {
				searchFieldsList.add(tmpList.get(i).getEnglishname().toLowerCase());
			}
		}
		//生成查询字段
		String[] fields = new String[searchFieldsList.size()];
		searchFieldsList.toArray(fields);

		try {
			Directory dir = new SimpleFSDirectory(new File(tableIndexDir));

			IndexReader reader = IndexReader.open(dir);
			// 创建 IndexSearcher对象
			indexSearch = new IndexSearcher(reader);

			// 创建一个分词器,和创建索引时用的分词器要一致
			// Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
			Analyzer analyzer = new IKAnalyzer();
			// 得到一个组合检索对象
			BooleanQuery m_BooleanQuery = new BooleanQuery();

//			QueryParser parser = new QueryParser(Version.LUCENE_36, "treeid",
//					analyzer);
			Query query = new TermQuery(new Term("treeid", treeid));
			m_BooleanQuery.add(query, BooleanClause.Occur.MUST);

			//权限字段
//			http://www.cnblogs.com/bluepoint2009/archive/2012/10/07/lucene-QueryParser.html
//			if(fMap != null){
//				Set<Map.Entry<String, String>> set = fMap.entrySet();
//				BooleanQuery bQuery = new BooleanQuery(); //权限字段关系组合对象
//				//读取设置的权限字段
//		        for (Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext();) {
//		            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
//		            System.out.println(entry.getKey() + "--->" + entry.getValue());
//		           
//		            String[] f = {entry.getKey().toLowerCase()};
//					QueryParser q = new MultiFieldQueryParser(Version.LUCENE_36,f, analyzer);// 检索content列
//					q.setDefaultOperator(QueryParser.AND_OPERATOR);
//					q.parse(entry.getValue());
//					Query qu = q.parse(entry.getValue());
//					bQuery.add(qu, BooleanClause.Occur.MUST); //权限字段关系（AND）
//		        }
//		        m_BooleanQuery.add(bQuery, BooleanClause.Occur.MUST);	//把权限字段关系组合对象添加到组合检索对象
//			}
			//权限字段
			setSearchAuthor(m_BooleanQuery, fMap, analyzer);
			
			if (null != searchTxt && !"".equals(searchTxt)) {
				QueryParser qp1 = new MultiFieldQueryParser(Version.LUCENE_36,
						fields, analyzer);// 检索content列
				qp1.setDefaultOperator(QueryParser.AND_OPERATOR);
				Query query1 = qp1.parse(searchTxt);

				m_BooleanQuery.add(query1, BooleanClause.Occur.MUST);
			}
			
			
			
			SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
					"<span style='color:red'>", "</span>");
			Highlighter highlighter = new Highlighter(simpleHTMLFormatter,
					new QueryScorer(m_BooleanQuery));

			int hm = start + pageSize;
            TopScoreDocCollector res = TopScoreDocCollector.create(hm, false);
			indexSearch.search(m_BooleanQuery, res);
			
			int rowCount = res.getTotalHits();
			int pages = (rowCount - 1) / pageSize + 1; //计算总页数
			TopDocs hits = res.topDocs(start, pageSize);
			
			// 搜索结果 TopDocs里面有scoreDocs[]数组，里面保存着索引值
//			TopDocs hits = indexSearch.search(m_BooleanQuery, 30);
			// hits.totalHits表示一共搜到多少个
			System.out.println("找到了" + hits.totalHits + "个");

			// 循环hits.scoreDocs数据，并使用indexSearch.doc方法把Document还原，再拿出对应的字段的值
			for (int i = 0; i < hits.scoreDocs.length; i++) {
				ScoreDoc sdoc = hits.scoreDocs[i];
				Document doc = indexSearch.doc(sdoc.doc);
				
				//设置高亮
				HashMap map = new HashMap();
				for (int j = 0; j < tmpList.size(); j++) {
					Sys_templetfield field = tmpList.get(j);
					if (field.getIssearch() == 1) {
						String str = doc.get(field.getEnglishname().toLowerCase());
//						highlighter.setTextFragmenter(new SimpleFragmenter(str.length()));
						highlighter.setTextFragmenter(new SimpleFragmenter(2000));
						TokenStream tk = analyzer.tokenStream(field.getEnglishname().toLowerCase(),new StringReader(str));
						String htext = highlighter.getBestFragment(tk, str);
						if (null != htext && !"".equals(htext)) {
							map.put(field.getEnglishname().toLowerCase(),htext);
						}
						else {
							map.put(field.getEnglishname().toLowerCase(),doc.get(field.getEnglishname().toLowerCase()));
						}
					}
					else {
						map.put(field.getEnglishname().toLowerCase(),doc.get(field.getEnglishname().toLowerCase()));
					}
				}
				resultList.add(map);
			}
			indexSearch.close();
			resultMap.put("DATA", resultList);
			resultMap.put("ROWCOUNT", rowCount);
			resultMap.put("PAGES", pages);
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				indexSearch.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void setTreeDao(TreeDao treeDao) {
		this.treeDao = treeDao;
	}
	
	/**
	 * 权限字段
	 * @param booleanQuery 关系组合对象
	 * @param fMap 权限字段集合
	 * @param analyzer 分词
	 * */
	public void setSearchAuthor(BooleanQuery booleanQuery,HashMap<String, String> fMap,Analyzer analyzer){
		//http://www.cnblogs.com/bluepoint2009/archive/2012/10/07/lucene-QueryParser.html 参考资料
		if(fMap != null){
			try {
				Set<Map.Entry<String, String>> set = fMap.entrySet();
				BooleanQuery bQuery = new BooleanQuery(); //权限字段关系组合对象
				//读取设置的权限字段
				for (Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext();) {
				    Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
//				    System.out.println(entry.getKey() + "--->" + entry.getValue());
				   
				    String[] f = {entry.getKey().toLowerCase()};
					QueryParser q = new MultiFieldQueryParser(Version.LUCENE_36,f, analyzer);// 检索content列
					q.setDefaultOperator(QueryParser.AND_OPERATOR);
					q.parse(entry.getValue());
					Query qu = q.parse(entry.getValue());
					bQuery.add(qu, BooleanClause.Occur.MUST); //权限字段关系（AND）
				}
				booleanQuery.add(bQuery, BooleanClause.Occur.MUST);	//把权限字段关系组合对象添加到组合检索对象
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
}
