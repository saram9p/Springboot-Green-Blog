package com.cos.blogapp.util;

public class Script {

	public static String back(String msg) { // UX를 위해 사용
			StringBuilder sb = new StringBuilder();
			sb.append("<script>");
			sb.append("alert('"+msg+"');");
			sb.append("history.back();");
			sb.append("</script>");
			
			return sb.toString();
	}
	
	public static String href(String path) { // 함수 따로 뺸 이유는 많이 사용할려고, 재활용
		StringBuilder sb = new StringBuilder();
		sb.append("<script>");
		sb.append("location.href='"+path+"';");
		sb.append("</script>");
		
		return sb.toString();
}
	
	public static String href(String path, String msg) { // 오버로딩, 개발자한테 좋다, 매개변수가 적을 때 좋다, 매개변수 순서 맞춰줘야 한다
		StringBuilder sb = new StringBuilder();
		sb.append("<script>");
		sb.append("alert('"+msg+"');");
		sb.append("location.href='"+path+"';");
		sb.append("</script>");
		
		return sb.toString();
}
	
}
