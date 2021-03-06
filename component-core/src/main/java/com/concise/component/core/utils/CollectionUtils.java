package com.concise.component.core.utils;

import java.util.*;

/**
 * The type Collection utils.
 *
 * @author shenguangyang
 */
public class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * Is empty boolean.
     *
     * @param col the col
     * @return the boolean
     */
    public static boolean isEmpty(Collection<?> col) {
        return !isNotEmpty(col);
    }

    /**
     * Is not empty boolean.
     *
     * @param col the col
     * @return the boolean
     */
    public static boolean isNotEmpty(Collection<?> col) {
        return col != null && !col.isEmpty();
    }

    /**
     * Is empty boolean.
     *
     * @param array the array
     * @return the boolean
     */
    public static boolean isEmpty(Object[] array) {
        return !isNotEmpty(array);
    }

    /**
     * Is not empty boolean.
     *
     * @param array the array
     * @return the boolean
     */
    public static boolean isNotEmpty(Object[] array) {
        return array != null && array.length > 0;
    }

    /**
     * To string string.
     *
     * @param col the col
     * @return the string
     */
    public static String toString(Collection<?> col) {
        if (isEmpty(col)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Object obj : col) {
            sb.append(StringUtils.toString(obj));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    /**
     * Is size equals boolean.
     *
     * @param col0 the col 0
     * @param col1 the col 1
     * @return the boolean
     */
    public static boolean isSizeEquals(Collection<?> col0, Collection<?> col1) {
        if (col0 == null) {
            return col1 == null;
        } else {
            if (col1 == null) {
                return false;
            } else {
                return col0.size() == col1.size();
            }
        }
    }

    private static final String KV_SPLIT = "=";

    private static final String PAIR_SPLIT = "&";

    /**
     * Encode map to string
     *
     * @param map origin map
     * @return String string
     */
    public static String encodeMap(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        if (map.isEmpty()) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append(KV_SPLIT).append(entry.getValue()).append(PAIR_SPLIT);
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * Decode string to map
     *
     * @param data data
     * @return map map
     */
    public static Map<String, String> decodeMap(String data) {
        if (data == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(data)) {
            return map;
        }
        String[] kvPairs = data.split(PAIR_SPLIT);
        if (kvPairs.length == 0) {
            return map;
        }
        for (String kvPair : kvPairs) {
            if (StringUtils.isEmpty(kvPair)) {
                continue;
            }
            String[] kvs = kvPair.split(KV_SPLIT);
            if (kvs.length != 2) {
                continue;
            }
            map.put(kvs[0], kvs[1]);
        }
        return map;
    }

    /**
     * To upper list list.
     *
     * @param sourceList the source list
     * @return the list
     */
    public static List<String> toUpperList(List<String> sourceList) {
        if (isEmpty(sourceList)) { return sourceList; }
        List<String> destList = new ArrayList<>(sourceList.size());
        for (String element : sourceList) {
            if (element != null) {
                destList.add(element.toUpperCase());
            } else {
                destList.add(null);
            }
        }
        return destList;
    }
}