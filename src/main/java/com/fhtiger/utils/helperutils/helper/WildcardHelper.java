/*
 * Copyright 2002-2007,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fhtiger.utils.helperutils.helper;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 复制于com.opensymphony.xwork2.interceptor.MethodFilterInterceptorUtil方法.
 * 可使用通配符*,匹配除/外的任意字符.两个**匹配任意字符
 * 
 * @author LuoGang
 * @since 2019/7/30 17:37 updated by LFH
 */
public class WildcardHelper {

	/**
	 * 判断method是否在符合includeMethods,并且不在excludeMethods中
	 * 
	 * @param excludeMethods 要排除的方法,为空则不排除任何方法.
	 * @param includeMethods 要包含的方法,为空则包含所有方法
	 * @param method 要判断的方法
	 * @return method是否符合条件
	 */
	public static boolean include(String[] excludeMethods, String[] includeMethods, String method) {
		Set<String> includeMethodsSet = new HashSet<>();
		if (!Helper.isEmpty(includeMethods)) {
			Helper.add(includeMethodsSet, includeMethods);
		}
		Set<String> excludeMethodsSet = new HashSet<>();
		if (!Helper.isEmpty(excludeMethods)) {
			Helper.add(excludeMethodsSet, excludeMethods);
		}

		return include(excludeMethodsSet, includeMethodsSet, method);
	}

	/**
	 * Static method to decide if the specified <code>method</code> should be
	 * apply (not filtered) depending on the set of <code>excludeMethods</code>
	 * and <code>includeMethods</code>.
	 *
	 * <ul>
	 * <li><code>includeMethods</code> takes precedence over
	 * <code>excludeMethods</code></li>
	 * </ul>
	 * <b>Note:</b> Supports wildcard listings in includeMethods/excludeMethods
	 *
	 * @param excludeMethods
	 *            list of methods to exclude.
	 * @param includeMethods
	 *            list of methods to include.
	 * @param method
	 *            the specified method to check
	 * @return <tt>true</tt> if the method should be applied.
	 */
	public static boolean include(Set<String> excludeMethods, Set<String> includeMethods, String method) {
		if (excludeMethods == null) excludeMethods = Collections.emptySet();
		if (includeMethods == null) includeMethods = Collections.emptySet();
		// quick check to see if any actual pattern matching is needed
		boolean needsPatternMatch = false;
		for (String includeMethod : includeMethods) {
			if (!"*".equals(includeMethod) && includeMethod.contains("*")) {
				needsPatternMatch = true;
				break;
			}
		}

		for (String excludeMethod : excludeMethods) {
			if (!"*".equals(excludeMethod) && excludeMethod.contains("*")) {
				needsPatternMatch = true;
				break;
			}
		}

		// this section will try to honor the original logic, while
		// still allowing for wildcards later
		if (!needsPatternMatch && (includeMethods.contains("*") || includeMethods.size() == 0)) {
			if ( excludeMethods.contains(method) && !includeMethods.contains(method)) {
				return false;
			}
		}

		// test the methods using pattern matching
		WildcardMatcher wildcard = new WildcardMatcher();
		String methodCopy;
		if (method == null) { // no method specified
			methodCopy = "";
		} else {
			methodCopy = new String(method);
		}
		for (String pattern : includeMethods) {
			if (pattern.contains("*")) {
				int[] compiledPattern = wildcard.compilePattern(pattern);
				HashMap<String, String> matchedPatterns = new HashMap<>();
				boolean matches = wildcard.match(matchedPatterns, methodCopy, compiledPattern);
				if (matches) {
					return true; // run it, includeMethods takes precedence
				}
			} else {
				if (pattern.equals(methodCopy)) {
					return true; // run it, includeMethods takes precedence
				}
			}
		}
		if (excludeMethods.contains("*")) {
			return false;
		}

		// CHECK ME: Previous implementation used include method
		for (String pattern : excludeMethods) {
			if (pattern.contains("*")) {
				int[] compiledPattern = wildcard.compilePattern(pattern);
				HashMap<String, String> matchedPatterns = new HashMap<>();
				boolean matches = wildcard.match(matchedPatterns, methodCopy, compiledPattern);
				if (matches) {
					// if found, and wasn't included earlier, don't run it
					return false;
				}
			} else {
				if (pattern.equals(methodCopy)) {
					// if found, and wasn't included earlier, don't run it
					return false;
				}
			}
		}

		// default fall-back from before changes
		return includeMethods.size() == 0 || includeMethods.contains(method) || includeMethods.contains("*");
	}

	/**
	 * Same as {@link #include(Set, Set, String)}, except that
	 * <code>excludeMethods</code> and <code>includeMethods</code> are supplied
	 * as comma separated string.
	 * 
	 * @param excludeMethods
	 *            comma seperated string of methods to exclude.
	 * @param includeMethods
	 *            comma seperated string of methods to include.
	 * @param method
	 *            the specified method to check
	 * @return <tt>true</tt> if the method should be applied.
	 */
	public static boolean include(String excludeMethods, String includeMethods, String method) {
		Set<String> includeMethodsSet = new HashSet<>();
		if (!StringHelper.isEmpty(includeMethods)) {
			Helper.add(includeMethodsSet, StringHelper.split(includeMethods));
		}
		Set<String> excludeMethodsSet = new HashSet<>();
		if (!StringHelper.isEmpty(excludeMethods)) {
			Helper.add(excludeMethodsSet, StringHelper.split(excludeMethods));
		}

		return include(excludeMethodsSet, includeMethodsSet, method);
	}

}
