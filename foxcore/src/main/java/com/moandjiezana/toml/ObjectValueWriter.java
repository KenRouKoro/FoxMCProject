package com.moandjiezana.toml;

import cn.korostudio.ctoml.Location;
import cn.korostudio.ctoml.OutputAnnotation;
import cn.korostudio.ctoml.OutputAnnotationData;
import com.foxapplication.mc.core.config.interfaces.FieldAnnotation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.moandjiezana.toml.MapValueWriter.MAP_VALUE_WRITER;

/**
 * 对象类型的值写入器
 */
class ObjectValueWriter implements ValueWriter {
  static final ValueWriter OBJECT_VALUE_WRITER = new ObjectValueWriter();

  @Override
  public boolean canWrite(Object value) {
    return true;
  }

  @Override
  public void write(Object value, WriterContext context) {
    Map<String, Object> to = new LinkedHashMap<String, Object>();
    Set<Field> fields = getFields(value.getClass());
    for (Field field : fields) {

      OutputAnnotation annotation = field.getAnnotation(OutputAnnotation.class);
      FieldAnnotation fieldAnnotation = field.getAnnotation(FieldAnnotation.class);
      if((annotation != null)&&(annotation.at() != null)&&(annotation.value() != null)){
        OutputAnnotationData data = new OutputAnnotationData(annotation.value(),annotation.at(),getFieldValue(field, value));
        to.put(field.getName(), data);
      }else if((fieldAnnotation!=null)&&(fieldAnnotation.value()!=null)){
        OutputAnnotationData data = new OutputAnnotationData(fieldAnnotation.value(), Location.Top,getFieldValue(field, value));
        to.put(field.getName(), data);
      }
      else {
        to.put(field.getName(), getFieldValue(field, value));
      }
    }

    MAP_VALUE_WRITER.write(to, context);
  }

  @Override
  public boolean isPrimitiveType() {
    return false;
  }

  /**
   * 获取类及其父类的所有字段
   * @param cls 类
   * @return 字段集合
   */
  private static Set<Field> getFields(Class<?> cls) {
    Set<Field> fields = new LinkedHashSet<Field>(Arrays.asList(cls.getDeclaredFields()));
    while (cls != Object.class) {
      fields.addAll(Arrays.asList(cls.getDeclaredFields()));
      cls = cls.getSuperclass();
    }
    removeConstantsAndSyntheticFields(fields);

    return fields;
  }

  /**
   * 移除常量和合成字段
   * @param fields 字段集合
   */
  private static void removeConstantsAndSyntheticFields(Set<Field> fields) {
    Iterator<Field> iterator = fields.iterator();
    while (iterator.hasNext()) {
      Field field = iterator.next();
      if ((Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) || field.isSynthetic() || Modifier.isTransient(field.getModifiers())) {
        iterator.remove();
      }
    }
  }

  /**
   * 获取字段的值
   * @param field 字段
   * @param o 对象
   * @return 字段的值
   */
  private static Object getFieldValue(Field field, Object o) {
    boolean isAccessible = field.isAccessible();
    field.setAccessible(true);
    Object value = null;
    try {
      value = field.get(o);
    } catch (IllegalAccessException ignored) {
    }
    field.setAccessible(isAccessible);

    return value;
  }

  private ObjectValueWriter() {}
}
