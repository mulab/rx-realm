package mu.lab.common.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guangchen.
 */
public class ReflectionUtil {
    public static List<Field> getPrivateFields(Class clazz) {
        List<Field> privateFields = new ArrayList<>();
        Field[] allFields = clazz.getDeclaredFields();
        for (Field field: allFields) {
            if (Modifier.isPrivate(field.getModifiers())) {
                privateFields.add(field);
            }
        }
        return privateFields;
    }

    public static boolean isSubclass(Field field, Class<?> aClass) {
        return aClass.isAssignableFrom(field.getType());
    }
}
