app.controller("payController", function ($scope, $controller,$location, payService) {
    $controller('baseController', {$scope: $scope});//继承

    // 1 生成二维码
    $scope.createNative = ()=>{
        payService.createNative().success(response=>{
            //1.1.1)得到金额及订单号
            alert(response.out_trade_no);
            $scope.total_fee = (response.total_fee/100).toFixed(2);
            $scope.out_trade_no = response.out_trade_no;
            //1.1.2)生成二维码
            new QRious({
                element: document.getElementById("img1"),
                size: 200,
                level: 'H',
                value: response.code_url
            })
        });
    }
    //2 查询订单
    $scope.queryOrder = ()=>{
        payService.quertyOrder($scope.out_trade_no).success(response=>{
            if (response.success){
                location.href = "paysuccess.html#?money="+$scope.total_fee;
            }else {
                if (response.message=='二维码超时'){
                    $scope.createNative();
                }else {
                   location.href = "payfail.html";
                }
            }
        });
    }

    //3 将支付的金额显示在支付成功页面
    $scope.getMoney = ()=>{
       return  $location.search()['money'];
    }

})