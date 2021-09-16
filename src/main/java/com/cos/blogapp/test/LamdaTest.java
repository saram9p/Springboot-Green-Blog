package com.cos.blogapp.test;

// 1.8 람다식, (옵셔널 도 추가 되었다)
// 1. 함수를 넘기는 게 목적
// 2. 인터페이스에 함수가 무조건 하나여야 함
// 3. 쓰면 코드 간결해지고, 타입을 몰라도 됨

interface MySupplier {
	void get();
}

public class LamdaTest {
	
	static void start(MySupplier s) { // System.out.println("get함수 호출됨");}
		s.get();
	}
	
	public static void main(String[] args) { // 자바는 1급 객체가 아니다, 함수 자체를 넘기지 못한다
		
		
		// 람다식은 스택만 넘길 수 있다, 함수가 2개 이상이면 안된다
		start(() -> {System.out.println("get함수 호출됨");});
	}
}
