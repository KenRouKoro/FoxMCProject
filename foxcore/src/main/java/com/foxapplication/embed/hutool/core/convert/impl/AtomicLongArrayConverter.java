package com.foxapplication.embed.hutool.core.convert.impl;

import com.foxapplication.embed.hutool.core.convert.AbstractConverter;
import com.foxapplication.embed.hutool.core.convert.Convert;

import java.util.concurrent.atomic.AtomicLongArray;

/**
 * {@link AtomicLongArray}转换器
 *
 * @author Looly
 * @since 5.4.5
 */
public class AtomicLongArrayConverter extends AbstractConverter<AtomicLongArray> {
	private static final long serialVersionUID = 1L;

	@Override
	protected AtomicLongArray convertInternal(Object value) {
		return new AtomicLongArray(Convert.convert(long[].class, value));
	}

}
