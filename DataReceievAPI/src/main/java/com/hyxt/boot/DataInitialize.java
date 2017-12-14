package com.hyxt.boot;

import cn.com.cnpc.vms.common.cache.LocalCache;
import cn.com.cnpc.vms.common.cache.LocalCacheManager;
import com.hyxt.utils.Color;
import com.hyxt.utils.QueryUitl;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuxiaofei
 * @version v1.0
 * @Description 数据初始化
 * @Date:2017/12/13
 * @Modifide By:
 **/
@Component
@Order(value = 1)
public class DataInitialize implements CommandLineRunner {

    static Log log = LogFactory.getLog(DataInitialize.class);
    // <接入码,平台ID>
    public static LocalCache accessCodeToBusinessCache = LocalCacheManager.getCache("_accessCodeToBusiness");
    // <车牌号+颜色,List<平台ID>>
    public static LocalCache plateToBusinessCache = LocalCacheManager.getCache("_plateToBusiness");
    // <平台接入码，加密因子>
    public static LocalCache accessCodeToM1IA1IC1Cache = LocalCacheManager.getCache("_codes");

    @Override
    public void run(String... strings) throws Exception {
        loadVehicleInfo();
        addCommandToList();

    }

    public static List<String> commandList;

    void loadVehicleInfo() {


        //车辆静态信息,平台信息
        QueryRunner qr = QueryUitl.getQueryRunner();
        String sql_str1 = "SELECT CENTERID,ID,M1,IC1,IA1 FROM JTB_PLATINFO";
        //centerID是接入码,id是平台ID
        try {
            Map<String, String> resultMap = qr.query(sql_str1, new ResultSetHandler<Map<String, String>>() {

                @Override
                public Map<String, String> handle(ResultSet rs) throws SQLException {

                    Map<String, String> tmpMap = new HashMap<String, String>();
                    int count = 0;
                    while (rs.next()) {
                        String centerId = rs.getString("CENTERID");
                        String ID = rs.getString("ID");
                        int M1 = rs.getInt("M1");
                        int IA1 = rs.getInt("IA1");
                        int IC1 = rs.getInt("IC1");

                        tmpMap.put(centerId, ID);
                        accessCodeToM1IA1IC1Cache.put(centerId, new int[]{M1, IA1, IC1});
                        System.out.println("Load平台数据...." + ++count);
                    }
                    return tmpMap;
                }
            });
            System.out.println(resultMap);
            // 装载到本地缓存
            accessCodeToBusinessCache.putAll(resultMap);
            String sql_str2 = "SELECT V.ID_NUMBER,V.COLOR_ID_NUMBER_ID,P.PID FROM V_VEHICLEINFO V INNER JOIN  JTB_PLAT_VEHICLE P ON V.VEHICLE_ID=P.VID where V.VEHICLE_ID in ( select vid from v_vehicleinfo_up )";
            Map<String, List<Integer>> plateMap = qr.query(sql_str2, new ResultSetHandler<Map<String, List<Integer>>>() {
                @Override
                public Map<String, List<Integer>> handle(ResultSet rs) throws SQLException {
                    Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
                    int count = 0;
                    while (rs.next()) {
                        try {

                            String id_Number = rs.getString("ID_NUMBER");
                            String color = rs.getString("COLOR_ID_NUMBER_ID");
                            //部标颜色和系统颜色的转化
                            color = Color.conColor(color);
                            int pid = rs.getInt("PID");
                            String key = id_Number + color;
                            if (map.containsKey(key)) {
                                // 有这个key
                                map.get(key).add(pid);
                            } else {
                                List<Integer> list = new ArrayList<Integer>();
                                list.add(pid);
                                map.put(key, list);
                            }
                            log.info("Load车辆数据.... key " + key + " to " + map.get(key));
                        } catch (Exception e) {
                            log.warn("", e);
                        }
                        // System.out.println();
                    }
                    return map;
                }
            });
            // 装载本地缓存
            plateToBusinessCache.putAll(plateMap);

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

    static void addCommandToList() {
        commandList = new ArrayList<String>();
        // commandList.add(""+0x1601);
        // commandList.add(""+0x1501);
        // commandList.add(""+0x1502);
        // commandList.add(""+0x1503);
        // commandList.add(""+0x1504);
        // commandList.add(""+0x1505);
        // commandList.add(""+0x1401);
        //主动上报报警处理结果信息
        commandList.add("" + 0x1403);
        //上报报警信息
        commandList.add("" + 0x1402);
        //上传车辆注册信息
        commandList.add("" + 0x1201);
        //实时上传车辆定位信息
        commandList.add("" + 0x1202);
        //车辆定位信息自动补报
        commandList.add("" + 0x1203);
        // commandList.add(""+0x1205);
        // commandList.add(""+0x1206);
        //申请交换指定车辆定位信息请求
        commandList.add("" + 0x1207);
        //取消交换指定车辆定位信息请求
        commandList.add("" + 0x1208);
        //补发车辆定位信息请求
        commandList.add("" + 0x1209);
        // commandList.add(""+0x120A);
        // commandList.add(""+0x120B);
        commandList.add("" + 0x120C);
        commandList.add("" + 0x120D);
        // commandList.add("" + 0x1001);
        // commandList.add("" + 0x1005);
    }

}
