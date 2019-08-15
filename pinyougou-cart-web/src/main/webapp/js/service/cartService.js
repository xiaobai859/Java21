// 购物车前端服务层
app.service("cartService", function($http){
	// 购物车列表
	this.findCartList = function() {
		return $http.get("cart/findCartList.do");
	}
	
	// 添加商品到购物车
	this.addGoodsToCartList = function (itemId, num) {
		return $http.get("cart/addGoodsToCartList.do?itemId=" + itemId + "&num=" + num);
	}
	
	// 计算累计数量和累计金额
	this.sum = function (cartList) {
		var totalValue = {totalNum : 0, totalMoney : 0.00}; // 合计对象

		for(var i = 0; i < cartList.length; i++) {
			var cart = cartList[i]; // 购物车对象
			for(var j = 0; j < cart.orderItemList.length; j ++) {
				var orderItem = cart.orderItemList[j]; // 购物车明细对象
				totalValue.totalNum += orderItem.num;
				totalValue.totalMoney += orderItem.totalFee;
			}
		}
		return totalValue;
	}
	
	// 查询收货地址列表
	this.findAddressList = function() {
		return $http.get("address/findListByLoginUser.do");
	}
	
	// 添加收货地址
	this.addAddress = function(addAddress) {
		console.log(addAddress);
		return $http.post("address/add.do", addAddress);
	}
	
	
	
	//保存订单
	this.submitOrder=function(order){
		return $http.post('order/add.do',order);		
	}


	
	
});