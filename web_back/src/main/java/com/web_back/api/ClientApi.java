package com.web_back.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web_back.entity.Client;
import com.web_back.service.ClientService;
import com.web_back.utils.ActionResult;
import com.web_back.utils.BaseApi;
import com.web_back.utils.PageModel;

@RestController
@RequestMapping("/api/client")
public class ClientApi extends BaseApi {
	@Autowired
	ClientService clientService;

	@PostMapping("/getClientPage")
	public ActionResult getClientPage(@RequestBody PageModel<Client> pageModel){
		return success("",clientService.getClientPage(pageModel));
	}
	
	@PostMapping("/saveClient")
	public ActionResult saveClient(@RequestBody Client client){
		return success("",clientService.saveClient(client));
	}
	
	@PostMapping("/deleteClient")
	public ActionResult deleteClient(@RequestBody Client client){
		return success("",clientService.deleteClient(client.getId()));
	}
	
	@GetMapping("/getAllClient")
	public ActionResult getAllClient(){
		return success("",clientService.getClient(null));
	}

	@PostMapping("/getClientInfoPage")
	public ActionResult getClientInfoPage(@RequestBody PageModel<Client> pageModel){
		return success("",clientService.getClientInfoPage(pageModel));
	}
}
