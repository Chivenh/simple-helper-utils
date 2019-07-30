package com.fhtiger.utils.helperutils.util;

import com.fhtiger.utils.helperutils.helper.FileHelper;
import com.fhtiger.utils.helperutils.helper.Helper;
import com.fhtiger.utils.helperutils.helper.MethodHelper;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;

/**
 * 专用调试与小工具类
 * @author LFH:
 * @since 完善日期2017-4-13 时间11:23:02
 * @version lfh.1.0
 */
public class Tutil {

	private static Logger logger = LoggerFactory.getLogger(Tutil.class);

	private static final boolean use_flag = true;// 主控制标志(改成false禁止所有)
	private static boolean _flag = false;// 临时获取使用权.

	/**
	 * 提供外设置途径,在系统已禁用此类后,可通过设置_flag为true临时使用.
	 * @param _flag flag
	 */
	public static void set_flag(boolean _flag) {
		Tutil._flag = _flag;
	}

	@SuppressWarnings("unused")
	private static boolean check() {
		return (Tutil.use_flag || Tutil._flag);
	}

	/**
	 * 有值(错误信息)打印方法
	 */
	public static void consoleErr(Object v) {
		if (!check()) return;
		System.err.println(v);
	}

	/**
	 * 多值打印,制表符隔开
	 */
	public static void consoleErr(Object v1, Object... v2) {
		if (!check()) return;
		Object[] v = v2;
		System.err.println(v1);
		for (Object o : v) {
			System.err.print("\t" + o);
		}
		System.err.println();
	}

	/**
	 * 有值打印方法
	 */
	public static void consoleOut(Object v) {
		if (!check()) return;
		if (v instanceof Collection<?>) {
			consoleOut((Collection<?>) v);
		} else {
			System.out.println(v);
		}
	}

	/**
	 * 有值打印(collection)方法
	 */
	public static void consoleOut(Collection<?> v) {
		if (!check()) return;
		v.forEach(o -> System.out.print(o + "\n"));
	}

	/**
	 * 纯换行
	 */
	public static void consoleOut() {
		if (!check()) return;
		System.out.println();
	}

	/**
	 * 多值打印,制表符隔开
	 */
	public static void consoleOut(Object v1, Object... v2) {
		if (!check()) return;
		Object[] v = v2;
		System.out.println(v1);
		for (Object o : v) {
			System.out.print("\t" + o);
		}
		System.out.println();
	}

	/**
	 * 遍历数组
	 */
	public static void consoleOut(Object[] v) {
		if (!check()) return;
		System.out.println();
		if (v == null) return;
		for (Object o : v) {
			System.out.print(o + " ");
		}
		System.out.println();
	}

	/**
	 * 遍历数组,以指定字符串分隔
	 */
	public static void consoleOut(Object[] v, String r) {
		if (!check()) return;
		System.out.println();
		if (v == null) return;
		for (Object o : v) {
			System.out.print(o + r);
		}
		System.out.println();
	}

	/**
	 * 遍历数组,追加数字标记
	 */
	public static void consoleOut(Object[] v, int j) {
		if (!check()) return;
		System.out.println();
		if (v == null) return;
		int i = j;
		for (Object o : v) {
			System.out.print(o + " " + i++ + " ");
		}
		System.out.println();
	}

	/**
	 * 获取当前时间[yyyy-MM-dd]
	 * @param type {@link DateType}
	 * @return String
	 * @author LFH
	 * @since 2017年4月24日 上午10:27:40
	 */
	public static String getNow(DateType type) {
		return strDate(type, getNowDate(type));
	}

	public static Date getNowDate(DateType... type) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DateType.A.getValue());
		SimpleDateFormat tsdf = null;
		String cdate = null;
		Date now = null;
		try {
			if (type.length > 0) {
				DateType DT = type[0];
				tsdf = DT.getLocale() != null ? new SimpleDateFormat(DT.getValue(), DT.getLocale()) : new SimpleDateFormat(DT.getValue());

				cdate = tsdf.format(cal.getTime());
				now = tsdf.parse(cdate);

			} else {
				tsdf = sdf;
				cdate = tsdf.format(cal.getTime());
				now = tsdf.parse(cdate);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return now;
	}

	/**
	 * 创建新日期格式
	 *
	 * @param sdf 格式化字符串
	 * @return SimpleDateFormat
	 * @author LFH
	 * @since 2017年11月2日 上午9:37:46
	 */
	private static SimpleDateFormat createFormat(String sdf) {
		return new SimpleDateFormat(sdf);
	}

	/**
	 * 创建新日期格式(指定国际化)
	 *
	 * @param sdf 格式化字符串
	 * @return SimpleDateFormat
	 * @author LFH
	 * @since 2017年11月2日 上午9:37:46
	 */
	private static SimpleDateFormat createFormat(String sdf, Locale locale) {
		return new SimpleDateFormat(sdf, locale);
	}

	/**
	 * 将字符串以指定格式转化为日期.
	 *
	 * @param sdf 格式化字符串
	 * @param date 字符串日期
	 * @return {@link Date}
	 * @author LFH
	 * @since 2017年12月9日 上午8:51:11
	 */
	public static Date getDate(String sdf, String date) {
		Date d = null;
		try {
			d = createFormat(sdf).parse(date);
		} catch (ParseException e) {
			logger.error("error:{0}",e);
		}
		return d;
	}

	/**
	 * 将字符串以指定格式转化为日期(指定国际化)
	 *
	 * @param sdf 格式化字符串
	 * @param date 字符串日期
	 * @return {@link Date}
	 * @author LFH
	 * @since 2017年12月9日 上午8:51:11
	 */
	public static Date getDate(String sdf, Locale locale, String date) {
		Date d = null;
		try {
			d = createFormat(sdf, locale).parse(date);
		} catch (ParseException e) {
			logger.error("error:{0}",e);
		}
		return d;
	}

	/**
	 * 得到日期字符串
	 *
	 * @param sdf 格式化字符串
	 * @param date 字符串日期
	 * @return String
	 * @author LFH
	 * @since 2017年11月2日 上午11:23:31
	 */
	public static String strDate(DateType sdf, Date date) {
		if (date == null) {
			return "";
		}
		return sdf.getLocale() != null ? createFormat(sdf.getValue(), sdf.getLocale()).format(date) : createFormat(sdf.getValue()).format(date);

	}

	/**
	 * 向给定日期增加或减少指定时间.
	 *
	 * @param from 日期
	 * @param dValue 调整值
	 * @param type 调整类型
	 * @return {@link Date}
	 * @author LFH
	 * @since 2017年11月30日 上午9:26:52
	 */
	@SuppressWarnings("static-access") public static Date calcDate(Date from, int dValue, CalcType type) {
		Calendar c = new GregorianCalendar();
		c.setTime(from);
		switch (type) {
		case MONTH:
			c.add(c.MONTH, dValue);
			break;
		case DAY:
			c.add(c.DAY_OF_MONTH, dValue);
			break;
		case YEAR:
			c.add(c.YEAR, dValue);
			break;
		case HOUR:
			c.add(c.HOUR_OF_DAY, dValue);
			break;
		default:
			break;
		}
		return c.getTime();
	}

	/**
	 * 日期计算类型
	 */
	public enum CalcType {
		MONTH, DAY, YEAR, HOUR
	}

	/**
	 * 写json文件
	 *
	 * @param filePath 文件路径
	 * @param sets 文件内容
	 * @throws IOException {@link IOException}
	 * @author LFH
	 * @since 2017年12月9日 上午9:40:11
	 */
	public static void writeFile(String filePath, String sets) throws IOException {
		FileWriter fw = new FileWriter(filePath);
		PrintWriter out = new PrintWriter(fw);
		out.write(sets);
		out.println();
		fw.close();
		out.close();
	}

	private static final String[] BIG_NUMBER = new String[] { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十" };

	/**
	 * 获取1-10的数字汉字表示.
	 *
	 * @param index 1-10
	 * @return String {@link #BIG_NUMBER}
	 * @author LFH
	 * @since 2017年12月11日 下午3:39:17
	 */
	public static String getBigNumber(int index) {
		if (index > 0 && index < 11) {
			return BIG_NUMBER[index - 1];
		} else {
			return "";
		}
	}

	/**
	 * 通用toString()方法.
	 * @param obj 对象
	 * @return String
	 * @author LFH
	 * @since 2017年4月21日 下午1:11:53
	 * @deprecated since:2019/7/30 0030 17:28 ,please move to {@link com.fhtiger.utils.helperutils.helper.StringHelper#toString(Object)}
	 */
	@SuppressWarnings("rawtypes")
	@Deprecated
	public static String toString(Object obj) {
		if (obj == null) {
			return null;
		}
		Class c1 = obj.getClass();
		if (c1 == String.class) {
			return (String) obj;
		}
		if (c1.isArray()) {
			StringBuilder r = new StringBuilder(c1.getComponentType() + "[]{");
			for (int i = 0; i < Array.getLength(obj); i++) {
				if (i > 0) {
					r.append(",");
				}
				Object val = Array.get(obj, i);
				if (c1.getComponentType().isPrimitive()) r.append(val);
				else r.append(toString(val));
			}
			return r + "}";
		}
		StringBuilder r = new StringBuilder(c1.getName());
		r.append("{");
		do {
			r.append("[");
			Field[] fields = c1.getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);
			for (Field f : fields) {
				if (!Modifier.isStatic(f.getModifiers())) {
					if (!r.toString().endsWith("[")) {
						r.append(",\n");
					}
					r.append(f.getName()).append(" = ");
					try {
						Class t = f.getType();
						Object val = f.get(obj);
						if (t.isPrimitive()) {
							r.append(val);
						} else {
							r.append(toString(val));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			r.append("]");
			c1 = c1.getSuperclass();
		} while (c1 != null);
		r.append("}");
		return r.toString().replaceAll("\\[]", "");
	}

	/**
	 * 得到字符串
	 *
	 * @param o 原始值
	 * @param v 默认值
	 * @return String
	 */
	public static String getStr(Object o, String... v) {
		return o == null ? (v.length < 1 ? "" : v[0]) : o.toString().trim();
	}

	/**
	 * 得到整数
	 *
	 * @param o 原始值
	 * @param v 默认值
	 * @return int
	 */
	public static int getInt(Object o, int... v) {
		int t = 0;
		try {
			o = o == null || o.toString().trim().length() < 1 ? (v.length < 1 ? 0 : v[0]) : o.toString().trim();
			t = Integer.parseInt(o.toString());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return t;
	}

	/**
	 * 得到BigDecimal.
	 *
	 * @param o 原始值
	 * @return {@link BigDecimal}
	 */
	public static BigDecimal getBigDecimal(Object o) {
		try {
			if (o == null || o.toString().trim().length() < 1) {
				return null;
			} else {
				return new BigDecimal(getStr(o));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 得到整数
	 *
	 * @param o 原始值
	 * @param v 默认值
	 * @return long
	 */
	public static long getLong(Object o, int... v) {
		long t = 0;
		try {
			o = o == null || o.toString().trim().length() < 1 ? (v.length < 1 ? 0 : v[0]) : o.toString().trim();
			t = Long.parseLong(o.toString());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return t;
	}

	/**
	 * 得到浮点数.
	 *
	 * @param o 原始值
	 * @param v 默认值
	 * @return double
	 */
	public static double getDouble(Object o, double... v) {
		double t = 0.0;
		try {
			o = o == null || o.toString().trim().length() < 1 ? (v.length < 1 ? 0.0 : v[0]) : o.toString().trim();
			t = Double.parseDouble(o.toString());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return t;
	}

	/**
	 * 得到排序字符串.
	 *
	 * @param o 原始值
	 * @param v 默认值
	 * @return String
	 */
	public static String getOrder(Object o, String... v) {
		return o == null || o.toString().trim().length() < 1 ?
				(v.length < 1 ? "ASC" : (v[0].length() < 3 ? "ASC" : ("asc,desc".contains(v[0])) ? v[0] : "ASC")) :
				("asc,desc".contains(o.toString().trim()) ? o.toString().trim() : "ASC");
	}

	/**
	 * 判断是否为空(空则真|反之假.)
	 *
	 * @param o 值
	 * @return boolean
	 */
	public static boolean isNull(Object o) {
		if (o == null) {
			return true;
		} else {
			return o instanceof String && "".equals(o.toString().trim());
		}
	}

	/**
	 * 默认转换一个字符串成yyyy-MM-dd日期格式.
	 * @param o 值
	 * @param type {@link DateType}
	 * @return Date
	 * @author LFH
	 * @since 2017年4月25日 下午5:36:05
	 */
	public static Date getDate(Object o, DateType type) {
		if (o == null) {
			return null;
		} else {
			if (!isNull(o)) {
				SimpleDateFormat spl = new SimpleDateFormat(DateType.A.getValue());
				SimpleDateFormat tspl = null;
				if (type !=null) {
					if (type.getLocale() != null) {

						tspl = new SimpleDateFormat(type.getValue(), type.getLocale());
					} else {

						tspl = new SimpleDateFormat(type.getValue());

					}

				} else {
					tspl = spl;
				}
				try {
					return tspl.parse(o.toString());
				} catch (ParseException e) {
					throw new RuntimeException(e.getMessage());
				}
			} else {
				return null;
			}
		}
	}

	public static Date getDate(Object o){
		return getDate(o,null);
	}

	/**
	 * 深克隆一个对象
	 * @param preObj 要克隆的对象
	 * @return Object 新对象
	 * @author LFH
	 * @since 2017年7月4日 上午9:52:34
	 */
	public static Object deepClone(Object preObj) {
		ObjectInputStream ois = null;
		Object nowObj = null;
		try {
			// 将对象写到流里
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(preObj);
			// 从流里读回来
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ois = new ObjectInputStream(bis);
			nowObj = ois.readObject();
		} catch (Exception e) {
			logger.error("error:{0}",e);
		}
		return nowObj;
	}

	/**
	 * 深克隆一个对象(依赖于对象的getter和setter)
	 * @param pre  要克隆的对象
	 * @return Object 新对象
	 */
	public static Object cloneByProps(Object pre) {
		Class<?> type = pre.getClass();
		Object cur = null;
		try {
			cur = type.newInstance();
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();// 获取属性数组
			for (PropertyDescriptor pd : propertyDescriptors) {
				String propertyName = pd.getName();// 获取属性名
				if (!"class".equalsIgnoreCase(propertyName)) {
					Method get = pd.getReadMethod();// 得到读属性方法(get...())
					Method set = pd.getWriteMethod();
					if (get != null) {
						Object value = get.invoke(pre);// 获取属性值
						if (value != null && set != null) {
							set.invoke(cur, value);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(type.getName() + " 没有可用构造器,克隆对象失败!" + e.getMessage());
		}
		return cur;
	}

	/**
	 * 以读写属性的方法复制对象.
	 *
	 * @param pre 要复制的对象
	 * @param cur 接收复制的目标对象
	 * @return 目标对象
	 * @author LFH
	 * @since 2018年4月14日 下午11:13:46
	 */
	public static Object copyProps(Object pre, Object cur) {
		if (pre.getClass() != cur.getClass()) {
			return cur;
		}
		Class<?> type = pre.getClass();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();// 获取属性数组
			for (PropertyDescriptor pd : propertyDescriptors) {
				String propertyName = pd.getName();// 获取属性名
				if (!"class".equalsIgnoreCase(propertyName)) {
					Method get = pd.getReadMethod();// 得到读属性方法(get...())
					Method set = pd.getWriteMethod();
					if (get != null) {
						Object value = get.invoke(pre);// 获取属性值
						if (value != null && set != null) {
							set.invoke(cur, value);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(type.getName() + " 没有可用构造器,克隆对象失败!" + e.getMessage());
		}
		return cur;
	}

	/**
	 * 对象转Map输出
	 *
	 * @param obj 要转换的对象
	 * @return {@link Map} 对应对象属性名和值
	 * @author LFH
	 * @since 2017年5月31日 下午2:48:01
	 */
	public static Map<String, Object> objToMap(Object obj) {
		Map<String, Object> map = new HashMap<>();
		try {
			Class<?> type = obj.getClass();
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();// 获取属性数组
			for (PropertyDescriptor pd : propertyDescriptors) {
				String propertyName = pd.getName();// 获取属性名
				if (!"class".equalsIgnoreCase(propertyName)) {
					Method method = pd.getReadMethod();// 得到读属性方法(get...())
					Object o = method.invoke(obj);// 获取属性值
					if (o != null) {// 填充进map
						map.put(propertyName, o);
					} else {
						map.put(propertyName, null);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return map;
	}

	/**
	 * 将 {@link Map} 转为对应对象
	 * 反之Object转Map 有方法:{@link #objToMap(Object)}
	 * @param objClass 目标对象Class
	 * @param map 数据集
	 * @return 返回转换结果对象
	 * @author LFH
	 * @since 2017年5月31日 下午3:07:43
	 */
	public static Object mapToObj(Class<?> objClass, Map<String, Object> map) {
		Object newObj = null;
		try {
			newObj = objClass.newInstance();
			BeanInfo beanInfo = Introspector.getBeanInfo(objClass);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();// 获取属性数组
			for (PropertyDescriptor pd : propertyDescriptors) {
				String propertyName = pd.getName();// 获取属性名
				if (!map.containsKey(propertyName)) {
					Method method = pd.getWriteMethod();// 得到写属性方法(set...())
					Object value = map.get(propertyName);// 从map中获取对应属性值
					method.invoke(newObj, value);// 写入属性值.
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return newObj;
	}

	/**
	 * 上传文件到fpt服务器
	 * @param url 服务器地址
	 * @param port 端口，如果小于等于零  默认21
	 * @param username 用户名
	 * @param password 密码
	 * @param path 上传目录
	 * @param filename 文件名
	 * @param input 输入流
	 * @return boolean
	 */
	public static boolean uploadFile(String url,int port,String username,String password,String path,String filename,InputStream input) {
		if(port<=0) port = 21;
		//初始化表示上传失败
		boolean result=false;
		FTPClient ftp= null;
		try{
			//创建ftpclient对象
			ftp = new FTPClient();
			int reply;
			// 连接FTP服务器
			// 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftp.connect(url,port);

			//登录ftp服务器
			ftp.login(username, password);

			//看返回的值是不是230，如果是，表示登陆成功
			reply=ftp.getReplyCode();

			//以2开头的返回值就会为真
			//System.out.println("登陆成功");
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return result;
			}

			// 转到指定上传目录
			ftp.changeWorkingDirectory(path);

			// 将上传文件存储到指定目录
			ftp.enterLocalPassiveMode();
			if(ftp.storeFile(filename, input)){
				logger.info("文件保存到服务器成功");
				result=true;
			}else{
				logger.info("文件保存到服务器失败");
				result=false;
			}

		}catch(Exception e){
			return result;
		}finally{
			try {
				//关闭输入流
				input.close();
			} catch (IOException e1) {
				logger.error("error:{0}",e1);
			}
			try {
				//退出ftp
				if(ftp!=null){
					ftp.logout();
				}
			} catch (IOException e) {
				logger.error("error:{0}",e);
			}

			if(ftp!=null&& ftp.isConnected()){
				try{
					ftp.disconnect();
				}catch(IOException ioe){
					logger.error("error:{0}",ioe);
				}
			}
		}

		logger.info("文件上传到服务器执行完毕---end");
		return result;
	}

	/**
	 * 执行系统命令行脚本
	 *
	 * @param command 命令行
	 * @return [0: 退出代码,0为正常退出; 1:执行中的输出信息]
	 */
	public static Object[] execute(List<String> command) {
		if (Helper.isEmpty(command)) return null;
		Process process = null;
		StringBuffer message = new StringBuffer();
		List<Serializable> results = new ArrayList<Serializable>();
		ProcessBuilder builder = null;
		BufferedReader in = null;
		builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		try {
			process = builder.start();
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				// 读取输出信息
				message.append(line).append("\n");
			}
			process.waitFor();
		} catch (Exception e) {
			// throw new MortalException(e);
		} finally {
			MethodHelper.close(in);
			if (process != null) process.destroy();
		}
		results.add(process.exitValue());
		results.add(message);
		return results.toArray();
	}

	public static Object[] execute(String[] command) {
		if (Helper.isEmpty(command)) return null;
		List<String> c = new ArrayList<String>();
		Helper.add(c, command);
		return execute(c);
	}
}