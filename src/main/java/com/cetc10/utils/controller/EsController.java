package com.cetc10.utils.controller;

import com.cetc10.utils.service.EsService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("es")
@Slf4j
public class EsController {
    @Autowired
    private EsService esService;

    @PostMapping("/insert")
    @ApiOperation(value = "批量插入es")
    public void insertEs(){
        esService.insert();
    }

    @PostMapping("/search")
    @ApiOperation(value = "查询es")
    public SearchResponse search(){
        return esService.search();
    }

//    @PostMapping("insert")
//    public void insert(){
//        List<User> userList = new ArrayList<>();
//        User user1 = new User("1", "张豆豆", 23, new Date());
//        User user2 = new User("2", "张豆豆1", 231, new Date());
//        User user3 = new User("3", "张豆豆2", 232, new Date());
//        User user4 = new User("4", "张豆豆3", 233, new Date());
//        User user5 = new User("5", "张豆豆4", 234, new Date());
//        User user6 = new User("6", "张豆豆5", 235, new Date());
//        User user7 = new User("7", "张豆豆6", 236, new Date());
//        User user8 = new User("8", "张豆豆7", 237, new Date());
//        User user9 = new User("9", "张豆豆8", 238, new Date());
//        User user10 = new User("10", "张豆豆9", 239, new Date());
//
//        userList.add(user1);
//        userList.add(user2);
//        userList.add(user3);
//        userList.add(user4);
//        userList.add(user5);
//        userList.add(user6);
//        userList.add(user7);
//        userList.add(user8);
//        userList.add(user9);
//        userList.add(user10);
//
//        long l = System.currentTimeMillis();
//        esService.save(userList);
//        long l1 = System.currentTimeMillis();
//        log.info("============批量插入时间=========>"+(l1-l));
//    }
//
//    @GetMapping("/getAll")
//    public Iterable<User> getAll() {
//        Iterable<User> all = esService.findAll();
//        for (User User : all) {
//            System.out.println(User);
//        }
//        return all;
//    }
//    @GetMapping("/delete")
//    public String delete() {
//        esService.delete(1L);
////        esService.deleteById(1L);
//        return "sd";
//    }
//
//    @GetMapping("/getAllIK")
//    public Iterable<User> getAllIK() {
////        // 构建查询条件
//        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//
//        // 添加基本分词查询
//        queryBuilder.withQuery(QueryBuilders.boolQuery()
//                .must(QueryBuilders.matchQuery("name","张豆豆"))
////                .must(QueryBuilders.termQuery("category","电视"))
//        );
//
//        // 搜索，获取结果
//        Page<User> Users = esService.search(queryBuilder.build());
//        // 总条数
//        long total = Users.getTotalElements();
//        System.out.println("total = " + total);
//        Users.forEach(User -> System.out.println("User = " + User));
//
//        return Users;
//    }
//
//    /**
//     *
//     * 分词（有拼音分词器时可用）
//     * 拼接搜索条件
//     *
//     * @param name     the name
//     * @return list
//     */
//    public  List<User> getIK(String name) {
//        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(structureQuery("小米手机"))
//                .build();
//        List<User> UserList = esService.search(searchQuery).getContent();
//        return UserList;
//    }
//    /**
//     * 中文、拼音混合搜索
//     *
//     * @param content the content
//     * @return dis max query builder
//     */
//    public DisMaxQueryBuilder structureQuery(String content) {
//        //使用dis_max直接取多个query中，分数最高的那一个query的分数即可
//        DisMaxQueryBuilder disMaxQueryBuilder = QueryBuilders.disMaxQuery();
//        //boost 设置权重,只搜索匹配name和disrector字段
//        QueryBuilder ikNameQuery = QueryBuilders.matchQuery("title", content).boost(2f);
//
////        QueryBuilder pinyinNameQuery = QueryBuilders.matchQuery("name.pinyin", content);
////        QueryBuilder ikDirectorQuery = QueryBuilders.matchQuery("director", content).boost(2f);
//        disMaxQueryBuilder.add(ikNameQuery);
////        disMaxQueryBuilder.add(pinyinNameQuery);
////        disMaxQueryBuilder.add(ikDirectorQuery);
//        return disMaxQueryBuilder;
//    }
}
