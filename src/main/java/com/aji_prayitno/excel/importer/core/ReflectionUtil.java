package com.aji_prayitno.excel.importer.core;


import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public final class ReflectionUtil {

	private ReflectionUtil() {}
	
	private static Method resolveMethod(SerializedLambda lambda) throws ClassNotFoundException, NoSuchMethodException {
	    Class<?> implClass = Class.forName(lambda.getImplClass().replace('/', '.'));
	    String methodName = lambda.getImplMethodName();
	    for (Method method : implClass.getDeclaredMethods()) {
	        if (method.getName().equals(methodName)) {
	            return method;
	        }
	    }
	    throw new NoSuchMethodException(methodName);
	}
	
    public static Class<?> resolveParameterType(Object lambda) {
        try {
            Method writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(lambda);
            Method method = resolveMethod(serializedLambda);
            return method.getParameterTypes()[0];
        } catch (Exception e) {
            return String.class;
        }
    }
}
