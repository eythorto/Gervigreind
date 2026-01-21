import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

// AI generated based of pysize.py

public class JavaSize {

    /**
     * Estimates the deep size of an object in bytes.
     * 
     * @param obj The object to measure.
     * @return The estimated size in bytes.
     */
    public static long getSize(Object obj) {
        // IdentityHashMap is critical here. It compares keys using '==' (identity) 
        // rather than '.equals()', which mirrors Python's `id(obj)` behavior.
        Map<Object, Object> visited = new IdentityHashMap<>();
        return getSizeRecursive(obj, visited);
    }

    private static long getSizeRecursive(Object obj, Map<Object, Object> visited) {
        if (obj == null) {
            return 0;
        }

        // 1. Check if already visited (Handling circular references)
        if (visited.containsKey(obj)) {
            return 0;
        }
        visited.put(obj, null);

        Class<?> clazz = obj.getClass();
        long size = 0;

        // 2. Base logic: Arrays
        if (clazz.isArray()) {
            // Header overhead for array (approx 16 bytes)
            size += 16;
            
            int length = Array.getLength(obj);
            if (clazz.getComponentType().isPrimitive()) {
                // Primitive arrays: simply multiply length by type width
                size += length * getPrimitiveSize(clazz.getComponentType());
            } else {
                // Object arrays: Add reference sizes + recursive size of elements
                size += length * getReferenceSize(); 
                for (int i = 0; i < length; i++) {
                    Object element = Array.get(obj, i);
                    if (element != null) {
                        size += getSizeRecursive(element, visited);
                    }
                }
            }
            return size;
        }

        // 3. Base logic: Objects (including Collections, Maps, Strings, and custom classes)
        
        // Header overhead for object (approx 16 bytes)
        size += 16;

        // Traverse hierarchy to get private fields of parent classes too
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // Skip static fields (they belong to class, not instance)
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                // In Python, this was `if hasattr(obj, '__dict__')` logic
                field.setAccessible(true);
                
                try {
                    Class<?> fieldType = field.getType();
                    if (fieldType.isPrimitive()) {
                        size += getPrimitiveSize(fieldType);
                    } else {
                        // It's a reference
                        size += getReferenceSize();
                        Object value = field.get(obj);
                        if (value != null) {
                            size += getSizeRecursive(value, visited);
                        }
                    }
                } catch (IllegalAccessException e) {
                    // Should not happen as we set accessible true
                    e.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass();
        }

        return size;
    }

    // Helper: Size of references (pointers)
    // On 64-bit JVMs with Compressed Oops (default), this is 4. Without, it's 8.
    // We assume standard 64-bit architecture without compressed oops for a "safe" upper bound, 
    // or you can set this to 4.
    private static int getReferenceSize() {
        return 8; 
    }

    // Helper: Size of primitives in bytes
    private static int getPrimitiveSize(Class<?> clazz) {
        if (clazz == boolean.class || clazz == byte.class) return 1;
        if (clazz == char.class || clazz == short.class) return 2;
        if (clazz == int.class || clazz == float.class) return 4;
        if (clazz == long.class || clazz == double.class) return 8;
        return 0; // Should not reach here
    }

    // --- Example Usage ---
    public static void main(String[] args) {
        // Test 1: Simple String
        String s = "Hello World";
        System.out.println("String size: " + getSize(s));

        // Test 2: List of Integers
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) list.add(i);
        System.out.println("ArrayList size: " + getSize(list));

        // Test 3: Circular Reference
        Node a = new Node();
        Node b = new Node();
        a.next = b;
        b.next = a; // Cycle
        System.out.println("Circular Reference size: " + getSize(a));
    }

    static class Node {
        Node next;
        int data = 10;
    }
}
