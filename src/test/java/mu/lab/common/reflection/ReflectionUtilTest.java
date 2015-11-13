package mu.lab.common.reflection;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * @author guangchen.
 */
public class ReflectionUtilTest {
    public static class A {}
    public static class B extends A {}
    public static class C {
        @SuppressWarnings("unused")
        B b;
    }

    @Test
    public void testIsSubclass() throws Exception {
        Field field = C.class.getDeclaredFields()[0];
        Assert.assertTrue(ReflectionUtil.isSubclass(field, A.class));
    }
}
