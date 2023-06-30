package com.fhtiger.helper.utils.helpful;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.fhtiger.helper.utils.ListUtil;
import com.fhtiger.helper.utils.StringValueUtil;
import org.springframework.web.util.JavaScriptUtils;


/**
 * SimpleGarbleUtil
 *
 * @author LFH
 * @since 2021年05月18日 11:56
 */
@SuppressWarnings({ "unused","WeakerAccess", "UnnecessaryLocalVariable" })

public final class SimpleGarbleUtil {

	private SimpleGarbleUtil() throws IllegalAccessException{
		throw new IllegalAccessException("The util-class do not need to be instantiated");
	}

	public static final String SPLIT_CHARS="@#$%_&^*!,.";

	/**
	 * 获取两者之间的随机值
	 * @param min 小值
	 * @param max 大值
	 * @return -
	 */
	public static int randomSize(int min,int max){
		Random random = new Random();

		return random.nextInt(max)%(max-min+1)+min;
	}

	public static List<Character> getSplitChars(int length){

		List<Character> chars = new ArrayList<>();

		Random random = new Random();

		int charLength = SPLIT_CHARS.length();

		while (length>0){
			/*随机摘选，拼装成一组分隔符定义*/
			chars.add(SPLIT_CHARS.charAt(random.nextInt(charLength)));
			length--;
		}

		return chars;
	}

	public static List<Integer> getGarbleIndexes(int length){

		List<String> indexes = new ArrayList<>();

		Random random = new Random();

		int indexLength = length;

		while (length>0){
			/*随机生成一组混淆顺序*/
			indexes.add(random.nextInt(indexLength)+","+--length);
		}

		/*得到混淆顺序*/
		ListUtil.sort(indexes,(k)->k.replaceFirst(",\\d+",""));

		/*混淆顺序对应实际的顺序*/
		List<Integer> newIndexes =indexes.stream().map(k->Integer.parseInt(k.replaceFirst("\\d+,",""))).collect(
				Collectors.toList());

		return newIndexes;
	}

	public static GarbleContent garbleContent(String content,int splitSize,long timestamp){

		/*对文本进行分段处理*/
		List<String> contentList = StringValueUtil.splitBySize(content,splitSize);

		int length = contentList.size();

		List<Character> splitChars = getSplitChars(length);

		List<Integer> garbleIndexes = getGarbleIndexes(length);

		/*混淆文本分隔符*/
		String splitStr = "---%s---";

		/*此时间参数因子和前端保持一致*/
		long timestampIndex = timestamp / 1000;

		StringBuilder contentStr = new StringBuilder();
		StringBuilder indexStr = new StringBuilder();

		for (Integer garbleIndex : garbleIndexes) {
			/*每段进行脚本转义，防止发送前端执行时，出现脚本识别问题*/
			contentStr.append(JavaScriptUtils.javaScriptEscape( contentList.get(garbleIndex)));
			indexStr.append(garbleIndex+timestampIndex);
			if(--length>0){
				/*最后一项不再追加分隔符*/
				contentStr.append(String.format(splitStr, splitChars.get(garbleIndex)));
				indexStr.append(",");
			}
		}

		return new GarbleContent(contentStr.toString(),indexStr.toString());
	}
	
	public static String deGarbleContent(String garbleContent,String garbleKey,long timestamp){

		/*还原时间参数因子*/
		long timestampIndex = (timestamp / 1000);

		/*获取原始顺序*/
		String[] strIndex = garbleKey.split(",");

		/*获取拼图字符串组*/
		String[] contentStr = garbleContent.split(String.format("---[%s]---", SPLIT_CHARS));

		String [] contentArray = new String[contentStr.length];

		int indexLength = strIndex.length;

		/*还原拼图*/
		for (int i = 0; i < indexLength; i++) {
			contentArray[(int)(Long.parseLong(strIndex[i]) - timestampIndex)]=contentStr[i];
		}

		/*合并内容*/
		return String.join("",contentArray);
	}

	/**
	 * 混淆结果
	 */
	public static class GarbleContent{
		/**
		 * 混淆后文本
		 */
		private String garbleContent;
		/**
		 * 混淆顺序
		 */
		private String garbleIndexes;

		public GarbleContent(String garbleContent, String garbleIndexes) {
			this.garbleContent = garbleContent;
			this.garbleIndexes = garbleIndexes;
		}

		public String getGarbleContent() {
			return garbleContent;
		}

		public String getGarbleIndexes() {
			return garbleIndexes;
		}
	}
}
