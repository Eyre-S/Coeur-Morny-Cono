package cc.sukazyo.cono.morny.util;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 这个注解表示当前字段是由 gradle 任务 {@code generateBuildConfig} 自动生成的.
 * @since 1.0.0-alpha4
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface BuildConfigField {}
