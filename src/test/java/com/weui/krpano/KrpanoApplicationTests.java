package com.weui.krpano;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KrpanoApplicationTests {

    @Test
    public void contextLoads() {
        Long start = System.currentTimeMillis();
        System.out.println("运行时间：" + (System.currentTimeMillis() - start));
    }
}
