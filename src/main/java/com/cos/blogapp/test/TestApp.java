package com.cos.blogapp.test;

// class 만 메모리에 뜰 수 있다
// abstract class (x), interface (x)

interface Callback{
	void hello(); // 추상메서드 = 몸체X => 스택X => 구체적인 행위가 없다.
}

// A a = new A();
// Callback a = new A();
// 함수 구현이 목적
// 한 번 쓰고 버려서 안씀
//class A implements Callback {
//
//	@Override
//	public void hello() {
//		System.out.println("Hello");
//	}
//	
//}

//자바에서 메서드는 1급객체가 아니다
// 메서드를 전달하고 싶으면 인터페이스를 쓴다
public class TestApp {
	void speak(Callback c) { // 무조건 Callback 타입을 넣어야 한다
		c.hello();
	}

//	Callback c = new Callback() {
//
//		@Override
//		public void hello() {
//			// TODO Auto-generated method stub
//			
//		}
//		
//	};

	void bye(Bye b) {
		
	}
	
	interface Bye {
		void start();
	}

	class H implements Bye {

		@Override
		public void start() {
			System.out.println("페이지 이동"); // 동적 바인딩, 부모의 메서드를 무효화
		}

	}

	public static void main(String[] args) {
		TestApp t = new TestApp();
		t.speak(new Callback() { // 익명 함수, 함수만 구현해 주면 메모리에 뜬다

			@Override
			public void hello() {
				System.out.println("hello");
			}
		});

		t.bye(null);

	}
}
