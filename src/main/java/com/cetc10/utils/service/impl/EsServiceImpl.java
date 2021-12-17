package com.cetc10.utils.service.impl;

import com.alibaba.fastjson.JSON;
import com.cetc10.utils.model.User;
import com.cetc10.utils.service.EsService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class EsServiceImpl implements EsService {
    @Qualifier("myClient")
    @Autowired
    private RestHighLevelClient highLevelClient;
    //es索引名,自定义名称
    private String indexName = "sc";
    //es类型名,自定义名称
    private String typeName = "user";

    /**
     * 初始化es索引
     */
    public void initIndex(){
        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices(indexName);
        try {
            boolean exists = highLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
            if (!exists) {
                //这里的都是es的对象 indexName就是你的索引名
                CreateIndexRequest request = new CreateIndexRequest(indexName);

                //自定义按逗号分词
                Map<String, Object> settings = new HashMap<>();
                Map<String, Object> analysis = new HashMap<>();
                Map<String, Object> analyzer = new HashMap<>();
                Map<String, Object> comma = new HashMap<>();    //分词方法
                comma.put("type","pattern");
                comma.put("pattern",",");
                analyzer.put("comma",comma);
                analysis.put("analyzer",analyzer);
                settings.put("analysis",analysis);

                //设置es最大返回数
                settings.put("max_result_window",10000000);
                request.settings(settings);

                //下面是索引的字段，和对应的字段类型 getField方法可以自己修改下，我这边是进行的text类型的添加分词操作
                Map<String, Object> properties = new HashMap<>();
                properties.put("id",getField("keyword"));
                properties.put("name",getField("text"));
                properties.put("age",getField("integer"));
                properties.put("createTime",getField("date"));
                properties.put("address",getMyField("text"));

                Map<String, Object> mapping = new HashMap<>();
                mapping.put("properties", properties);
                request.mapping("user",mapping);

                // 执行创建请求
                CreateIndexResponse createIndexResponse = null;
                try {
                    createIndexResponse = highLevelClient.indices().create(request, RequestOptions.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //判断成功与否
                boolean acknowledged = createIndexResponse.isAcknowledged();
                boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getField(String type){
        Map<String, Object> result = new HashMap<>();
        result.put("type", type);
        if ("text".equals(type)) {
            result.put("analyzer", "ik_max_word");
            result.put("search_analyzer", "ik_max_word");
        }
        return result;
    }

    public Map<String, Object> getMyField(String type){
        Map<String, Object> result = new HashMap<>();
        result.put("type", type);
        if ("text".equals(type)) {
            result.put("analyzer", "comma");
        }
        return result;
    }

    @Override
    public void insert() {
        initIndex();

        //1.创建批量导入数据
        BulkRequest bulkRequest = new BulkRequest();
        //设置多长时间导入一次
//        bulkRequest.timeout("10s");
        //2.定义一个集合
        List<User> list = new ArrayList<>();
        User user1 = new User("no1","杜甫是一个诗人",22,new Date(),"成都蓝,宽窄巷子,川军抗日阵亡将士纪念碑");
        User user2 = new User("no2","达芬奇是一个画家",25,new Date(),"北京红,紫禁城,人民英雄纪念碑");
        list.add(user1);
        list.add(user2);
        //3.将数据批量添加
        long l = System.currentTimeMillis();
        for (User user : list) {
            //如果需要做批量删除或者批量更新，修改这里请求即可
            bulkRequest.add (
                    new IndexRequest(indexName)
                            //不填id时将会生成随机id
                            .id (user.getId())
                            .type(typeName)
                            .source (JSON.toJSONString (user), XContentType.JSON)
            );
        }

        //4.执行请求
        BulkResponse bulkResponse = null;
        try {
            bulkResponse = highLevelClient.bulk (bulkRequest, RequestOptions.DEFAULT);
            //5.响应 判断是否执行成功
            RestStatus status = bulkResponse.status();
            long l1 = System.currentTimeMillis();
            log.info("-------写入时间--------->"+(l1-l)+"=============写入大小==============>"+list.size());
            log.info (status.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SearchResponse search() {
        //1.创建批量查询数据的请求
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.types(typeName);
        //2.构建搜索的条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder ();
        int page = 1;
        int pagesize = 10;
        // 第几页
        searchSourceBuilder.from((page-1)*pagesize);
        // 每页多少条数据
        searchSourceBuilder.size(pagesize);
        // 获取的字段（列）和不需要获取的列
        searchSourceBuilder.fetchSource(new String[]{"name", "password"}, new String[]{"id"});
        // 设置排序规则
        searchSourceBuilder.sort("age", SortOrder.ASC);

        //构建查询条件,must必须，mustNot必须不，should类似于or，filter过滤
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //单条件精确查询(字段.keyword,"value")
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("address","四川主");
        boolQueryBuilder.must(termQueryBuilder);
        //多条件精确查询(字段.keyword,"value1","value2","value3",...)，取并集，也可以将多个条件放在一个集合里面
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("name", "张豆豆","张小豆");
        TermsQueryBuilder termsQueryBuilder1 = QueryBuilders.termsQuery("name", new ArrayList<>());
        boolQueryBuilder.must(termsQueryBuilder);
        //模糊查询(字段.keyword,"*value*")
        WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("name", "*张豆*");
        boolQueryBuilder.must(wildcardQueryBuilder);
        //范围查询,有false是开区间，无false是闭区间
        RangeQueryBuilder password = QueryBuilders.rangeQuery("password").from(4, false).to(27, false);
        RangeQueryBuilder password1 = QueryBuilders.rangeQuery("password").gte(22).lte(33);
        boolQueryBuilder.filter(password);
        boolQueryBuilder.must(password1);
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        try {
            long l = System.currentTimeMillis();
            SearchResponse searchResponse = highLevelClient.search (searchRequest, RequestOptions.DEFAULT);
            long l1 = System.currentTimeMillis();
            System.out.println("-------------查询时间--------------->"+(l1-l));
            System.out.println(searchResponse.toString());
            return  searchResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
