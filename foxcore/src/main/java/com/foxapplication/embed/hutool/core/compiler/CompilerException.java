package com.foxapplication.embed.hutool.core.compiler;

import com.foxapplication.embed.hutool.core.exceptions.ExceptionUtil;
import com.foxapplication.embed.hutool.core.util.StrUtil;

/**
 * 编译异常
 *
 * @author looly
 * @since 5.5.2
 */
public class CompilerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CompilerException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public CompilerException(String message) {
		super(message);
	}

	public CompilerException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public CompilerException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public CompilerException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
