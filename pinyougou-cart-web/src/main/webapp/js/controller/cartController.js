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
})