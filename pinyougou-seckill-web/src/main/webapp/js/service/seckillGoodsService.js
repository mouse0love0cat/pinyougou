app.service("seckillGoodsService",function ($http) {

    //1.查询所有的秒杀商品
    this.findSecKillGoods=()=>{
        return $http.get("./seckillGoods/findSecKillGoods.do");
    }
    //2.根据秒杀商品的id查询秒杀商品
    this.findOne=(id)=>{
        return $http.get("./seckillGoods/findOne.do?id="+id);
    }
    //3 根据秒杀商品id进行下单
    this.submitOrder=(id)=>{
        return $http.get("./seckillOrder/submitOrder.do?id="+id);
    }
    //4 创建订单
    this.createNative =(out_trade_no,total_fee)=>{
        return $http.get("./seckillOrder/createNative.do?out_trade_no="+out_trade_no+"&total_fee="+total_fee);
    }

})