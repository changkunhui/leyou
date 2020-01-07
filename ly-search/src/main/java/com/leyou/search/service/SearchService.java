package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.*;
import com.leyou.search.dto.SearchRequest;
import com.leyou.search.entity.Goods;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author changkunhui
 * @date 2019/12/28 18:23
 */

@Service
public class SearchService {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    private GoodsRepository goodsRepository;

    public Goods buildGoods(SpuDTO spuDTO) {
        Goods goods = new Goods();
        Long spuId = spuDTO.getId();
        goods.setId(spuId);
        goods.setSubTitle(spuDTO.getSubTitle());
        goods.setCategoryId(spuDTO.getCid3());
        goods.setBrandId(spuDTO.getBrandId());
        goods.setCreateTime(spuDTO.getCreateTime().getTime());

        //all:是商品名称+品牌名称+分类名称
        String all = spuDTO.getName() + spuDTO.getBrandName() + spuDTO.getCategoryName();
        goods.setAll(all);

        //根据spuId查询SKU集合
        List<SkuDTO> skuDTOList = itemClient.findSkuBySpuId(spuId);
        List<Map> skus = skuDTOList.stream().map(skuDTO -> {
            Map map = new HashMap<>();
            map.put("id", skuDTO);
            map.put("price", skuDTO.getPrice());
            map.put("title", skuDTO.getTitle());
            map.put("image", StringUtils.substringBefore(skuDTO.getImages(), ","));
            return map;
        }).collect(Collectors.toList());

        //设置属性skus
        goods.setSkus(JsonUtils.toString(skus));

        //设置属性peices
        Set<Long> prices = skuDTOList.stream().map(SkuDTO::getPrice).collect(Collectors.toSet());
        goods.setPrice(prices);//价格取决去sku中的price中的值

        //                查询 SpuDetail  generic_spec  special_spec
        SpuDetailDTO spuDetail = itemClient.findDetail(spuId);
        String genericSpec = spuDetail.getGenericSpec();//通用的属性
        //转成map集合
        Map<Long, String> genericSpecMap = JsonUtils.toMap(genericSpec, Long.class, String.class);

        String specialSpec = spuDetail.getSpecialSpec();//特殊的属性
        //转成map集合
        Map<Long, List<String>> specialSpecMap = JsonUtils.nativeRead(specialSpec, new TypeReference<Map<Long, List<String>>>() {});

        //                根据分类id只查询用来搜索的spec_param
        List<SpecParamDTO> specParamList = itemClient.findSpecParamByCidOrGid(null, spuDTO.getCid3(), true);

        HashMap<String, Object> specsMap = new HashMap<>();
        for (SpecParamDTO param : specParamList) {
            String key = param.getName();
            Object value = null;
            if(param.getGeneric()){
                //是通用的属性
                value = genericSpecMap.get(param.getId());
            }else{
                //不是通过属性
                value = specialSpecMap.get(param.getId());
            }

            //如果是数值类型,替换为区间值
            if(param.getIsNumeric()){
                value = chooseSegment(value,param);
            }

            specsMap.put(key,value);
            goods.setSpecs(specsMap);
        }

        return goods;

    }


    /**
     * 判断value属于哪一个区间值,并返回所在的区间值
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(Object value, SpecParamDTO p) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 分页查询+关键字搜索
     * @param searchRequest 搜索对象
     * @return PageResult<Good>
     */
    public PageResult<Goods> findGoodsByPage(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        if(StringUtils.isBlank(key)){
            return null;
        }

        //构建查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //指定查询方式,字段
        queryBuilder = buildBaseQuery(searchRequest, queryBuilder);
        Integer page = searchRequest.getPage();
        Integer size = searchRequest.getSize();
        //执行查询
        queryBuilder.withPageable(PageRequest.of(page -1,size));

        //只查询需要的字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));

        //执行查询
        AggregatedPage<Goods> aggregatedPage = esTemplate.queryForPage(queryBuilder.build(), Goods.class);

        //获取需要的数据
        Integer totalPages = aggregatedPage.getTotalPages();
        long total = aggregatedPage.getTotalElements();
        List<Goods> goodsList = aggregatedPage.getContent();

        return new PageResult<>(total,totalPages.longValue(),goodsList);
    }

    /**
     * 构建基本查询条件并追加查询方法
     */
    private NativeSearchQueryBuilder buildBaseQuery(SearchRequest searchRequest,NativeSearchQueryBuilder queryBuilder) {

        //重新构建查询,使用关键字查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("all",searchRequest.getKey()));

        //追加过滤条件
        Map<String, String> filterMap = searchRequest.getFilterMap();

        for (String key : filterMap.keySet()) {
            //判断key是否是品牌和分类
            if("品牌".equals(key)){
                boolQueryBuilder.filter(QueryBuilders.termQuery("brandId",filterMap.get(key)));
            }else if("分类".equals(key)){
                boolQueryBuilder.filter(QueryBuilders.termQuery("categoryId",filterMap.get(key)));
            }else{
                boolQueryBuilder.filter(QueryBuilders.termQuery("specs."+key,filterMap.get(key)));
            }
        }

        queryBuilder.withQuery(boolQueryBuilder);

        return queryBuilder;
    }


    /**
     * 商城查询过滤条件
     * @param searchRequest
     * @return
     */
    public Map<String, List<?>> findFilter(SearchRequest searchRequest) {

        Map<String, List<?>> filterMap = new LinkedHashMap<>();

        String key = searchRequest.getKey();
        if(StringUtils.isBlank(key)){
            return null;
        }
        //构建查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //指定查询方式,字段
        queryBuilder = buildBaseQuery(searchRequest, queryBuilder);

        //执行查询
        queryBuilder.withPageable(PageRequest.of(0,1));

        //构建品牌聚合的条件(显示充足的条数,防止品牌显示不全)
        queryBuilder.addAggregation(AggregationBuilders.terms("brandAgg").field("brandId").size(100));

        //构建分类聚合的条件
        queryBuilder.addAggregation(AggregationBuilders.terms("categoryAgg").field("categoryId").size(10));

        //执行查询
        AggregatedPage<Goods> aggregatedPage = esTemplate.queryForPage(queryBuilder.build(), Goods.class);

        Aggregations aggregations = aggregatedPage.getAggregations();

        //处理查询出来的品牌的结果
        handlerBrandAgg(filterMap, aggregations);

        //处理查询出来的分类的结果
        handlerCategoryAgg(filterMap,queryBuilder, aggregations);

        return filterMap;
    }

    /**
     * 处理分类和规格的聚合结果
     * @param filterMap
     * @param aggregations
     */
    private void handlerCategoryAgg(Map<String, List<?>> filterMap,NativeSearchQueryBuilder queryBuilder, Aggregations aggregations) {
        Terms categoryTerms = aggregations.get("categoryAgg");
        List<? extends Terms.Bucket> categoryBuckets = categoryTerms.getBuckets();
        //流式编程获取categoryId的集合
        List<Long> categoryIdList = categoryBuckets.stream().map(Terms.Bucket::getKeyAsNumber).map(Number::longValue).collect(Collectors.toList());
        //根据categoryId的集合获取category对象的集合
        List<CategoryDTO> categoryList = itemClient.findCategoryListByCategoryIds(categoryIdList);

        filterMap.put("分类",categoryList);

        //有了分类的结果才知道要查那些规格
        if(!CollectionUtils.isEmpty(categoryList)){
            //查询规格数据
            //根据第一个分类查询规格数据
            Long categoryId = categoryIdList.get(0);
            //根据categoryId查询出需要搜索的规格参数
            List<SpecParamDTO> specParamList = itemClient.findSpecParamByCidOrGid(null, categoryId, true);

            //构建规格参数的聚合条件
            for (SpecParamDTO specParamDTO : specParamList) {
                String paramName = specParamDTO.getName();
                queryBuilder.addAggregation(AggregationBuilders.terms(paramName+"Agg").field("specs."+paramName).size(10));
            }

            //执行查询.获取刚构建的规格参数的集合结果
            AggregatedPage<Goods> aggregatedPage = esTemplate.queryForPage(queryBuilder.build(), Goods.class);
            Aggregations specAggregations = aggregatedPage.getAggregations();

            //获取规格参数聚合后的结果
            for (SpecParamDTO specParamDTO : specParamList) {
                String paramName = specParamDTO.getName();
                Terms termParam = specAggregations.get(paramName + "Agg");
                List<? extends Terms.Bucket> paramBuckets = termParam.getBuckets();

                List<String> paramList = paramBuckets.stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList());

                filterMap.put(paramName,paramList);

            }
        }
    }


    /**
     * 处理品牌的聚合结果
     * @param filterMap
     * @param aggregations
     */
    private void handlerBrandAgg(Map<String, List<?>> filterMap, Aggregations aggregations) {
        //获取品牌的聚合结果
        Terms brandTerms = aggregations.get("brandAgg");
        List<? extends Terms.Bucket> brandBuckets = brandTerms.getBuckets();
        //流式变成获取brandId的集合
        List<Long> brandIdList = brandBuckets.stream().map(Terms.Bucket::getKeyAsNumber).map(Number::longValue).collect(Collectors.toList());
        //根据brandId的集合获取brand对象的集合
        List<BrandDTO> brandList = itemClient.findBrandListByBrandIds(brandIdList);

        filterMap.put("品牌",brandList);
    }

    /**
     * 商品上架的时候创建索引
     * @param spuId
     */
    public void createIndex(Long spuId) {

        //根据spuId查询spu对象
        SpuDTO spuDTO = itemClient.findSpuBySpuId(spuId);
        if(spuDTO == null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //构建成goods对象
        Goods goods = this.buildGoods(spuDTO);
        //保存数据到索引库
        goodsRepository.save(goods);
    }

    /**
     * 商品下架的时候删除索引库中的索引
     * @param spuId
     */
    public void removeById(Long spuId) {
        goodsRepository.deleteById(spuId);
    }
}
