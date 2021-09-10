package com.cos.blogapp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {

	public static String encrypt(String rawPassword, MyAlgorithm algorithm) {
		// 함수 전체에 예외 처리를 하려면 throw 한다
		// 다른 곳에서 new 를 못하게 할려면 보통 protected를 한다
		// getInstance 는 new 한 것을 재사용하는 것이다

		// 1. SHA256 함수를 가진 클래스 객체 가져오기
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(algorithm.getType());  // SHA-256, SHA-512 // MD5, AES ( 둘다 뚫림)
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 

		// 2. 비밀번호 1234 -> SHA256 던지기
		md.update(rawPassword.getBytes());

		// 암호화된 글자를 16진수로 변환(헥사코드)
		StringBuilder sb = new StringBuilder();
		for (Byte b : md.digest()) {
			sb.append(String.format("%02x", b)); // 16 진수로 바꿈, SHA-256의 프로토콜
		}
		return sb.toString();

	}
}
