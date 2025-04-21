package com.los.leagueofstats.utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.TreeMultimap;

import org.springframework.util.MultiValueMap;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.http.util.TextUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

public class CollectionUtilities {


    /**
     * Преобразовывает Map в ArrayListMultimap
     *
     * @param <K> тип ключа
     * @param <V> тип значения
     * @param map Map
     *
     * @return ArrayListMultimap
     */
    public static <K extends Object, V extends Object> ArrayListMultimap<K, V> toArrayListMultimap(Map<K, List<V>> map) {
        checkArgument(map != null, "Map not sprcified");
        ArrayListMultimap<K, V> multimap = ArrayListMultimap.create();

        for (Map.Entry<K, List<V>> entry : map.entrySet()) {
            multimap.putAll(entry.getKey(), entry.getValue());
        }
        return multimap;
    }

    /**
     * Преобразовывает Map в TreeMultimap
     *
     * @param <K>             тип ключа
     * @param <V>             тип значения
     * @param map             Map
     * @param keyComparator   компаратор для ключа,
     *                        чтобы указать естественный порядок - использовать Ordering.natural()
     * @param valueComparator компаратор для значения,
     *                        чтобы указать естественный порядок - использовать Ordering.natural()
     *
     * @return TreeMultimap
     */
    public static <K extends Object, V extends Object> TreeMultimap<K, V> toTreeMultimap(
            Map<K, List<V>> map,
            Comparator<? super K> keyComparator,
            Comparator<? super V> valueComparator) {
        checkArgument(map != null, "Map not sprcified");
        checkArgument(keyComparator != null, "Key comparator not sprcified");
        checkArgument(valueComparator != null, "Value comparator not sprcified");

        TreeMultimap<K, V> multimap = TreeMultimap.create(keyComparator, valueComparator);

        for (Map.Entry<K, List<V>> entry : map.entrySet()) {
            multimap.putAll(entry.getKey(), entry.getValue());
        }
        return multimap;
    }

    /**
     * Преобразовывает Map в TreeMultimap
     *
     * @param <K> тип ключа
     * @param <V> тип значения
     * @param map Map
     *
     * @return TreeMultimap
     */
    public static <K extends Comparable, V extends Comparable> TreeMultimap<K, V> toTreeMultimap(
            Map<K, List<V>> map) {
        checkArgument(map != null, "Map not sprcified");
        TreeMultimap<K, V> multimap = TreeMultimap.create();

        for (K key : map.keySet()) {
            multimap.putAll(key, map.get(key));
        }
        return multimap;
    }

    /**
     * Сортирует списки значений в ListMultimap.
     * не сортирует ключи
     *
     * @param <K>      тип ключа
     * @param <V>      тип значения
     * @param multimap ListMultimap
     * @param cmp      компаратор
     */
    public static <K, V> void sortListMultimapValues(ListMultimap<K, V> multimap,
                                                     Comparator<V> cmp) {
        checkArgument(multimap != null, "Multimap not sprcified");
        checkArgument(cmp != null, "Comparator not sprcified");
        if (multimap.isEmpty()) {
            return;
        }

        for (K key : new ArrayList<>(multimap.keySet())) {          // проходим по ключам мультикарты
            List<V> valueList = new ArrayList<>(multimap.get(key));
            Collections.sort(valueList, cmp);                       // сортируем список значений
            multimap.replaceValues(key, valueList);                 // пересохраняем в multimap сортированный список
        }
    }

    /**
     * Проверка Map на пустоту
     *
     * @param map Map
     *
     * @return true если карта пустая или null, иначе false
     */
    public static boolean isMapEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    /**
     * Добавляет значение в список, если значение не null
     *
     * @param <T>   тип значения в списке
     * @param list  спсиок
     * @param value значение для добавления
     */
    public static <T> void addIfNotNull(List<T> list,
                                        T value) {
        checkArgument(list != null, "List not specified");
        if (value != null) {
            list.add(value);
        }
    }

    /**
     * Добавляет значение в карту, если значение не null
     *
     * @param <K>   тип ключа карты
     * @param <V>   тип значения карты
     * @param map   карта
     * @param key   ключ для добавления
     * @param value значение для добавления
     */
    public static <K, V> void putIfNotNull(Map<K, V> map,
                                           K key,
                                           V value) {
        checkArgument(map != null, "Map not specified");
        if (value != null) {
            map.put(key, value);
        }
    }

    /**
     * Добавляет значение в MultiValueMap, если значение не null
     *
     * @param <K>   тип значения карты
     * @param map   карта
     * @param key   ключ для добавления
     * @param value значение для добавления
     */
    public static <K> void putIfNotNull(
            MultiValueMap<K, String> map,
            K key,
            Object value) {
        checkArgument(map != null, "Map not specified");
        if (value != null) {
            map.add(key, value.toString());
        }
    }

    /**
     * Добавляет строковое значение в карту, если значение не blank
     *
     * @param <K>   тип ключа карты
     * @param map   карта
     * @param key   ключ для добавления
     * @param value значение для добавления
     */
    public static <K> void putIfNotBlank(
            Map<K, String> map,
            K key,
            String value) {
        checkArgument(map != null, "Map not specified");
        if (!isBlank(value)) {
            map.put(key, value);
        }
    }

    /**
     * Добавляет строковое значение в MultiValueMap, если значение не blank
     *
     * @param <K>   тип ключа карты
     * @param map   MultiValueMap
     * @param key   ключ для добавления
     * @param value значение для добавления
     */
    public static <K> void putIfNotBlank(
            MultiValueMap<K, String> map,
            K key,
            String value) {
        checkArgument(map != null, "Map not specified");
        if (!isBlank(value)) {
            map.add(key, value);
        }
    }

    /**
     * Преобразовывает список в массив
     *
     * @param list исходный список
     *
     * @return массив
     */
    public static Object[] listToArray(List list) {
        if (list == null) {                 // если список null - вернем null
            return null;
        }
        if (list.isEmpty()) {               // если список пуст - возвращаем пустой массив
            return new Object[0];
        }

        return list.toArray(new Object[list.size()]);
    }

    /**
     * Фильтрует null значения из списка.
     *
     * @param <T>  тип значения в списке
     * @param list исходный список
     *
     * @return отфильрованный список или null если исходный спсиок null
     */
    public static <T> List<T> filterNullValues(List<T> list) {
        if (list == null) {
            return null;
        }
        if (list.isEmpty()) {
            return new ArrayList<>(0);
        }

        return list.stream()
                .filter(value -> value != null)
                .collect(Collectors.toList());
    }

    /**
     * Формирует список уникальных значений
     *
     * @param <T>  тип значения в списке
     * @param list исходный список
     *
     * @return список с уникальными значениями
     */
    @Nullable
    public static <T> List<T> distinct(@Nullable List<T> list) {
        if (list == null) {
            return null;
        }
        if (list.isEmpty()) {
            return new ArrayList<>(0);
        }
        return list.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     *
     * @param <T>
     * @param <K>
     * @param list
     * @param classifier
     * @return
     */
    @Nullable
    public static <T, K> List<T> distinctBy(
            @Nullable List<T> list,
            Function<? super T, ? extends K> classifier) {
        checkArgument(classifier != null, "Classifier is not specified");

        if (list == null) {
            return null;
        }
        if (list.isEmpty()) {
            return new ArrayList<>(0);
        }

        Collection<T> distinctValues = list.stream()
                .collect(Collectors.toMap(classifier, Function.identity(), CollectionUtilities.oneValueMerger()))
                .values();
        return new ArrayList<>(distinctValues);
    }

    /**
     * Преобразовывает коллекцию в steam.
     * Если исходная коллекция пустая - вернет пустой Stream
     * @param <T> тип элементов коллекции
     * @param collection исходная коллекция
     * @return Stream
     */
    public static <T> Stream<T> asStream(@Nullable Collection<? extends T> collection) {
        return (Stream<T>) Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }

    /**
     * Add element if list doesn`t contain any not null element
     *  with type of adding element
     *
     * @param <L> type of list
     * @param <T> type of adding element
     * @param list list
     * @param value element to add
     */
    public static <L, T extends L> void addIfNotExistsByType(List<L> list, T value) {
        checkArgument(list != null, "List is not specified");
        checkArgument(value != null, "Value is not specified");
        Class valueClass = value.getClass();

        boolean found = list.stream()
                .filter(Objects::nonNull)
                .anyMatch(el -> valueClass.equals(el.getClass()));

        if (!found) {
            list.add(value);
        }
    }

    /**
     * Move elements of specified type to end of list
     *
     * @param <T>  type
     * @param list list
     * @param type type
     */
    public static <T> void moveToEndOfListByType(List<T> list, Class type) {
        checkArgument(list != null, "List is not specified");
        checkArgument(type != null, "Type is not specified");

        // found elements to move
        List<T> filteredConverters = list.stream()
                .filter(element -> type.isInstance(element))
                .collect(Collectors.toList());
        // move elements to end
        list.removeAll(filteredConverters);
        list.addAll(filteredConverters);
    }

    /**
     * Возвращает null, если коллекция пустая
     * @param <T> тип элементов коллекции
     * @param collection исходная коллекция
     * @return исходная коллекция, или null если исходная коллекция пустая
     */
    @Nullable
    public static <T extends Collection> T emptyToNull(@Nullable T collection) {
        if (isEmpty(collection)) {
            return null;
        }
        return collection;
    }

    /**
     * Returns list containing specified value,
     *  if value is null - empty list will be returned.
     *
     * @param <T>   type
     * @param value value
     * @return list
     */
    public static <T> List<T> singletonListOrEmptyIfNull(T value) {
        if (value != null) {
            return Collections.singletonList(value);
        }
        return Collections.emptyList();
    }

    /**
     * Returns a merge function, that returns one value from two input values.
     * Second input value will be ignored.
     * This can be used to collect same elements.
     *
     * @param <T>
     * @return
     */
    public static <T> BinaryOperator<T> oneValueMerger() {
        return (a, b) -> a;
    }

}
