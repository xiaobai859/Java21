//控制层 
app.controller('goodsController',
    function ($scope, $controller, $location, goodsService, uploadService,
        itemCatService, typeTemplateService) {

      $controller('baseController', {$scope: $scope});//继承

      //读取列表数据绑定到表单中  
      $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
              $scope.list = response;
            }
        );
      }

      //分页
      $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
              $scope.list = response.rows;
              $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
      }

      //查询实体 
      $scope.findOne = function () {
        var id = $location.search()["id"];
        // alert(id);
        if (id == null) {
          return;
        }
        goodsService.findOne(id).success(
            function (response) {
              $scope.entity = response; // 基础信息
              editor.html($scope.entity.goodsDesc.introduction); // 文本框区域
              $scope.entity.goodsDesc.itemImages = JSON.parse(
                  $scope.entity.goodsDesc.itemImages); // 图片
              // console.log($scope.entity.goodsDesc.customAttributeItems);
              $scope.entity.goodsDesc.customAttributeItems = JSON.parse(
                  $scope.entity.goodsDesc.customAttributeItems); // 扩展属性
              // console.log($scope.entity.goodsDesc.customAttributeItems)
              $scope.entity.goodsDesc.specificationItems = JSON.parse(
                  $scope.entity.goodsDesc.specificationItems); // 规格
              for (var i = 0; i < $scope.entity.itemList.length; i++) {
                $scope.entity.itemList[i].spec = JSON.parse(
                    $scope.entity.itemList[i].spec);
              }
            }
        );
      }

      //保存 
      $scope.save = function () {
        $scope.entity.goodsDesc.introduction = editor.html();

        var serviceObject;//服务层对象  				
        if ($scope.entity.goods.id != null) {//如果有ID
          serviceObject = goodsService.update($scope.entity); //修改  
        } else {
          serviceObject = goodsService.add($scope.entity);//增加 
        }
        serviceObject.success(
            function (response) {
              if (response.success) {
                //重新查询 
                alert("保存成功");
                location.href = "goods.html";
              } else {
                alert(response.message);
              }
            }
        );
      }

      // 添加
      /*$scope.add=function(){		
        $scope.entity.goodsDesc.introduction=editor.html();
        
        goodsService.add( $scope.entity ).success(
          function(response){
            if(response.success){
              // 成功
              alert("保存成功");
              $scope.entity={};
              editor.html("");
            }else{
              alert(response.message);
            }
          }		
        );				
      }*/

      //批量删除 
      $scope.dele = function () {
        //获取选中的复选框			
        goodsService.dele($scope.selectIds).success(
            function (response) {
              if (response.success) {
                $scope.reloadList();//刷新列表
                $scope.selectIds = [];
              }
            }
        );
      }

      $scope.searchEntity = {};//定义搜索对象 

      //搜索
      $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
              $scope.list = response.rows;
              $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
      }

      $scope.image_entity = {};

      // 上传文件
      $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {
              if (response.success) {
                $scope.image_entity.url = response.message;//设置文件地址
              } else {
                alert(response.message);
              }
            }
        );
      }
      $scope.entity = {
        goods: {},
        goodsDesc: {itemImages: [], specificationItems: []}
      }

      // 添加到列表显示
      $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
      }

      // 从列表中删除
      $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
      }

      // 查询一级列表
      $scope.selectItemCatList1 = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
              $scope.itemCatList1 = response;
            }
        );
      }

      // 查询二级列表
      $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        if (newValue) {
          itemCatService.findByParentId(newValue).success(
              function (response) {
                $scope.itemCatList2 = response;
              }
          );
        }
      });

      // 查询三级列表
      $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        if (newValue) {
          itemCatService.findByParentId(newValue).success(
              function (response) {
                $scope.itemCatList3 = response;
              }
          );
        }
      });

      // 查询显示类型模板ID
      $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        if (newValue) {
          itemCatService.findOne(newValue).success(
              function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;
              }
          );
        }
      });

      // 根据模板id显示品牌
      $scope.$watch('entity.goods.typeTemplateId',
          function (newValue, oldValue) {
            if (newValue) {
              typeTemplateService.findOne(newValue).success(
                  function (response) {
                    $scope.typeTemplate = response; // 获得属性模板
                    $scope.typeTemplate.brandIds = JSON.parse(
                        $scope.typeTemplate.brandIds); // 获得品牌列表
                    // 扩展属性
                    if ($location.search()["id"] == null) {
                      $scope.entity.goodsDesc.customAttributeItems = JSON.parse(
                          $scope.typeTemplate.customAttributeItems);
                    }

                  }
              );

              // 读取规格
              typeTemplateService.findSpecList(newValue).success(
                  function (response) {
                    $scope.specList = response;
                  }
              );
            }
          });

      $scope.updateSpecAttribute = function ($event, name, value) {
        var object = $scope.searchObjectByKey(
            $scope.entity.goodsDesc.specificationItems, "attributeName", name);
        if (object != null) {
          if ($event.target.checked) {
            object.attributeValue.push(value);
          } else {
            // 取消选中
            object.attributeValue.splice(object.attributeValue.indexOf(value),
                1); // 取消勾选后删除

            // 一行选项都取消了,将此条记录删除
            if (object.attributeValue.length == 0) {
              $scope.entity.goodsDesc.specificationItems.splice(
                  $scope.entity.goodsDesc.specificationItems.indexOf(object),
                  1);

            }
          }
        } else {
          $scope.entity.goodsDesc.specificationItems.push(
              {"attributeName": name, "attributeValue": [value]}
          );
        }

      }

      // 创建SKU列表
      $scope.createItemList = function () {
        $scope.entity.itemList = [{
          spec: {},
          price: 0,
          num: 99999,
          status: "0",
          ifDefault: "0"
        }]; // 初始化

        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
          $scope.entity.itemList = addColumn($scope.entity.itemList,
              items[i].attributeName, items[i].attributeValue);
        }
      }

      addColumn = function (list, columnName, columnValues) {
        var newList = []; // 新集合
        for (var i = 0; i < list.length; i++) {
          var oldRow = list[i];
          for (var j = 0; j < columnValues.length; j++) {
            var newRow = JSON.parse(JSON.stringify(oldRow)); // 使用深克隆完全复制oldRow到newRow
            newRow.spec[columnName] = columnValues[j];
            newList.push(newRow);
          }
        }

        return newList;
      }

      $scope.status = ["未审核", "已审核", "审核未通过", "已关闭"]; // 商品状态

      $scope.itemCatList = [];
      $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
              for (var i = 0; i < response.length; i++) {
                $scope.itemCatList[response[i].id] = response[i].name;
              }

            }
        );
      }

      $scope.checkAttributeValue = function (specName, optionName) {
        var items = $scope.entity.goodsDesc.specificationItems;
        // [{"attributeValue":["移动3G"],"attributeName":"网络"},{"attributeValue":["32G","64G"],"attributeName":"机身内存"}]

        var object = $scope.searchObjectByKey(items, "attributeName", specName); // 网络
        // {"attributeName":"网络","attributeValue":["移动3G",”移动4G”]}
        if (object != null && object.attributeValue.indexOf(optionName) >= 0) {
          return true;
        } else {
          return false;
        }
      }

      /**
       * 商品上下架标记
       */
      $scope.goodsMark = function (isMarketable) {
        goodsService.goodsMark($scope.selectIds, isMarketable).success(
            function (response) {
              if (response.success) {
                $scope.reloadList(); // 刷新列表
                $scope.selectIds = []; // 重置为空
              } else {
                alert(response.message);
              }
            }
        );
      }

    });	
