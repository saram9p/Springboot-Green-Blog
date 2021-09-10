package com.cos.blogapp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

public class SHATest {
	// 단위테스트의 장점 : 시간 단축
	// 똑같은 패키지와 클래스를 만들어서 @Test를 붙여서 테스트한다
	// JUnit은 main을 쓸수 없다
	// 인자를 넘길 수 없다
	@Test
	public void encrypt() {
		String salt = "코스";
		String rawPassword = "1234!" + salt;  // 소금은 안쳐도 된다
		
		// 1. SHA256 함수를 가진 클래스 객체 가져오기
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256"); // MD5, AES ( 둘다 뚫림)
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		// 2. 비밀번호 1234 -> SHA256 던지기
		md.update(rawPassword.getBytes());
		
//		for(Byte b : rawPassword.getBytes()) {
//			System.out.print(b);
//		}
		
		System.out.println();
		
		StringBuilder sb = new StringBuilder();
		for(Byte b : md.digest()) {
			sb.append(String.format("%02x", b));  // 16 진수로 바꿈, SHA-256의 프로토콜
		}
		System.out.println(sb.toString());
		System.out.println(sb.toString().length());
	}
	
}
