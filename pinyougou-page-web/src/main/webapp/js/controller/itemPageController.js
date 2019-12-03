app.controller('itemPageController',function($scope,$http){

	//数量操作
	//初始化
	$scope.num = 1;
	$scope.addNum=function(x){
		$scope.num += x;
		if($scope.num<1){
			$scope.num=1;
			}
	}


	//选中规格参数列表
	$scope.selectSpecification = function(name,value) {
		//用户选择规格
		$scope.selectSpecification[name] = value;
		searchSku();
	}
	// 判断某规格选项是否被用户选中
	$scope.isSelected = function (name,value) {
		// 
		if ($scope.selectSpecification[name] == value) {
			return true;
		}
		return false;
	}

	//加载sku商品信息
	$scope.loadSku = function() {
		//设置默认值为第一个
		$scope.sku = skuList[0];
		//将规格参数列表进行深克隆
		$scope.selectSpecification = JSON.parse(JSON.stringify($scope.sku.spec));
	}

	//匹配两个对象
	matchObject=function(map1,map2){
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}
		}
		for(var n in map2){
			if(map2[n]!=map1[n]){
				return false;
			}
		}
		return true;
	}

   //查询对象
   searchSku=function(){
		for(var i=0;i<skuList.length;i++){
			if(matchObject(skuList[i].spec,$scope.specItem)){
				$scope.sku=skuList[i];
				return;
			}
		}
		$scope.sku={id:0,title:'--------',price:0};//如果没有匹配的
	}

	//定义添加到购物车方法
	$scope.addCart=()=> {
		alert("hello")
		//{'withCredentials':true}：代表在前端设置可以跨域访问后台
		$http.get("http://localhost:9107/cart/addGoodsToCartList.do?itemId=" + $scope.sku.id + "&num=" + $scope.num, {'withCredentials': true}).success(response => {
			if (response.success) { //如果添加购物车成功就跳转到购物车列表页面
				location.href = "http://localhost:9107/cart.html";
			} else {
				alert(response.message);
			}
		})
	}

});
