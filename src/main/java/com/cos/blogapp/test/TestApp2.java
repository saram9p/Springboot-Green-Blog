package com.cos.blogapp.test;

// 에이 컴퍼니
// 사장 : 야!! 우리 프로그램에 동물들이 특이한 기능!! - 서빙

interface Owner{
   void serving();
}

abstract class Animal {
   abstract void sound();
   abstract void walk();
}

class Dog extends Animal implements Owner{
   void sound() {
      System.out.println("멍멍");
   }
   void walk() {
      System.out.println("달린다");   
   }
   @Override
   public void serving() {
      System.out.println("강아지 서빙");
   }
}
class Cat extends Animal implements Owner{
   void sound() {
      System.out.println("야옹");
   }
   void walk() {
      System.out.println("달린다");   
   }
   @Override
   public void serving() {
      System.out.println("야옹이 서빙");
   }
}

class Bird extends Animal implements Owner{
   @Override
   void sound() {
      System.out.println("짹짹");
   }
   void walk() {
      System.out.println("걷다");   
   }
   @Override
   public void serving() {
      System.out.println("새 서빙");
   }
}

public class TestApp2 {

   public static void start(Animal a) {
      a.sound();
      a.walk();
   }
   
   public static void main(String[] args) {   
      start(new Bird());
   }
}