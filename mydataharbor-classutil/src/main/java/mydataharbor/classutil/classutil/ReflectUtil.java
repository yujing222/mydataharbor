package mydataharbor.classutil.classutil;

import mydataharbor.classutil.exception.ReflectException;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 获取接口类型的泛型
 *
 * @auth xulang
 * @Date 2021/5/10
 **/
public class ReflectUtil {

  public static Type getTypeByIndex(Class clazz, int index, String flag, Class targetClazz) {
    List<Class<?>> allSuperClass = new ArrayList<>();
    List<Type> allGenericInterfaces = new ArrayList<>();
    List<Type> allGenericClazz = new ArrayList<>();
    getAllClazz(clazz, allSuperClass, allGenericInterfaces, allGenericClazz);
    for (Type genericInterface : allGenericInterfaces) {
      if (genericInterface.getTypeName().startsWith(targetClazz.getName())) {
        return findType(allGenericInterfaces, allGenericClazz, (ParameterizedType) genericInterface, index, flag);
      }
    }
    throw new ReflectException("不应该执行到这里，请检查泛型配置有问题否！");
  }

  private static Type findType(List<Type> allGenericInterfaces, List<Type> allGenericClazz, ParameterizedType genericInterface, int index, String flag) {
    Type actualTypeArgument = ((ParameterizedType) genericInterface).getActualTypeArguments()[index];
    if (actualTypeArgument.getTypeName().equals(flag)) {
      TypeVariable typeVariable = (TypeVariable) actualTypeArgument;
      GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
      //查找
      for (Type genericClazz : allGenericClazz) {
        if (genericClazz != null && genericClazz.getTypeName().startsWith(((Type) genericDeclaration).getTypeName())) {
          return findType(allGenericInterfaces, allGenericClazz, (ParameterizedType) genericClazz, index, flag);
        }
      }
      for (Type allGenericInterface : allGenericInterfaces) {
        if (allGenericInterface != null && allGenericInterface.getTypeName().startsWith(((Type) genericDeclaration).getTypeName())) {
          return findType(allGenericInterfaces, allGenericClazz, (ParameterizedType) allGenericInterface, index, flag);
        }
      }
      return genericInterface.getActualTypeArguments()[index];
    } else {
      return actualTypeArgument;
    }
  }

  public static void getAllClazz(Class<?> clazz, List<Class<?>> superClazzList, List<Type> genericInterfacesList, List<Type> genericClassList) {
    if (clazz == null) {
      return;
    }
    if (!superClazzList.contains(clazz)) {
      superClazzList.add(clazz);
      genericInterfacesList.addAll(Arrays.asList(clazz.getGenericInterfaces()));
      genericClassList.addAll(Arrays.asList(clazz.getGenericSuperclass()));
      Class<?> superclass = clazz.getSuperclass();
      getAllClazz(superclass, superClazzList, genericInterfacesList, genericClassList);
      Class<?>[] interfaces = clazz.getInterfaces();
      for (Class<?> interfaceCls : interfaces) {
        getAllClazz(interfaceCls, superClazzList, genericInterfacesList, genericClassList);
      }
    }
  }
}
