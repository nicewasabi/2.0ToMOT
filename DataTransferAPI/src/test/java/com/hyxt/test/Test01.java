package com.hyxt.test;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author songm
 * @version v1.0
 * @Description
 * @Date: Create in 14:24 2017/12/14
 * @Modifide By:
 **/
public class Test01 {

    @Test
    public void name() throws Exception {
        Map map = new HashMap();
        map.put("value",true);
        System.out.println("true".equals(map.get("value")));
    }
}
