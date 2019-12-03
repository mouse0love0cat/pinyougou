app.service("cartService",function ($http) {
    //1.查询购物车列表
    this.findCartList = ()=>{
        return $http.get("./cart/findCartList.do");
    }
    //2.添加到购物车
    this.addGoodsToCartList=(itemId,num)=>{
        return $http.get("./cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num);
    }
})