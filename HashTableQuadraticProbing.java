//open addressing method to avoid has collisions
//load factor = (items in table/size of table)
//H(k) -> hashing function
//P(x, k) -> probing function
import java.util.*;

import sun.security.krb5.internal.ktab.KeyTabEntry;


public class HashTableQuadraticProbing <K, V> implements Iterable <K>{

    private double loadFactor;
    private int capacity, threshold, modificationCount = 0;

    private int usedBuckets = 0;//total number of used buckets inside hash-table(including deleted(tombstone))
    private int keyCount = 0;//number of unique keys

    private K [] keyTable;
    private V [] valueTable;

    private boolean containsFlag;//flag used for get method

    //marker used to indicate deletion of key-value pair
    private final K TOMBSTONE = (K) (new Object());

    private static final int DEFAULT_CAPACITY = 8;
    private static final double DEFAULT_LOAD_FACTOR = 0.45;

    public HashTableQuadraticProbing(){
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public HashTableQuadraticProbing(int capacity){
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    public HashTableQuadraticProbing(int capacity, double loadFactor){
        if(capacity <= 0) throw new IllegalArgumentException("Illegal capacity: " + capacity);
        if(loadFactor <= 0 || Double.isNaN(loadFactor) || Double.isInfinite(loadFactor)){
            throw new IllegalArgumentException("Illegal LoadFactor: " + loadFactor);
        }
        this.loadFactor = loadFactor;
        this.capacity = Math.max(DEFAULT_CAPACITY, next2Power(capacity));
        threshold = (int) (this.capacity * loadFactor);

        keyTable = (K[]) new Object[this.capacity];
        valueTable = (V[]) new Object[this.capacity];


    }

    private static int next2Power(int n){
        return Integer.highestOneBit(n) << 1;
    }

    private static int P(int x){
        return (x*x + x) >> 1;
    }

    private int normalizeIndex(int keyHash){
        return(keyHash & 0x7FFFFFFF) % capacity;
    }

    public void clear(){
        for(int i = 0; i < capacity; i++){
            keyTable[i] = null;
            valueTable[i] = null;
        }

        keyCount = usedBuckets = 0;
        modificationCount++;
    }

    public int size(){return keyCount;}

    public boolean isEmpty(){return keyCount == 0;}

    public V put(K key, V value){return insert(key, value);}
    public V add(K key, V value){return insert(key, value);}

    public V insert(K key, V val){
        if(key == null) throw new IllegalArgumentException("Null key");
        if(usedBuckets >= threshold) resizeTable();

        final int hash = normalizeIndex(key.hashCode());
        int i = hash, j = -1, x = 1;
        //j -> first tombstone


        do{
            if(keyTable[i] == TOMBSTONE){
                if(j == -1) j = i;
            }else if(keyTable[i] != null){
                if(keyTable[i].equals(key)){
                    V oldValue = valueTable[i];
                    if(j == -1){
                        valueTable[i] = val;
                    }else{
                        keyTable[i] = TOMBSTONE;
                        valueTable[i] = null;
                        keyTable[j] = key;
                        valueTable[j] = val;
                    }

                    modificationCount++;
                    return oldValue;
                }
            }else{//current cell is none
                if(j == -1){
                    usedBuckets++;keyCount++;
                    keyTable[i] = key;
                    valueTable[i] = val;
                }else{
                    keyCount++;
                    keyTable[j] = key;
                    valueTable[j] = val;
                }

                modificationCount++;
                return null;
            }

            i = normalizeIndex(hash + P(x++));
        }while(true);
    }

    public boolean containsKey(K key){
        return hasKey(key);
    }

    public boolean hasKey(K key){
        get(key);
        return containsFlag;
    }

    public V get(K key){
        if(key == null) throw new IllegalArgumentException("Null key");
     
        final int hash = normalizeIndex(key.hashCode());
        int i = hash, j = -1, x = 1;

        do{
            if(keyTable[i] == TOMBSTONE){
                if(j == -1) j = i;
            }else if(keyTable[i] != null){
                if(keyTable[i].equals(key)){
                    containsFlag = true;

                    if(j != -1){
                        keyTable[j] = keyTable[i];
                        valueTable[j] = valueTable[i];

                        keyTable[i] = TOMBSTONE;
                        valueTable[i] = null;

                        return valueTable[j];
                    }else{
                        return valueTable[i];
                    }
                
                }

                
            }else{
                containsFlag = false;
                return null;}

            i = normalizeIndex(hash + P(x++));
        }while(true);
    }

    public V remove(K key){
        
    }


}