package com.web_back.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.web_back.entity.Client;
import com.web_back.mapper.IClientMapper;
import com.web_back.utils.PageModel;
import com.web_back.utils.PageResult;

@Service("ClientService")
public class ClientService {
	@Autowired
	private IClientMapper clientMapper;
	
	public List<Client> getClient(Client client){
		return clientMapper.getClient(client);
	}
	
	public PageResult getClientPage(PageModel<Client> clientPage) {
		Page<Client> pageInfo=PageHelper.startPage(clientPage.getStart(),clientPage.getLength(),clientPage.getSortColumn()+" "+clientPage.getSortType());
		return new PageResult(getClient(clientPage.getFilter()),pageInfo.getTotal(),pageInfo.getPageNum(),pageInfo.getPageSize());
	} 
	
	public Integer saveClient(Client client) {
		Integer clientId=client.getId();
		if(clientId!=null) {
			return clientMapper.updateClient(client)>0?clientId:null;
		}else {
			return clientMapper.addClient(client)>0?client.getId():null;
		}
	}
	
	public boolean deleteClient(int id) {
		Client clientParams=new Client();
		clientParams.setId(id);
		clientParams.setDeleted(true);
		return clientMapper.updateClient(clientParams)>0?true:false;
	}

	public List<Client> getClientInfo(Client client){
		return clientMapper.getClientInfo(client);
	}
	
	public PageResult getClientInfoPage(PageModel<Client> clientPage) {
		Page<Client> pageInfo=PageHelper.startPage(clientPage.getStart(),clientPage.getLength(),clientPage.getSortColumn()+" "+clientPage.getSortType());
		return new PageResult(getClientInfo(clientPage.getFilter()),pageInfo.getTotal(),pageInfo.getPageNum(),pageInfo.getPageSize());
	} 
}
