/**
 * 定时任务模块，提供类Crontab表达式的定时任务，实现参考了Cron4j，同时可以支持秒级别的定时任务定义和年的定义（同时兼容Crontab、Cron4j、Quartz表达式）<br>
 * 定时任务模块由三部分组成：
 * <ul>
 *     <li>{@link com.foxapplication.embed.hutool.cron.Scheduler} 定时任务调度器，用于整体管理任务的增删、启停和触发运行。</li>
 *     <li>{@link com.foxapplication.embed.hutool.cron.task.Task} 定时任务实现，用于定义具体的任务</li>
 *     <li>{@link com.foxapplication.embed.hutool.cron.pattern.CronPattern} 定时任务表达式，用于定义任务触发时间</li>
 * </ul>
 *
 * 同时，提供了{@link com.foxapplication.embed.hutool.cron.CronUtil}工具类，维护一个全局的{@link com.foxapplication.embed.hutool.cron.Scheduler}。
 *
 * @author looly
 */
package com.foxapplication.embed.hutool.cron;
