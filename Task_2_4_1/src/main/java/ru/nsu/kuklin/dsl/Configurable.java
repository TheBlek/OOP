package ru.nsu.kuklin.dsl;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MetaProperty;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class Configurable extends GroovyObjectSupport {
    public void methodMissing(String name, Object args) {
        System.out.println("Call to " + getClass() + " with name = " + name);
        MetaProperty metaProperty = getMetaClass().getMetaProperty(name);
        if (metaProperty != null) {
            var arg = ((Object []) args)[0];
            try {
                if (arg instanceof Closure closure) {
                    Object value = getProperty(name) == null ?
                        metaProperty.getType().getConstructor().newInstance() :
                        getProperty(name);
                    objectFromClosure(closure, value);
                    setProperty(name, value);
                } else if (arg instanceof Collection<?> genericCollection) {
                    ParameterizedType collectionType = (ParameterizedType) getClass().getDeclaredField(name).getGenericType();
                    Type itemType = collectionType.getActualTypeArguments()[0];
                    if (!(itemType instanceof Class itemClass)) {
                        System.out.println("Collection type is not concrete, this does not work, sorry");
                        setProperty(name, null);
                        return;
                    }
                    /* If the type is complex - initialize it recursively */
                    if (Configurable.class.isAssignableFrom(itemClass)) {
                        var newValue = (Collection) (arg.getClass().getDeclaredConstructor().newInstance());
                        for (var i : genericCollection) {
                            if (i instanceof Closure closure) {
                                var item = itemClass.getDeclaredConstructor().newInstance();
                                objectFromClosure(closure, item);
                                newValue.add(item);
                            } else if (itemClass.isAssignableFrom(i.getClass())) {
                                newValue.add(i);
                            } else {
                                System.out.println("Item type cannot be converted to collection's type, sorry");
                                setProperty(name, null);
                                return;
                            }
                        }
                        setProperty(name, newValue);
                    } else {
                        setProperty(name, arg);
                    }
                } else {
                    setProperty(name, arg);
                }
            } catch (Exception e) {
                System.out.println("Failed to set property: " + e);
            }
        } else {
            throw new IllegalArgumentException("No such field: " + name);
        }
    }

    private void objectFromClosure(Closure closure, Object v) {
        closure.setDelegate(v);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
    }
}
