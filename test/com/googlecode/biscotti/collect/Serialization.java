package com.googlecode.biscotti.collect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

final public class Serialization {

	public static byte[] writeObject(Object o) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();
		return baos.toByteArray();
	}

	public static Object readObject(byte[] bytes) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object o = ois.readObject();
		ois.close();
		return o;
	}

	public static void writeObject(Object o, String f) throws Exception {
		FileOutputStream fos = new FileOutputStream(f);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(o);
		oos.close();
	}

	public static Object readObject(String f) throws Exception {
		FileInputStream fis = new FileInputStream(f);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object o = ois.readObject();
		ois.close();
		return o;
	}

}