package com.pinyougou.sellergoods.service.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	@Autowired
	private TbBrandMapper brandMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Autowired
	private TbSellerMapper sellerMapper;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		
		goods.getGoods().setAuditStatus("0"); // 添加商品初始化为未审核状态
		goods.getGoods().setIsMarketable("1"); // 新添加的商品默认为上架状态
		goodsMapper.insert(goods.getGoods()); // 插入商品
		
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goods.getGoodsDesc()); // 插入商品扩展属性
		
		saveItemList(goods); // 插入SKU列表数据
		
		
	}
	
	private void setItemValues(Goods goods, TbItem item) {
		item.setGoodsId(goods.getGoods().getId()); // 商品SPU编号
		item.setSellerId(goods.getGoods().getSellerId()); // 商家编号
		item.setCategoryid(goods.getGoods().getCategory3Id()); // 商品三级分类编号
		item.setCreateTime(new Date()); // 创建时间
		item.setUpdateTime(new Date()); // 修改时间
		
		// 品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());
		
		// 分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(itemCat.getName());
		
		// 商家名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());
		
		// 图片地址
		List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		
		if( imageList.size() > 0) {
			item.setImage(((String)imageList.get(0).get("url")));
		}
	}
	
	// 添加商品SKU列表数据
	private void saveItemList(Goods goods) {
		if( "1".equals(goods.getGoods().getIsEnableSpec())) {
			for (TbItem item : goods.getItemList()) {
				String title = goods.getGoods().getGoodsName();
				Map<String, Object> specMap = JSON.parseObject(item.getSpec());
				for (String key : specMap.keySet()) {
					title += "" + specMap.get(key);
				}
				
				item.setTitle(title);	
				setItemValues(goods, item);
				itemMapper.insert(item);
				
			}
		} else { // 没有启用规格
			TbItem item = new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());
			item.setPrice(goods.getGoods().getPrice());
			item.setStatus("1");
			item.setIsDefault("1");
			item.setNum(9999);
			item.setSpec("{}");
			setItemValues(goods, item);
			itemMapper.insert(item);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		// 更新商品表
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		// 更新商品信息表
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		
		// 先删除SKU数据
		TbItemExample example = new TbItemExample();
		example.createCriteria().andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		// 再添加新的SKU信息
		saveItemList(goods); // 插入SKU列表数据
		
	}	
	
	/**
	 *   根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		// 商品表
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);
		// 商品扩展信息表
		TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(goodsDesc);
		
		TbItemExample example = new TbItemExample();
		example.createCriteria().andGoodsIdEqualTo(id);
		List<TbItem> itemList = itemMapper.selectByExample(example);
		goods.setItemList(itemList);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			// 进行逻辑删除，并不将其从数据库中删除
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1"); // 数据库中isDelete字段不为空时代表已逻辑删除
			goodsMapper.updateByPrimaryKey(goods);
		}		
	}
	
	
	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull(); // 未进行逻辑删除的才会显示
		criteria.andIsMarketableEqualTo("1");
		
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				/*criteria.andSellerIdLike("%"+goods.getSellerId()+"%");*/
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				/*criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");*/
				criteria.andAuditStatusEqualTo(goods.getAuditStatus());
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 *	更新状态
	 */
	@Override
	public void updateStatus(Long[] ids, String status) {
		for (Long id : ids) {
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(goods);
		}
		
	}

	/**
	 *	 商品上下架状态
	 *  0：下架    1：上架
	 */
	@Override
	public void goodsMark(Long[] ids, String isMarketable) {
		for (Long id : ids) {
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsMarketable(isMarketable);
			goodsMapper.updateByPrimaryKey(goods);
		}
		
	}

	/**
	 * 根据SPU的id查询SKU列表
	 */
	@Override
	public List<TbItem> findItemListByGoodsIdListAndStatus(Long[] ids, String status) {
		
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(status); // 状态
		criteria.andGoodsIdIn(Arrays.asList(ids)); // SPU的id集合
		return itemMapper.selectByExample(example );
	}
	
}
