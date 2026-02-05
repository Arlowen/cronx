/*
 * Copyright 2008-2009 the original author or authors. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package io.cronx.toolkit.utils;

import java.util.*;
import java.util.function.Function;

/**
 * @version : 2013-4-12
 */
public class CollectionUtils {

    // Empty utilities
    // --------------------------------------------------------------------------
    public static boolean isNotEmpty(Collection<?> tables) {
        return tables != null && !tables.isEmpty();
    }

    public static boolean isEmpty(Collection<?> tables) {
        return tables == null || tables.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> maps) {
        return maps != null && !maps.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> maps) {
        return maps == null || maps.isEmpty();
    }
    // split utilities
    // --------------------------------------------------------------------------

    /**
     * 切分list
     *
     * @param sourceList
     * @param groupSize 每组定长
     */
    public static <T> List<List<T>> splitList(List<T> sourceList, int groupSize) {
        groupSize = Math.max(1, groupSize);
        int length = sourceList.size();
        // 计算可以分成多少组
        long num = (length + (long) groupSize - 1) / groupSize;
        List<List<T>> newList = null;

        if (groupSize > 8192) {
            newList = new LinkedList<>();
        } else {
            newList = new ArrayList<>((int) (groupSize * 0.25));
        }

        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = Math.min((i + 1) * groupSize, length);
            newList.add(sourceList.subList(fromIndex, toIndex));
        }
        return newList;
    }
    // Iterator/Enumeration utilities
    // --------------------------------------------------------------------------

    /** 迭代器类型转换 */
    public static <T, O> Iterator<O> convertIterator(final Iterator<T> oriIterator, final Function<T, O> converter) {
        return new Iterator<O>() {

            @Override
            public void remove() {
                oriIterator.remove();
            }

            @Override
            public O next() {
                return converter.apply(oriIterator.next());
            }

            @Override
            public boolean hasNext() {
                return oriIterator.hasNext();
            }
        };
    }

    /** 转换为 Enumeration */
    public static <T> Enumeration<T> asEnumeration(final Iterator<T> iterator) {
        return new Enumeration<T>() {

            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public T nextElement() {
                return iterator.next();
            }
        };
    }

    /** 合并两个迭代器 */
    public static <T> Enumeration<T> mergeEnumeration(final Enumeration<T> enum1, final Enumeration<T> enum2) {
        final Enumeration<T> i1 = enum1 != null ? enum1 : CollectionUtils.asEnumeration(Collections.emptyIterator());
        final Enumeration<T> i2 = enum2 != null ? enum2 : CollectionUtils.asEnumeration(Collections.emptyIterator());
        return new Enumeration<T>() {

            private Enumeration<T> it = i1;

            @Override
            public boolean hasMoreElements() {
                return i1.hasMoreElements() || i2.hasMoreElements();
            }

            @Override
            public T nextElement() {
                if (!this.it.hasMoreElements()) {
                    this.it = i2;
                }
                return this.it.nextElement();
            }
        };
    }

    /** 合并两个迭代器 */
    public static <T> Iterator<T> mergeIterator(final Iterator<T> iterator1, final Iterator<T> iterator2) {
        final Iterator<T> i1 = iterator1 != null ? iterator1 : Collections.emptyIterator();
        final Iterator<T> i2 = iterator2 != null ? iterator2 : Collections.emptyIterator();
        return new Iterator<T>() {

            private Iterator<T> it = i1;

            @Override
            public boolean hasNext() {
                return i1.hasNext() || i2.hasNext();
            }

            @Override
            public T next() {
                if (!this.it.hasNext()) {
                    this.it = i2;
                }
                return this.it.next();
            }

            @Override
            public void remove() {
                this.it.remove();
            }
        };
    }

    /**
     * Returns <code>true</code> iff at least one element is in both collections.
     * <p>
     * In other words, this method returns <code>true</code> iff the intersection of <i>coll1</i> and <i>coll2</i> is not empty.
     *
     * @param coll1 the first collection, must not be null
     * @param coll2 the first collection, must not be null
     * @return <code>true</code> iff the intersection of the collections is non-empty
     * @since 2.1
     */
    public static boolean containsAny(final Collection<?> coll1, final Collection<?> coll2) {
        if (coll1.size() < coll2.size()) {
            for (Object o : coll1) {
                if (coll2.contains(o)) {
                    return true;
                }
            }
        } else {
            for (Object o : coll2) {
                if (coll1.contains(o)) {
                    return true;
                }
            }
        }
        return false;
    }
}
