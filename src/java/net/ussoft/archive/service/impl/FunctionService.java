package net.ussoft.archive.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.ussoft.archive.dao.FunctionDao;
import net.ussoft.archive.model.Sys_function;
import net.ussoft.archive.service.IFunctionService;

import org.springframework.stereotype.Service;

@Service
public class FunctionService implements IFunctionService {

	
	@Resource
	private FunctionDao functionDao;
	
	/*
	 * (non-Javadoc)
	 * @see net.ussoft.archive.service.IFunctionService#getFunction(net.ussoft.archive.model.Sys_function)
	 */
	@Override
	public Sys_function getFunction(Sys_function function) {
		return functionDao.searchOne(function);
	}

	@Override
	public List<Sys_function> list() {
		return functionDao.getAll();
	}
	


}
