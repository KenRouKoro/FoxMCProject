/**
 * 定时任务表达式匹配器，内部使用<br>
 * 单一表达式使用{@link com.foxapplication.embed.hutool.cron.pattern.matcher.PatternMatcher}表示<br>
 * {@link com.foxapplication.embed.hutool.cron.pattern.matcher.PatternMatcher}由7个{@link com.foxapplication.embed.hutool.cron.pattern.matcher.PartMatcher}组成，
 * 分别表示定时任务表达式中的7个位置:
 * <pre>
 *         0      1     2        3         4       5        6
 *      SECOND MINUTE HOUR DAY_OF_MONTH MONTH DAY_OF_WEEK YEAR
 * </pre>
 *
 * @author looly
 *
 */
package com.foxapplication.embed.hutool.cron.pattern.matcher;
