package oop

// 유틸 메소드. 자바에서 정적 메소드가 있는 것처럼 사용 가능
fun isDirectoryPath(path: String): Boolean {
    return path.endsWith("/")
}