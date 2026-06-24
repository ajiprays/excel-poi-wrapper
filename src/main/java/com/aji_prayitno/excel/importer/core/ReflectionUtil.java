package com.aji_prayitno.excel.importer.core;


import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

import com.aji_prayitno.excel.importer.step.DataSetter;

public final class ReflectionUtil {

	private ReflectionUtil() {}
    
    @SuppressWarnings("unchecked")
	public static <T, V> Class<V> resolve(Class<T> dtoClass, DataSetter<T, V> dataSetter){
    	try {
	    	Method writeReplace = dataSetter.getClass().getDeclaredMethod("writeReplace");
	        writeReplace.setAccessible(true);
	        SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(dataSetter);
	        
	        String implClassName = serializedLambda.getImplClass().replace('/', '.');
	        String implMethodName = serializedLambda.getImplMethodName();
	        
	        if (implClassName.equals(dtoClass.getName()) && !implMethodName.startsWith("lambda$")) {
	        	Class<?> implClass = Class.forName(implClassName);
	        	for (Method method : implClass.getDeclaredMethods()) {
	    	        if (method.getName().equals(implMethodName)) {
	    	            return (Class<V>) method.getParameterTypes()[0];
	    	        }
	    	    }
            }
	        
	    	return null;
    	}catch (Exception e) {
    		return null;
		}
    }
}
