app.controller("seckillGoodsController", function ($scope, $controller, $location, $interval, seckillGoodsService) {
    $controller('baseController', {$scope: $scope});//继承
    //1.查询所有的秒杀商品
    $scope.findSecKillGoods = () => {
        seckillGoodsService.findSecKillGoods().success(response => {
            $scope.secKillGoodsList = response;
        })
    }
    //2.根据id查询一件商品
    $scope.findOne = () => {
        //2.1)得到秒杀商品的id
        let id = $location.search()["id"];
        //2.2)根据秒杀商品到后台查询此商品
        seckillGoodsService.findOne(id).success(response => {
            $scope.secKillGoods = response;
            //动态显示时间
            showTime();
        })
    }
    //3.跳转到指定的秒杀详情页面
    $scope.goItem = (id) => {
        location.href = "seckill-item.html#?id=" + id;
    }

    //4.$interval的基本用法
    function test() {
        let seconds = 10;
        let flag = $interval(() => {
            if (seconds > 0) {
                console.log(seconds--);
            } else {
                $interval.cancel(flag);
                alert("任务完成！")
            }
        }, 1000);
    }

    //test();
    //5.项目中的倒计时处理
    showTime = () => {
        //5.1)得到余下的总秒数
        let times = Math.floor((new Date($scope.secKillGoods.endTime).getTime() - new Date().getTime()) / 1000);
        //定义存放最终结果的字符串
        $scope.timeStr = "";
        //5.2)如果余下的秒数大于0
        let flag = $interval(()=>{
            //5.3)得到天数
            let day = Math.floor(times / (3600 * 24));
            //5.4)得到小时数
            let hours = Math.floor((times - day * 3600 * 24) / 3600);
            //5.5)得到分钟数
            let minutes = Math.floor((times - day * 3600 * 24 - hours * 3600) / 60);
            //5.6)得到秒数
            let seconds = times - day * 3600 * 24 - hours * 3600 - minutes * 60;
            //5.7)判断是否存在天数
            if (day > 0) {
                $scope.timeStr = day + "天";
            }
            $scope.timeStr += hours + ":" + minutes + ":" + seconds;
            if(times-- <= 0){
                $interval.cancel(flag);
                alert("秒杀结束！");
            }
        },1000);
    }

    //6 提交订单 传入秒杀商品id
    $scope.submitOrder = ()=>{
        seckillGoodsService.submitOrder($scope.secKillGoods.id).success(response=>{
            if (response.success){
                alert("亲，请在一分钟内完成支付操作!");
                location.href = "pay.html";
            }else {
                alert(response.message);
            }
        });
    }



})