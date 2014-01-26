package net.ussoft.archive.service.impl;

import javax.annotation.Resource;

import net.ussoft.archive.dao.TreeDao;
import net.ussoft.archive.model.PageBean;
import net.ussoft.archive.model.Sys_tree;
import net.ussoft.archive.service.ITreeService;

import org.springframework.stereotype.Service;


@Service
public class TreeService implements ITreeService {
	
	@Resource
	private TreeDao treeDao;

	@Override
	public Sys_tree selectById(String id) {
		return treeDao.get(id);
	}

	@Override
	public PageBean<Sys_tree> list(Sys_tree tree, PageBean<Sys_tree> p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Sys_tree insertOne(Sys_tree tree) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Sys_tree tree) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

}
