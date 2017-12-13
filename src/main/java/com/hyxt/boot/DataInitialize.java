package com.hyxt.boot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author yuxiaofei
 * @version v1.0
 * @Description
 * @Date:2017/12/13
 * @Modifide By:
 **/
@Component
@Order(value=1)
public class DataInitialize implements CommandLineRunner {
    @Override
    public void run(String... strings) throws Exception {

    }
}
