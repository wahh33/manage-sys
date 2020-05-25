const validatePassword = (rule, value, callback) => {
	if (!/^[0-9a-zA-Z]{6,}$/.test(value)) {
		callback(new Error('(请输入至少6位的数字字母密码！)'));
	} else {
		callback();
	}
};
const validatePhoneNum = (rule, value, callback) => {
	if (!/^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/.test(value.trim())) {
		callback(new Error('(请输入格式正确的手机号码！)'));
	} else {
		callback();
	}
};
const validateLoginName = (rule, value, callback) => {
	if (!/^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/.test(value.trim())&&value.trim().toLowerCase()!=='admin') {
		callback(new Error('(请输入格式正确的手机号码！)'));
	} else {
		callback();
	}
};
export {validatePassword,validatePhoneNum,validateLoginName};