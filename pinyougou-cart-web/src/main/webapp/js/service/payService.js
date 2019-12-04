app.service("payService",function ($http) {

    //1 生成二维码
    this.createNative = ()=>{
     return $http.get("./pay/createNative.do");
    }
    //2 查询订单状态
    this.quertyOrder = (out_trade_no)=>{
        return $http.get("./pay/quertyOrder.do?out_trade_no="+out_trade_no);
    }

})