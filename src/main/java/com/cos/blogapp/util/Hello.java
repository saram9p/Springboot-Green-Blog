package com.cos.blogapp.util;

public class Hello {
   // 싱글톤 패턴 : new를 한번만 하고 재사용한다
   private static Hello instance = new Hello();
   
   public static Hello getInstance() {
      return instance;
   }
   
   private Hello() {}
}