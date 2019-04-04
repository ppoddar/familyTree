package com.familytree.web;

import java.io.IOException;
import java.io.Writer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Serde {
	ObjectMapper mapper;
	
	public Serde() {
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);

	}
	public <T> void serialize(Writer writer, T value) {
		
		try {
			mapper.writeValue(writer, value);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
