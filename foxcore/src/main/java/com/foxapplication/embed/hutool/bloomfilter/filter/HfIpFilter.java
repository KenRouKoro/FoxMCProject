package com.foxapplication.embed.hutool.bloomfilter.filter;

import com.foxapplication.embed.hutool.core.util.HashUtil;

public class HfIpFilter extends FuncFilter {
	private static final long serialVersionUID = 1L;

	public HfIpFilter(long maxValue) {
		this(maxValue, DEFAULT_MACHINE_NUM);
	}

	public HfIpFilter(long maxValue, int machineNum) {
		super(maxValue, machineNum, HashUtil::hfIpHash);
	}
}
