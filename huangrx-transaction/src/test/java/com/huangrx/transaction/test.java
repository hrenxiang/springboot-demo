package com.huangrx.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author    hrenxiang
 * @since     2022/7/5 08:52
 */
@SpringBootTest
public class test {

    @Autowired
    @Qualifier(value = "userServiceImpl")
    private UserService userService;

    @Resource
    JdbcTemplate jdbcTemplate;

    /**
     * 测试sql操作
     */
    @Test
    public void testDemo1() {
        String s = userService.selectEmpNameById(7369);
        System.out.println(s);

        userService.updateEmpNameById(7369, "SMITH");

        userService.updateEmpSalaryById(7369, 900.0);
    }

    /**
     * 不修改同一条数据，并破坏第二条修改薪资的sql语句，查看是否同时失败
     * 结果，第一条成功，第二条失败
     */
    @Test
    public void testDemo2() {
        userService.updateTwice(7369, "HH", 7499, 1700.0);
    }

    // 添加事务功能,并开启，且在updateTwice方法上添加使用事务的注解

    /**
     * 再次调用updateTwice方法,看是否同时成功同时失败
     *
     * 效果：控制台报错，主要是因为第二项失败，而第一项名字修改也没有成功
     */
    @Test
    public void testDemo3() {
        userService.updateTwice(7369, "SMITH", 7499, 1700.0);
    }

    /**
     * 将sql修改正确
     *
     * 效果： 同时成功
     */
    @Test
    public void testDemo4() {
        userService.updateTwice(7369, "SMITH", 7499, 1700.0);
    }

    /**
     * 添加日子功能，在日志中查看事务
     */
    @Test
    public void testDemo5(){
        String s = userService.selectEmpNameById(7369);
        System.out.println(s);
    }

    @Test
    public void test3() {
        String  sql = "select empno,ename from emp where sal = ?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, 99999);
        for (Map<String, Object> map : maps) {
            System.out.println(map.get("empno"));
            System.out.println(map.get("ename"));
            System.out.println("=========");
        }
    }

}