package com.foxapplication.embed.hutool.core.comparator;

import com.foxapplication.embed.hutool.core.lang.Assert;
import com.foxapplication.embed.hutool.core.util.ClassUtil;

import java.lang.reflect.Field;

/**
 * Bean字段排序器<br>
 * 参阅feilong-core中的PropertyComparator
 *
 * @param <T> 被比较的Bean
 * @author Looly
 */
public class FieldsComparator<T> extends NullComparator<T> {
	private static final long serialVersionUID = 8649196282886500803L;

	/**
	 * 构造
	 *
	 * @param beanClass  Bean类
	 * @param fieldNames 多个字段名
	 */
	public FieldsComparator(Class<T> beanClass, String... fieldNames) {
		this(true, beanClass, fieldNames);
	}

	/**
	 * 构造
	 *
	 * @param nullGreater 是否{@code null}在后
	 * @param beanClass   Bean类
	 * @param fieldNames  多个字段名
	 */
	public FieldsComparator(boolean nullGreater, Class<T> beanClass, String... fieldNames) {
		super(nullGreater, (a, b) -> {
			Field field;
			for (String fieldName : fieldNames) {
				field = ClassUtil.getDeclaredField(beanClass, fieldName);
				Assert.notNull(field, "Field [{}] not found in Class [{}]", fieldName, beanClass.getName());
				// issue#3259，多个字段比较时，允许字段值重复
				final int compare = new FieldComparator<>(true, false, field).compare(a, b);
				if (0 != compare) {
					return compare;
				}
			}
			return 0;
		});
	}

}
