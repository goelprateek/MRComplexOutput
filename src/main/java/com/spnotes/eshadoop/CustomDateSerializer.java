package com.spnotes.eshadoop;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateSerializer extends JsonSerializer<Date>{
	 private static SimpleDateFormat formatter =
	 new SimpleDateFormat("YYYY-MM-dd");

	@Override
	public void serialize(Date value, JsonGenerator jsonGenerator,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jsonGenerator.writeString(formatter.format(value));
	}
	

}
