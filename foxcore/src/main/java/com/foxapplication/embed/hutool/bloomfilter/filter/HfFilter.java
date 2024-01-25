package com.foxapplication.embed.hutool.bloomfilter.filter;


import com.foxapplication.embed.hutool.core.util.HashUtil;

public class HfFilter extends FuncFilter {
	private static final long serialVersionUID = 1L;

	public HfFilter(long maxValue) {
		this(maxValue, DEFAULT_MACHINE_NUM);
	}

	public HfFilter(long maxValue, int machineNum) {
		super(maxValue, machineNum, HashUtil::hfHash);
	}
}
