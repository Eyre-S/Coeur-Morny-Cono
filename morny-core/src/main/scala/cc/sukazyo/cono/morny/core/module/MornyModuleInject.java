package cc.sukazyo.cono.morny.core.module;

import cc.sukazyo.cono.morny.core.module.loader.AnnotatedModulesLoader;

import java.lang.annotation.*;

/**
 * Indicates this class is a {@link MornyModule} and should be autoloaded.
 * <p>
 * The tagged module will be loaded by {@link AnnotatedModulesLoader} in Morny's default
 * launcher.
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MornyModuleInject {}
