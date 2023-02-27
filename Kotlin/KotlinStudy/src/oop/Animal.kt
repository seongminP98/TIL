package oop

abstract class Animal(
    protected val species: String,
    protected open val legCount: Int // 프로퍼티를 오버라이딩 하려면 open 키워드 필요 (Penguin 에서 legCount getter 오버라이딩)
) {
    abstract fun move()
}