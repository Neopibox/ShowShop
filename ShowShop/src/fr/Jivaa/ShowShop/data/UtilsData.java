package fr.Jivaa.ShowShop.data;

import java.security.MessageDigest;

public class UtilsData
{
	public static String convertToHex(byte[] data)
	{
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++)
		{
			int halfbyte = data[i] >>> 4 & 0xF;
			int two_halfs = 0;
			do
			{
				if ((halfbyte >= 0) && (halfbyte <= 9))
					buf.append((char)(48 + halfbyte));
				else
					buf.append((char)(97 + (halfbyte - 10)));
				
				halfbyte = data[i] & 0xF;
			}
			while (two_halfs++ < 1);
		}
		return buf.toString();
	}
	
	public static String sha1(String text)
	{
		byte[] sha1hash = new byte[40];
		
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
			sha1hash = md.digest();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return convertToHex(sha1hash);
	}
}
