package telran.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import telran.test.annotation.*;

public class TestRunner implements Runnable {
	private Object testObj;

	public TestRunner(Object testObj) {
		this.testObj = testObj;
	}

	@Override
	public void run() {
		Class<?> clazz = testObj.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		Method[] methodBeforeEach = runMethodBeforeEach(methods);
		runTestMethod(methods, methodBeforeEach);
	}

	private Method[] runMethodBeforeEach(Method[] methods) {
		return Arrays.stream(methods).filter(m -> m.isAnnotationPresent(BeforeEach.class)).toArray(Method[]::new);
	}

	private void runTestMethod(Method[] methods, Method[] methodBeforeEach) {
		for (Method method : methods) {
			if (method.isAnnotationPresent(Test.class)) {
				method.setAccessible(true);
				try {
					runMethods(methodBeforeEach);
					method.invoke(testObj);
				} catch (IllegalAccessException | InvocationTargetException e) {
					System.out.println("error: " + e.getMessage());
				}
			}
		}
	}

	private void runMethods(Method[] methodBeforeEach) {
		for (Method method : methodBeforeEach) {
			method.setAccessible(true);
			try {
				method.invoke(testObj);
			} catch (IllegalAccessException | InvocationTargetException e) {
				System.out.println("error: " + e.getMessage());
			}
		}
	}

}
