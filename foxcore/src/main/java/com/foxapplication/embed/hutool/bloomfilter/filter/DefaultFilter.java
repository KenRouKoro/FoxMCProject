package com.foxapplication.embed.hutool.bloomfilter.filter;

import com.foxapplication.embed.hutool.core.util.HashUtil;

/**
 * 默认Bloom过滤器，使用Java自带的Hash算法
 *
 * @author loolly
 */
public class DefaultFilter extends FuncFilter {
	private static final long serialVersionUID = 1L;

	public DefaultFilter(long maxValue) {
		this(maxValue, DEFAULT_MACHINE_NUM);
	}

	public DefaultFilter(long maxValue, int machineNumber) {
		super(maxValue, machineNumber, HashUtil::javaDefaultHash);
	}
}
