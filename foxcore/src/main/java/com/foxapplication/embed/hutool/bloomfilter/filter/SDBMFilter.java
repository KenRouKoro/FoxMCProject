package com.foxapplication.embed.hutool.bloomfilter.filter;

import com.foxapplication.embed.hutool.core.util.HashUtil;

public class SDBMFilter extends FuncFilter {
	private static final long serialVersionUID = 1L;

	public SDBMFilter(long maxValue) {
		this(maxValue, DEFAULT_MACHINE_NUM);
	}

	public SDBMFilter(long maxValue, int machineNum) {
		super(maxValue, machineNum, HashUtil::sdbmHash);
	}
}
