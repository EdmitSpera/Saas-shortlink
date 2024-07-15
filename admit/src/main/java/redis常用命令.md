### string
#### 设置值
```java
void set(K key, V value);

void set(K key, V value, long timeout, TimeUnit unit);

@Nullable
Boolean setIfAbsent(K key, V value);

@Nullable
Boolean setIfAbsent(K key, V value, long timeout, TimeUnit unit);

@Nullable
Boolean setIfPresent(K key, V value);

@Nullable
Boolean setIfPresent(K key, V value, long timeout, TimeUnit unit);

```

#### 获得值
```java
@Nullable
    V get(Object key);

    @Nullable
    V getAndDelete(K key);

    @Nullable
    V getAndExpire(K key, long timeout, TimeUnit unit);

    @Nullable
    V getAndExpire(K key, Duration timeout);

    @Nullable
    V getAndPersist(K key);

    @Nullable
    V getAndSet(K key, V value);

```
#### 其他
```java
@Nullable
    Long increment(K key);

    @Nullable
    Long increment(K key, long delta);

    @Nullable
    Double increment(K key, double delta);

    @Nullable
    Long decrement(K key);

    @Nullable
    Long decrement(K key, long delta);

    @Nullable
    Integer append(K key, String value);

    @Nullable
    Long size(K key);

    

```

### Hash
#### 增加键值
```java
void putAll(H key, Map<? extends HK, ? extends HV> m);

void put(H key, HK hashKey, HV value);

Boolean putIfAbsent(H key, HK hashKey, HV value);
```
#### 查找
```java
@Nullable
HV get(H key, Object hashKey);

Set<HK> keys(H key);

List<HV> values(H key);

Map<HK, HV> entries(H key);

Boolean hasKey(H key, Object hashKey);

```