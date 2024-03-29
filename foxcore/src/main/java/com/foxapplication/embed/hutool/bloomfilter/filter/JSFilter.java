package com.foxapplication.embed.hutool.bloomfilter.filter;

import com.foxapplication.embed.hutool.core.util.HashUtil;

public class JSFilter extends FuncFilter {
	private static final long serialVersionUID = 1L;

	public JSFilter(long maxValue) {
		this(maxValue, DEFAULT_MACHINE_NUM);
	}

	public JSFilter(long maxValue, int machineNum) {
		super(maxValue, machineNum, HashUtil::jsHash);
	}
}
