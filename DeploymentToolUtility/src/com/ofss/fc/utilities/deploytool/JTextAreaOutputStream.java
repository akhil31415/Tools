package com.ofss.fc.utilities.deploytool;

import java.io.IOException;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class JTextAreaOutputStream extends OutputStream{
	private final JTextArea destination;
	
	public JTextAreaOutputStream(JTextArea destination) {
		if (destination ==null)
			throw new IllegalArgumentException("destination IS NULL");
		this.destination=destination;
	}
	
	@Override
	public void write(byte[] buffer, int offset, int length) throws IOException{
		final String text = new String (buffer, offset, length);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				destination.append(text);
			}
		});
	}
	
	@Override
	public void write(int b) throws IOException{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				destination.append(String.valueOf((char)b));
				destination.setCaretPosition(destination.getDocument().getLength());
			}
		});
		
	}
	

	@Override
	public void write_ulonglong_array(long[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public InputStream create_input_stream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write_Object(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_TypeCode(TypeCode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_any(Any arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_boolean(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_boolean_array(boolean[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_char(char arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_char_array(char[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_double(double arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_double_array(double[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_float(float arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_float_array(float[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_long(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_long_array(int[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_longlong(long arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_longlong_array(long[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_octet(byte arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_octet_array(byte[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_short(short arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_short_array(short[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_string(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_ulong(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_ulong_array(int[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_ulonglong(long arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_ushort(short arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_ushort_array(short[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_wchar(char arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_wchar_array(char[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write_wstring(String arg0) {
		// TODO Auto-generated method stub
		
	}

}