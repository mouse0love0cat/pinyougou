app.service("seckillGoodsService",function ($http) {

    //1.查询所有的秒杀商品
    this.findSecKillGoods=()=>{
        return $http.get("./seckillGoods/findSecKillGoods.do");
    }
    //2.根据秒杀商品的id查询秒杀商品
    this.findOne=(id)=>{
        return $http.get("./seckillGoods/findOne.do?id="+id);
    }
})