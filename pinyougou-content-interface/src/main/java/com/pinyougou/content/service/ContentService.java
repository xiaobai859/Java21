package com.pinyougou.content.service;
import java.util.List;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbItem;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface ContentService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbContent> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbContent content);
	
	
	/**
	 * 修改
	 */
	public void update(TbContent content);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbContent findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbContent content, int pageNum,int pageSize);
	
	/**
	 * @methodName:findByCategoryId
	 * @description: 根据分类id查询广告列表
	 * @author：Xiaobai
	 * @createTime：2019年7月31日 下午4:34:32
	 * @remarks: @param id
	 * @remarks: @return
	 * @resultType：List<TbContent>
	 */
	public List<TbContent> findByCategoryId(Long categoryId);
	
	
	
}