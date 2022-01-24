package com.propellum.oms.services;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.expression.spel.ast.Identifier;

public class test
{

	public static String replace(String text,String toReplace, String replacement)
	{
		int indexOf = text.indexOf(toReplace);
		if(indexOf!=-1)
		{
			String changedText = text.substring(indexOf+toReplace.length());
			text = text.substring(0,indexOf+toReplace.length()).replace(toReplace, replacement);
			text += replace(changedText,toReplace,replacement);
		}
		return text;
	}
	public static void main(String[] args)
	{
		
		try {
		String key = "https://jobs.wayne.edu/applicants/Central?delegateParameter=applicantPostingSearchDelegate&actionParameter=getJobDetail&rowId=554676&c=qPxWh7C9yGG%2F6rSYameuqQ%3D%3D&pageLoadIdRequestKey=1565117684934&functionalityTableName=8192&windowTimestamp=null";
		System.out.println("Before Decode: "+key);
		key = StringEscapeUtils.unescapeHtml4(URLDecoder.decode(key,"UTF-8"));
		System.out.println("After Decode: "+key);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		System.exit(0);
		
		Calendar thisMonth = Calendar.getInstance();
		Calendar lastMonth = Calendar.getInstance();
		lastMonth.add(Calendar.MONTH, -1);
//		c.add(Calendar.MONTH, -1);
		int start_month = Integer.valueOf(new SimpleDateFormat("MM").format(lastMonth.getTime()));
		int end_month = Integer.valueOf(new SimpleDateFormat("MM").format(thisMonth.getTime()));
		int start_year = Integer.valueOf(new SimpleDateFormat("yyyy").format(lastMonth.getTime()));
		int end_year = Integer.valueOf(new SimpleDateFormat("yyyy").format(thisMonth.getTime()));
		int start_day = 16;
		int end_day = 15;
		
//		Pattern p = Pattern.compile("");
//		String latin = "H\\u00f4te d\\u0027accueil H\\/F";
//		String newJobUrl = "https://careers.vidanthealth.com/Careers/Job-Listings/###job###";
//		String identifire = "RN$#%$@?-Home_Health-Weekends-Potential_";
//		
//		identifire = replace(identifire,"$","\\$");
//		System.out.println(identifire);
//		newJobUrl = newJobUrl.replaceAll("###" + "job" + "###", identifire);
//		
//		System.out.println(newJobUrl);
//		System.out.println(StringEscapeUtils.unescapeJava(latin));
		
//		Set<String> set = new HashSet<String>();
//		set.add("abc");
//		System.out.println(set.toString());
//		
//		
//		Calendar calendar = Calendar.getInstance();
//		System.out.println((long) ((60 * 60) - (calendar.get(Calendar.SECOND) + (calendar.get(Calendar.MINUTE) * 60))) * 1000);
		/*
		String md5Password = MD5("oms@123");
		System.out.println(md5Password);
*/
	}
	/*private static String MD5(String md5) {
		   try {
		        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		        byte[] array = md.digest(md5.getBytes());
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < array.length; ++i) {
		          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
		       }
		        return sb.toString();
		    } catch (java.security.NoSuchAlgorithmException e) {
		    }
		    return null;
	}*/
	

}
