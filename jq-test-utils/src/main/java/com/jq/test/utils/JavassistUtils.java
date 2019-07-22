package com.jq.test.utils;

import com.jq.test.task.ITestClass;
import com.jq.test.task.ITestMethod;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class JavassistUtils {

    /**
     * 添加ITestMethod字段set方法
     *
     * @param ct        类型
     * @param fieldName 字段名称
     * @throws CannotCompileException 异常
     */
    public static void setTestMethodSetter(CtClass ct, String fieldName) throws CannotCompileException {
        CtMethod setter = CtNewMethod.make(
                "public void set" + fieldName +
                        "(ITestMethod testMethod){this." + fieldName + " =testMethod;}", ct);
        ct.addMethod(setter);
    }

    /**
     * 添加注解
     *
     * @param constPool cons池
     * @param doing     所需添加注解的方法
     * @param type      所需添加的组件
     */
    private static void addAnnotations(ConstPool constPool, CtMethod doing, Class type) {
        AnnotationsAttribute methodAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation test = new Annotation(type.getName(), constPool);
        methodAttr.addAnnotation(test);
        doing.getMethodInfo().addAttribute(methodAttr);
    }

    /**
     * 添加ITestMethod字段
     *
     * @param pool       Class池
     * @param ct         类
     * @param methodName 方法名称
     */
    public static void setField(ClassPool pool, CtClass ct, String methodName, Class type) throws CannotCompileException, NotFoundException {
        CtField field = new CtField(pool.get(type.getName()), methodName, ct);
        field.setModifiers(Modifier.PUBLIC);
        ct.addField(field);
    }

    /**
     * 实列化testClass并赋值
     *
     * @param testClass testClass
     * @param ct        对象
     * @return 新的testClass
     */
    public static Object getInstance(ITestClass testClass, CtClass ct) throws CannotCompileException, NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        Class<?> aClass = ct.toClass();
        Constructor<?> classConstructor = aClass.getConstructor();
        Object instance = classConstructor.newInstance();
        for (ITestMethod testMethod : testClass.getTestMethods()) {
            Method method = aClass.getMethod("set" + testMethod.getName(), ITestMethod.class);
            method.invoke(instance, testMethod);
        }
        return instance;
    }

    /**
     * 添加构造方法
     *
     * @param pool      类池
     * @param className class名称
     * @param ct        类
     */
    public static void setConstructor(ClassPool pool, String className, CtClass ct, Class superType) throws CannotCompileException, NotFoundException {
        ct.setSuperclass(pool.get(superType.getName()));
        CtConstructor constructor = CtNewConstructor.make("public " + className + "(com.jq.test.task.ITestClass testClass){super(testClass);}", ct);
        ct.addConstructor(constructor);
    }

    /**
     * 添加测试方法
     *
     * @param ct         类
     * @param constPool  cons池
     * @param methodName 方法名称
     */
    public static void setTest(CtClass ct, ConstPool constPool, String methodName, String body) throws CannotCompileException {
        CtMethod test = CtNewMethod.make(
                "public void " + methodName + "()" + body, ct);
        addAnnotations(constPool, test, Test.class);
        ct.addMethod(test);
    }
}
