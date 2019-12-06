app.service("cartService",function ($http) {
    //1.查询购物车列表
    this.findCartList = ()=>{
        return $http.get("./cart/findCartList.do");
    }
    //2.添加到购物车
    this.addGoodsToCartList=(itemId,num)=>{
        return $http.get("./cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num);
    }
    //3 查询收货地址
    this.findAddressByName=()=>{
        return $http.get("./address/findAddressByName.do");
    }
    //4 添加订单到数据库
    this.saveOrder = (order)=>{
        return $http.post("./order/add.do",order);
    }
})