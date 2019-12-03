app.controller("cartController",function ($scope,$controller,cartService) {
    $controller('baseController',{$scope:$scope});//继承

    //1.查询购物车列表
    $scope.findCartList = ()=>{
        cartService.findCartList().success(response=>{
            $scope.cartList = response;
            //求累加的数量及金额
            sum($scope.cartList);
        })
    }
    //2.添加到购物车
    $scope.addGoodsToCartList=(itemId,num)=>{
        cartService.addGoodsToCartList(itemId,num).success(response=>{
            if(response.success){
                $scope.findCartList();
            }else{
                alert(response.message);
            }
        })
    }
    //3.定义求和的函数
    sum=(cartList)=>{
        //3.1)定义代表总金额及数量的变量
        $scope.total = {totalNum:0,totalMoney:0};
        //3.2)遍历购物车集合
        for(let i = 0,len = cartList.length; i < len;i++){
            //3.3)得到某个购物车
            let cart = cartList[i];
            //3.4)遍历订单项列表
            for(let j = 0;j < cart.orderItemList.length;j++){
                //3.5)得到某个订单项
                let orderItem = cart.orderItemList[j];
                //3.6)求出累加数量
                $scope.total.totalNum += orderItem.num;
                //3.7)求出累加金额
                $scope.total.totalMoney += orderItem.totalFee;
            }
        }
    }
    //4 根据用户名全部的地址列表
    $scope.findAddressByName =()=>{
        cartService.findAddressByName().success(response=>{
            //1 获取地址列表
            $scope.addressList=response;
            //2 遍历地址列表 获取当前的地址
            for (let i = 0,len =response.length;i<len ; i++){
                if (response[i].isDefault=='1'){
                    let addr = response[i];
                    $scope.selectAddress(addr);
                }
            }
        });
    }

    //5 点击选中地址(此处传入的为对象)
    $scope.selectAddress = (addr)=>{
        $scope.address = addr;
    }
    //6 判断是否选中(此处的address是一个属性  当前地址)
    $scope.isSelected=(addr)=>{
       return  $scope.address.address == addr;
    }

    //6 定义支付类型 默认为微信支付
    $scope.order={paymentType:'1'};
    $scope.selectPayType = (type)=>{
        $scope.order.paymentType = type;
    }

    //7 保存订单
    $scope.saveOrder = ()=>{
        $scope.order.receiverAreaName=$scope.address.address;
        $scope.order.receiverMobile=$scope.address.mobile;
        $scope.order.receiver=$scope.address.contact;
        cartService.saveOrder($scope.order).success(response=>{
            if (response.success){
                if ($scope.order.paymentType == '1'){
                    location.href = "pay.html";
                }else {
                    location.href = "paysuccess.html";
                }
            }else {
                alert(response.message);
            }
        });
    }

})