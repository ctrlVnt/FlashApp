package storage.utility

abstract class Storage<T> {
    abstract fun insert(obj: T): Int

    abstract fun size(): Int

    abstract fun find(id: Int): T?

    abstract fun findAll() : List<T>

    abstract fun update(id: Int, obj: T)

    abstract fun delete(id: Int)
}