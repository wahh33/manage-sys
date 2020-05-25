const getters={
	id:({user})=>user.id,
	role:({user})=>user.role,
	name:({user})=>user.name,
	password:({user})=>user.password,
	phoneNum:({user})=>user.phoneNum,
	eMail:({user})=>user.eMail,
	img:({user})=>user.img,
	menus:({menu},getters)=>{
		let role=getters.role,
			rootMenu=[...menu.rootMenu].sort((a,b)=>a.index-b.index),
			childMenu=menu.childMenu
					.filter(child=>child.meta.roles.some(r=>r===role))
					.map(child=>{
						child={...child,...child.meta};
						delete child.meta;
						return child;
					});
		return rootMenu
				.map(root=>{
					root.children=childMenu.filter(child=>child.root===root.name).sort((a,b)=>a.index-b.index);
					return root;
				})
				.filter(root=>root.children.length!=0);
	},
}
export default getters;