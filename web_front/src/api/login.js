import http from './index.js';

function LoginByPhone(phoneNum,password){
	const data={
		phoneNum,
		password
	};
	return http({
		method:'post',
		url:'/api/user/loginByPhone',
		data
	});
};
function GetInfo(){
	return http({
		method:'get',
		url:'/api/user/getUserInfo',
	});
};
function ChangePassword(password){
	const data={
		password
	};
	return http({
		method:'post',
		url:'/api/user/changePassword',
		data
	});
}
export {LoginByPhone,GetInfo,ChangePassword};