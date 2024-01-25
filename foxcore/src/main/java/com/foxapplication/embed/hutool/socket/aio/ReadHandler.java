package com.foxapplication.embed.hutool.socket.aio;

import com.foxapplication.embed.hutool.socket.SocketRuntimeException;

import java.nio.channels.CompletionHandler;

/**
 * 数据读取完成回调，调用Session中相应方法处理消息，单例使用
 * 
 * @author looly
 *
 */
public class ReadHandler implements CompletionHandler<Integer, AioSession> {

	@Override
	public void completed(Integer result, AioSession session) {
		session.callbackRead();
	}

	@Override
	public void failed(Throwable exc, AioSession session) {
		throw new SocketRuntimeException(exc);
	}

}
