 //控制层
app.controller('itemController' ,function($scope,$controller ,$http){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //修改商品数量按钮处理
	$scope.addNum=(x)=>{
		$scope.num += x;
		if($scope.num < 1){
			$scope.num = 1;
		}
	}
	//定义用户选择的规格对象
	$scope.specificationList={};
	 //当用户点击某个规格选项时向指定的变量中添加内容
	$scope.addSpec=(key,value)=>{
		$scope.specificationList[key]=value;
		$scope.selectSku();
	}
	//当用户点击某个规格时判断是否选中此规格
	$scope.isSelected=(key,value)=>{
		return $scope.specificationList[key]==value;
	}
	//加载sku列表中is_default倒序第一个的商品列表
	$scope.loadSku=()=>{
		alert(skuList)
		//1.将sku商品列表的第一项设置给全局变量
		$scope.sku=skuList[0];
		//2.设置当前选中项的值
		$scope.specificationList=JSON.parse(JSON.stringify($scope.sku.spec));

	}
	//比较两个json对象是否相等
	matchObject=(map1,map2)=>{
		for(var k in map1){
			if(map1[k] != map2[k]){
				return false;
			}
		}
		for(var k in map2){
			if(map2[k] != map1[k]){
				return false;
			}
		}
		return true;
	}
	$scope.selectSku=()=>{
		//1.遍历skuList列表,将其中的spec对象与当前用户选择的$scope.specificationList进行比较
		//如果相等就得到此对象对应的skuList中的某项,并且重新给$scope.sku赋值
		for(var i = 0;i < skuList.length;i++){
			if(matchObject(skuList[i].spec,$scope.specificationList)){
				$scope.sku=skuList[i];
				return;
			}
		}
		$scope.sku={id:0,title:'--------',price:0};//如果没有匹配的		
	}
	//定义添加到购物车方法
	$scope.addCart=()=>{
		alert("hello")
		//{'withCredentials':true}：代表在前端设置可以跨域访问后台
		$http.get("http://localhost:9107/cart/addGoodsToCartList.do?itemId="+$scope.sku.id+"&num="+$scope.num,{'withCredentials':true}).success(response=>{
			if(response.success){ //如果添加购物车成功就跳转到购物车列表页面
				location.href="http://localhost:9107/cart.html";
			}else{
				alert(response.message);
			}
		})
	}
});	
