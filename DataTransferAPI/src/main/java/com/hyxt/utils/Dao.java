package com.hyxt.utils;


import com.hyxt.DO.pojo.*;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.*;

public class Dao {
	
	/**
	 * 一下防止classnotfound，可能是对象长期不使用，被类加载器卸载，这样保持引用，应该不会被类卸载 
	 */
	private final static Department dp1 = new Department();
	private final static PlatInfo pi1 = new PlatInfo();
	private final static SuperiorPlatInfo sp1 = new SuperiorPlatInfo();
	private final static SuperiorPlat_Vehicle sv1 = new SuperiorPlat_Vehicle();
	private final static Vehicle v1 = new Vehicle();
	private final static VehiclePlat vp1 = new VehiclePlat();
	/**
	 * 一下防止classnotfound，可能是对象长期不使用，被类加载器卸载，这样保持引用，应该不会被类卸载
	 */
	
	
	// 通过企业名查询未上报的企业信息
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Department> queryDepartmentByName(String departmentName) throws SQLException {
		String sql = "select * from p_department where org_name like ? and org_id  not in (select org_id from p_department_up) and deleted =0";

		List<Department> list = null;
		Object[] param = { "%" + departmentName + "%" };

		QueryRunner qr = QueryUitl.getQueryRunner();
		try {
			list = (List<Department>) qr.query(sql, new BeanListHandler(Department.class), param);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(sql);
		return list;
	}

	// 修改企业上报状态，将上报的企业放入上报表中
	public int departmentUp(String id, String license) {
		String sql = "insert into p_department_up (org_id,license) values(?,?)";
		QueryRunner qr = QueryUitl.getQueryRunner();
		// int count = 0;
		try {
			// for (String i : intid) {
			qr.update(sql, id, license);
			// count++;
			// }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(sql);
		return 1;
	}

	public int departmentUp1(String[] intid) {
		String sql = "insert into p_department_up (org_id) values(?)";
		QueryRunner qr = QueryUitl.getQueryRunner();
		int count = 0;
		try {
			for (String i : intid) {
				qr.update(sql, i);
				count++;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(sql);
		return count;
	}

	// 查询已上报的企业
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Department> queryUpDepartment() {
		// TODO Auto-generated method stub
		String sql = "select * from p_department where org_id in (select org_id from p_department_up)";
		QueryRunner qr = QueryUitl.getQueryRunner();
		List<Department> list = null;
		try {
			list = (List<Department>) qr.query(sql, new BeanListHandler(Department.class));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(sql);
		return list;
	}

	// 移除已上报的企业
	@SuppressWarnings("unused")
	public int deleteUpDepartment(int[] intid) {
		String sql = "delete p_department_up where org_id=?";
		QueryRunner qr = QueryUitl.getQueryRunner();
		List<Department> list = null;
		int count = 0;
		for (int i : intid) {
			try {
				qr.update(sql, i);
				count++;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println(sql);
		return count;
	}

	// 通过车牌号查询未上报的车辆，并且实现把平台的信息给带出来
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Vehicle> queryVehicleByVin(String vin) {
		String sql = "select v.*,p.org_id,pu.license as org_license from v_vehicleinfo v,p_department p,p_department_up pu " + " where p.org_id=pu.org_id(+) and v.org_code=p.org_code "
				+ " and v.org_code in (select org_code from p_department d, p_department_up du ) "
				+ " and v.vehicle_id not in (select vid from v_vehicleinfo_up) and  v.id_number like ? and v.delete_flag = 0";
		List<VehiclePlat> list = null;
		Object[] param = { "%" + vin + "%" };
		QueryRunner qr = QueryUitl.getQueryRunner();
		try {
			System.out.println("开始");
			list = (List<VehiclePlat>) qr.query(sql, new BeanListHandler(VehiclePlat.class), param);
			Map<String, Vehicle> map = new TreeMap<String, Vehicle>();
			for (VehiclePlat vp : list) {
				if (!map.containsKey(vp.getId_number())) {
					Vehicle vehicle = new Vehicle();
					vehicle.setColor_id_number_id(Color.conColor(vp.getColor_id_number_id()));
					vehicle.setId_number(vp.getId_number());
					vehicle.setOrg_id(vp.getOrg_id());
					if (vp.getRegion_property() == 0) {
						vehicle.setRegion_property(Integer.valueOf(AreaUtil.getAreaCode(vp.getId_number())));
					} else {
						vehicle.setRegion_property(vp.getRegion_property());
					}
					vehicle.setTransport_permits(vp.getTransport_permits());
					vehicle.setVehicle_id(vp.getVehicle_id());
					vehicle.setPlat(new ArrayList<SuperiorPlatInfo>());
					vehicle.setVcode(AreaUtil.getVechileCode(vehicle.getId_number(), vehicle.getColor_id_number_id()));
					map.put(vp.getId_number(), vehicle);
				}
				if (vp.getPid() != 0) {
					SuperiorPlatInfo sp = new SuperiorPlatInfo();
					sp.setId(vp.getPid());
					sp.setName(vp.getpName());
					map.get(vp.getId_number()).getPlat().add(sp);
				}

			}
			ArrayList<Vehicle> resultlist = new ArrayList<Vehicle>();
			Set keySet = map.keySet();
			for (Object key : keySet) {
				resultlist.add(map.get(key));
			}
			System.out.println(sql);
			return resultlist;
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Vehicle>();
		}
	}

	//
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<SuperiorPlatInfo> queryPlatByVid(int id) {
		String sql = "select * from jtb_platinfo where id not in (select pid from jtb_plat_vehicle)";
		List<SuperiorPlatInfo> list = null;
		Object[] param = { id };
		QueryRunner qr = QueryUitl.getQueryRunner();
		try {
			list = (List<SuperiorPlatInfo>) qr.query(sql, new BeanListHandler(SuperiorPlatInfo.class), param);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(sql);
		return list;
	}

	public List<PlatInfo> queryPlats() {
		String sql = "select * from jtb_platinfo";
		QueryRunner qr = QueryUitl.getQueryRunner();
		List<PlatInfo> list = null;
		try {
			list = qr.query(sql, new BeanListHandler<PlatInfo>(PlatInfo.class));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public PlatInfo getPlatById(String id) {
		String sql = "select * from jtb_platinfo where id = ?";
		QueryRunner qr = QueryUitl.getQueryRunner();
		try {
			Object[] param = { id };
			return qr.query(sql, new BeanHandler<PlatInfo>(PlatInfo.class), param);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deletePlatById(String id) {
		String sql = "delete from jtb_platinfo where id = ?";
		QueryRunner qr = QueryUitl.getQueryRunner();
		try {
			Object[] param = { id };
			qr.update(sql, param);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insert(int id, String name, int typeid, String ip, short port, String userid, String password, String centerid, int m1, int iA1, int iC1, String version, String org, String person,
                       String phone, String remark) {
		String sql = "insert into jtb_platinfo values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		QueryRunner qr = QueryUitl.getQueryRunner();
		Object[] params = { id, name, typeid, ip, port, userid, password, centerid, m1, iA1, iC1, version, org, person, phone, remark };
		try {
			qr.update(sql, params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void update(int id, String name, int typeid, String ip, short port, String userid, String password, String centerid, int m1, int iA1, int iC1, String version, String org, String person,
                       String phone, String remark) {
		String sql = "update  jtb_platinfo set  name=?,typeid=?,ip=?,port=?,userid=?,password=?,centerid=?,m1=?,ia1=?,ic1=?,version=?, person=?,phone=?,remark=? where id=?";
		QueryRunner qr = QueryUitl.getQueryRunner();
		Object[] params = { name, typeid, ip, port, userid, password, centerid, m1, iA1, iC1, version, person, phone, remark, id };
		try {
			qr.update(sql, params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Department> queryAlreadyUpDepartment() {
		String sql = "select p.*,p_department_up.license from p_department p,p_department_up " + "where p.org_id in (select org_id from p_department_up) and p.org_id=p_department_up.org_id(+)";

		List<Department> list = null;
		QueryRunner qr = QueryUitl.getQueryRunner();
		try {
			list = (List<Department>) qr.query(sql, new BeanListHandler(Department.class));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(sql);
		return list;
	}

	/**
	 * @param vid
	 * @param id
	 * @param license
	 * @return
	 */
	public void platVehicleUp(String vid, String[] id, String license) {
		String sql = "insert into JTB_PLAT_VEHICLE values (?,?)";
		QueryRunner qr = QueryUitl.getQueryRunner();
		try {
			qr.update("insert into v_vehicleinfo_up (vid,license) values ('" + vid + "','" + license + "')");
			qr.update("delete from JTB_PLAT_VEHICLE where vid =" + vid);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		for (String sid : id) {
			Object[] params = { vid, sid };
			try {
				qr.update(sql, params);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param vin
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Vehicle> queryAlreadyVehicleByVin(String vin) {
		String sql = "select v.*,vu.license as org_license from v_vehicleinfo v,v_vehicleinfo_up vu" + " where v.vehicle_id = vu.vid "
				+ " and v.org_code in (select org_code from p_department d, p_department_up du ) "
				+ " and v.vehicle_id in (select vid from v_vehicleinfo_up) and  v.id_number like ? and v.delete_flag = 0";

		List<VehiclePlat> list = null;
		Object[] param = { "%" + vin + "%" };
		QueryRunner qr = QueryUitl.getQueryRunner();
		try {
			System.out.println("开始");
			list = (List<VehiclePlat>) qr.query(sql, new BeanListHandler(VehiclePlat.class), param);
			Map<String, Vehicle> map = new TreeMap<String, Vehicle>();
			for (VehiclePlat vp : list) {
				if (!map.containsKey(vp.getId_number())) {
					Vehicle vehicle = new Vehicle();
					vehicle.setColor_id_number_id(Color.conColor(vp.getColor_id_number_id()));
					vehicle.setId_number(vp.getId_number());
					vehicle.setOrg_id(vp.getOrg_id());
					if (vp.getRegion_property() == 0) {
						vehicle.setRegion_property(Integer.valueOf(AreaUtil.getAreaCode(vp.getId_number())));
					} else {
						vehicle.setRegion_property(vp.getRegion_property());
					}
					vehicle.setTransport_permits(vp.getTransport_permits());
					vehicle.setVehicle_id(vp.getVehicle_id());
					vehicle.setVcode(AreaUtil.getVechileCode(vehicle.getId_number(), vehicle.getColor_id_number_id()));
					vehicle.setPlat(new ArrayList<SuperiorPlatInfo>());
					vehicle.setOrg_license(vp.getOrg_license());
					map.put(vp.getId_number(), vehicle);

				}
				if (vp.getPid() != 0) {
					SuperiorPlatInfo sp = new SuperiorPlatInfo();
					sp.setId(vp.getPid());
					sp.setName(vp.getpName());
					map.get(vp.getId_number()).getPlat().add(sp);
				}

			}
			ArrayList<Vehicle> resultlist = new ArrayList<Vehicle>();
			Set keySet = map.keySet();
			for (Object key : keySet) {
				resultlist.add(map.get(key));
			}
			System.out.println(sql);
			return resultlist;
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Vehicle>();
		}
	}

	/**
	 * @param id
	 */
	public void deleteVehicle(String id) {
		String sql = "delete from v_vehicleinfo_up where vid = ?";
		QueryRunner qr = QueryUitl.getQueryRunner();
		try {
			Object[] param = { id };
			qr.update(sql, param);
			qr.update("delete from JTB_PLAT_VEHICLE where vid =" + id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Department getDepartmentById(String id) {
		String sql = "select p.*,pu.license from p_department p ,p_department_up pu where p.org_id = ? and p.org_id = pu.org_id(+) ";
		QueryRunner qr = QueryUitl.getQueryRunner();
		try {
			Object[] params = { id };
			Department d = qr.query(sql, new BeanHandler<Department>(Department.class), params);
			return d;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param vid
	 * @return
	 */
	public Vehicle getVehicleById(String vid) {
		String sql = "select v.*,vu.license as org_license from v_vehicleinfo v,v_vehicleinfo_up vu where v.vehicle_id = ? and v.vehicle_id = vu.vid";

		QueryRunner qr = QueryUitl.getQueryRunner();
		try {
			Object[] params = { vid };

			Vehicle d = qr.query(sql, new BeanHandler<Vehicle>(Vehicle.class), params);
			if (d.getRegion_property() == 0) {
				d.setRegion_property(Integer.valueOf(AreaUtil.getAreaCode(d.getId_number())));
			} else {
				d.setRegion_property(d.getRegion_property());
			}
			d.setColor_id_number_id(Color.conColor(d.getColor_id_number_id()));
			d.setVcode(AreaUtil.getVechileCode(d.getId_number(), d.getColor_id_number_id()));
			return d;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public PlatInfo selectPlatByCode(String string) {
		String sql = "select * from jtb_platinfo where centerid = ?";
		QueryRunner qr = QueryUitl.getQueryRunner();

		List<PlatInfo> list = null;
		try {
			list = qr.query(sql, new BeanListHandler<PlatInfo>(PlatInfo.class), string);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list.get(0);
	}

	public List<Vehicle> platVehicleInDepartmentUp(String orgcode, String license, String[] id) {

		String sqllike = "select * from v_vehicleinfo where org_code like '" + orgcode + "%' and delete_flag = 0";

		String sql = "insert into JTB_PLAT_VEHICLE values (?,?)";
		QueryRunner qr = QueryUitl.getQueryRunner();
		List<Vehicle> vlist = null;
		try {
			vlist = (List<Vehicle>) qr.query(sqllike, new BeanListHandler<Vehicle>(Vehicle.class));
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		for (Vehicle vehicle : vlist) {
			try {
				qr.update("delete from v_vehicleinfo_up where vid =" + vehicle.getVehicle_id());
				System.out.println("delete from v_vehicleinfo_up where vid =" + vehicle.getVehicle_id());
				qr.update("insert into v_vehicleinfo_up (vid,license) values ('" + vehicle.getVehicle_id() + "','" + license + "')");
				System.out.println("insert into v_vehicleinfo_up (vid,license) values ('" + vehicle.getVehicle_id() + "','" + license + "')");
				qr.update("delete from JTB_PLAT_VEHICLE where vid =" + vehicle.getVehicle_id());
				System.out.println("delete from JTB_PLAT_VEHICLE where vid =" + vehicle.getVehicle_id());
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			for (String sid : id) {
				Object[] params = { vehicle.getVehicle_id(), sid };
				System.out.println("insert into JTB_PLAT_VEHICLE values (?,?)");
				try {
					qr.update(sql, params);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (vehicle.getRegion_property() == 0) {
				vehicle.setRegion_property(Integer.valueOf(AreaUtil.getAreaCode(vehicle.getId_number())));
			} else {
				vehicle.setRegion_property(vehicle.getRegion_property());
			}
			vehicle.setOrg_license(license);
			vehicle.setColor_id_number_id(Color.conColor(vehicle.getColor_id_number_id()));
			vehicle.setVcode(AreaUtil.getVechileCode(vehicle.getId_number(), vehicle.getColor_id_number_id()));
		}

		return vlist;
	}

	// 更新v_vehicleinfo_up表
	public void updateV_vehicleinfo_up(HashSet<Integer> set) {
		String sql = "merge into v_vehicleinfo_up v using (select v.vehicle_id as vid,pdu.license from "
				+ "v_vehicleinfo v ,p_department pd , p_department_up pdu "
				+ "where v.org_code like pd.org_code||'%' and  pd.org_id = pdu.org_id " 
				+ "and (v.DELETE_FLAG <> 1 or v.DELETE_FLAG is null) and v.vehicle_id=? ) nv "
				+ "on (v.vid=nv.vid  and v.license=nv.license) " 
				+ "when not matched then insert values (nv.vid,nv.license);";
		QueryRunner qr = QueryUitl.getQueryRunner();
		for (Integer it : set) {
			try {
				qr.update(sql, it);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// 更新JTB_plat_vehicle表
	public void updateJTB_plat_vehicle(HashSet<Integer> set,String authCode) {
		String sql = "merge into jtb_plat_vehicle using (select v.vehicle_id as vid "
					+"from v_vehicleinfo v ,p_department pd , p_department_up pdu "
					+"where v.org_code like pd.org_code||'%' "
					+"and  pd.org_id = pdu.org_id and (v.DELETE_FLAG <> 1 or v.DELETE_FLAG is null) "
					+"and v.vehicle_id=?) nv "
					+"on (nv.vid=jtb_plat_vehicle.vid and jtb_plat_vehicle.pid=?) "
					+"when not matched then insert values(nv.vid,?)";
		QueryRunner qr = QueryUitl.getQueryRunner();
		for (Integer it : set) {
			try {
				qr.update(sql, it, authCode,authCode);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void cleanJTB_plat_vehicle(String authCode) {
		String sql = "delete from jtb_plat_vehicle where pid = "+authCode;
		QueryRunner qr = QueryUitl.getQueryRunner();
		try {
			qr.update(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
