package oop

class Car (
    internal val name: String,
    private var owner: String,
    _price: Int
) {
    var price = _price // 기본값은 public 이므로 getter는 public이다.
        private set // price의 setter는 private
}