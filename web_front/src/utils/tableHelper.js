const tableMethods=function(url){
	return {
		getPage(){
			let self=this;
			self.tableLoading=true;
			self.$http({
				method:'post',
				url:url,
				data:self.pageModel
			}).then(res=>{
				if(res.data.code===200){
					self.pageResult=res.data.data;
					self.tableLoading=false;
				}
				else self.tableError(res.data.message);
			}).catch(err=>self.tableError(err.message));
		},
		searchPage(){
			let self=this;
			self.pageModel.filter={...self.filter};
			self.getPage();
		},
		resetPage(){
			let self=this;
			self.filter={...self.filterEmpty};
			self.pageModel.filter={...self.filter};
			self.getPage();
		},
		selectPage(index){
			let self=this;
			self.pageModel.start=index;
			self.getPage();
		},
		changeLength(length){
			let self=this;
			self.pageModel.length=length;
			self.getPage();
		},
		sortPage({column,key,order}){
			let self=this;
			self.pageModel.sortColumn=key;
			self.pageModel.sortType=order=='normal'?'asc':order;
			self.getPage();
		},
		tableError(content){
			let self=this;
			self.$Modal.error({
			            title: '获取表格数据出错！',
			            content: content
			        });
		}
	}
};
const tableDatas=function(filterModel,sortColumn,sortType="asc",start=1,length=5){
	return {
		tableLoading:true,
		pageSizeOpts:[5,10,20,40],
		filter:{...filterModel},
		filterEmpty:{...filterModel},
		pageModel:{
			filter:{...filterModel},
			start:start,
			length:length,
			sortColumn:sortColumn,
			sortType:sortType
		},
		pageResult:{
			data:[],
			total:0,
			pageNum:0,
			pageSize:0
		},
	}
};
export {tableMethods,tableDatas};