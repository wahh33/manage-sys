import axios from 'axios';
import router from '@/router'

const http=axios.create({
	baseURL:'',
	timeout:5000
});

http.interceptors.request.use(config => {
	let token = localStorage.token;
	if(token) config.headers.common['Authorization'] = token;
	return config;
}, err => {
	return Promise.reject(err);
});

http.interceptors.response.use(res=>{
	if(res.data.code==401 || res.data.code==402){
		localStorage.token='';
		router.push({name:'Login'});
	}
	else if(res.data.data && res.data.data.token){
		localStorage.token=res.data.data.token;
	}
	return res;
},err=>{
	return Promise.reject(err)
});

export default http;
