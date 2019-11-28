 //控制层 
app.controller('goodsController' ,function($scope ,$controller,$location  ,goodsService,uploadService,itemCatService,typeTemplateService){

	$controller('baseController',{$scope:$scope});//继承
	//保存
	$scope.save=function(){
        // 提取kindeditor编辑器的内容
        $scope.entity.goodsDesc.introduction = editor.html();
        //定义默认的url
        let url = "../goods/add.do";
        if ($scope.entity.goods.id !=null){
            url = "../goods/update.do";
        }
		goodsService.save(url,$scope.entity).success(
			function(response){
				if(response.success){
					//重新查询 
		        	alert("保存成功!");
		        	//清空实体对象
					$scope.entity={};
					//清空富文本编辑器的内容
					editor.html(' ');
				}else{
					alert(response.message);
				}
			}		
		);				
	}


	//定义图片实体类对象
	$scope.imageEntity = {};
        //上传文件
	$scope.uploadFile = function () {
		uploadService.uploadFile().success(
			function (response) {
				if (response.success) {
					$scope.imageEntity.url = response.message;
				} else {
					alert(response.message);
				}
			}
        )
    }

	//保存图片
	$scope.savePic=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.imageEntity);
    }

    //列表中移除图片
    $scope.removePic=function(index){
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }


    //初始化实体类对象
    $scope.entity={goods:{},goodsDesc:{itemImages:[],customAttributeItems:[],specificationItems:[]},items:[]};

    /*加载一级列表*/
    $scope.findByParentId = function (parentId) {
        itemCatService.findByParentId(parentId).success(
            (response)=>{
                $scope.itemCat1List = response;
            }
        )
    }

    /*通过监听一节列表的改变，查询对应的二级列表的值*/
	$scope.$watch("entity.goods.category1Id",function (newValue,oldValue){
		itemCatService.findByParentId(newValue).success(
			(response)=>{
				$scope.itemCat2List = response;
			}
		)
    })
    /*通过监听二级列表的改变，查询对应的三级列表的值*/
    $scope.$watch("entity.goods.category2Id",function (newValue,oldValue){
        itemCatService.findByParentId(newValue).success(
            (response)=>{
                $scope.itemCat3List = response;
            }
        )
    })
    /*通过监听三级列表的改变，查询对应的模板id值*/
    $scope.$watch("entity.goods.category3Id",function (newValue,oldValue){
        itemCatService.findOne(newValue).success(
            (response)=>{
            	// 获取对应的模板id
                $scope.entity.goods.typeTemplateId = response.typeId;

            }
        )
    })

	/*根据模板id查询对应的品牌列表和 规参数列表*/
	$scope.$watch("entity.goods.typeTemplateId",function (newValue,oldValue) {
		typeTemplateService.findOne(newValue).success(
			(response)=>{
				//获取模板对象
				$scope.typeTemplate = response;
				//获取模板对象中的 brandId 即品牌列表
				$scope.brandList=JSON.parse(response.brandIds);
				//获取扩展属性列表
				//对其进行判断此时商品id为空，则添加，商品id不为空，则进行数据回显，进行修改操作
				let id = $location.search()["goodsId"];
				if (!id){
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
                }
			}
		)
		$scope.findSpecList(newValue);
    });

	$scope.findSpecList = function (id) {
        //查询规格参数列表
        typeTemplateService.findSpecList(id).success(
            (response)=>{
                $scope.specList=response;
            }
        )
    };

	// 定义一个方法 显示规格参数列表
	// 遍历list集合，查询相关的数据，若为空，则返回null
	searchObject = function (list,key,value) {
		//遍历集合
		for (let i =0,len = list.length;i<len;i++){
			if (list[i][key] == value){
				return list[i];
			}
		}
        return null;
    }

    //复选框选中时，触发的事件
    $scope.updateSpecAttribute = function (event,name,value) {
		//1 定义一个查询的对象
		var obj = searchObject( $scope.entity.goodsDesc.specificationItems,'attributeName',name);
		// 2 判断对象是否为空
		if (obj){         //当前不为空
			//2.1 复选框选中的情况
			if(event.target.checked){
				//将value值放入attributeValue的数组中
				obj.attributeValue.push(value);
			}else{   //2.2 表示未被选中  ，分为两种情况
				//2.2.1 数组中还有值，则直接未被勾选的选项
				//获取未被选中的选项在数组中的下标
				let indexValue = obj.attributeValue.indexOf(value);
				//根据下标进行删除
				obj.attributeValue.splice(indexValue,1);
				//2.2.2 若attribute数组中的值为空，则从大数组中移出该attribute小数组
				if (obj.attributeValue.length == 0){
					//获取attribute在大数组中的下标位置
					let index  = $scope.entity.goodsDesc.specificationItems.indexOf(obj);
					//通过下标移出小数组
					$scope.entity.goodsDesc.specificationItems.splice(index,1);
				}
			}
		}else {  //2.3 表示第一次添加  则直接初始化该数组
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]})
		}
		//选中复选框时，同时显示添加的选中的规格参数
		createItemList();
    }


    //动态生成用户选择的规格参数列表
	createItemList = function () {
		//1 初始化Item选项列表
		 $scope.entity.items =[{spec:{},price:0,num:99999,status:'0',isDefault:'0' }];
		 //2 获取规格列表
		var itemList = $scope.entity.goodsDesc.specificationItems;
		//3 遍历规格列表
		for (let i = 0,len = itemList.length;i<len;i++){
			$scope.entity.items = addColum($scope.entity.items,itemList[i].attributeName,itemList[i].attributeValue);
		}
    }
    //自定义方法，循环列表，为item中添加赋值，新增行
    addColum = function (list,name,value) {
		// 0 构造一个空的集合
		var newList = [];
		//1 遍历循环列表
		for (let j = 0,n =list.length;j < n;j++){
			//1.1 获取列表中的原始数据
			let oldValue = list[j];
			//1.2 遍历value集合
			for (let m=0,l=value.length;m<l;m++){
				//1.3 对原始数据进行深度克隆，得到一个新的行
				let newRow = JSON.parse(JSON.stringify(oldValue));
				//1.4 给新的行中添加元素
				newRow.spec[name] = value[m];
				//1.5 新行添加到列表中
				newList.push(newRow);
			}
		}
		//2 返回集合
		return newList;
    }


    /*第二部分   商品管理*/

    //分页
    $scope.findPage=function(page,rows){
        goodsService.findPage(page,rows).success(
            function(response){
                $scope.list=response.data;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

    //跳转到商品修改页面并将id传递到页面中
    $scope.skipToGoodsEdit = function(id){
        location.href = 'goods_edit.html#?goodsId='+id;
    }

    //查询单个实体对象，进行修改数据的回显
    $scope.findOne = function(){
        //1 页面跳转并带参数
        let goodsId = $location.search()["goodsId"];
        //2 执行查询方法
        goodsService.findOne(goodsId).success(
            (response)=>{
                //3 返回查询结果（不做处理仅显示部分数据  后面进行数据处理）
                $scope.entity = response;
                //3.1 富文本编辑器的处理
                editor.html($scope.entity.goodsDesc.introduction);
                //3. 2 图片处理
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                //3.2 扩展属性处理  注意：此时需对添加操作时的扩展属性赋值操作进行判断，否则会出现值覆盖的现象
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems)
                //3.3 转换规格列表
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                //3.4 转换sku商品列表
                for (let j = 0;j<$scope.entity.items.length ; j++){
                    //3.4.1 得到每一个item
                    let item = $scope.entity.items[j];
                    //3.4.2 将每一个item的spec转换成对象
                    item.spec = JSON.parse(item.spec);
                }
            }
        )
    }

    //根据当前item中的spec选项的值来确定是否选择某个规格选项
    //思路：将当前查询的的spec中的值与goods列表中specificationItem中的值进行比较，若存在，则返回true，不存在返回false
    $scope.checkAttributeValue = function(text,value){
        //1 得到当前用户所选的所有规格的参数列表
        let itemList = $scope.entity.goodsDesc.specificationItems;
        //2 得到当前用户所选择的规格参数列表
        let obj = searchObject(itemList,"attributeName",text);
        //3 进行判断，若返回的列表为空，则置为false
        if (!obj){
            return false;
        }else { //3.2 非空判断
            //3.2.1 判断当前的集合中是否包含用户所选的规格参数
            if (obj.attributeValue.indexOf(value)>=0){
                return true;
            }
            return false;
        }
    };




    //商品状态  通过数组的下标进行显示
    $scope.status=['未审核','已审核','审核未通过','关闭'];

    $scope.searchEntity={};//定义搜索对象
    //搜索
    $scope.search=function(page,rows){
        goodsService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.list=response.data;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    };

    //定义商品分类列表 用于显示商品分类
    $scope.itemCatList = [];
    //加载商品分类列表
    $scope.findItemList = function(){
        itemCatService.findAll().success(
            (response)=>{
                //遍历返回的response
                for (let i=0,len = response.length;i<len;i++){
                    $scope.itemCatList[response[i].id] = response[i].name;
                }
            }
        );
    }



});	
