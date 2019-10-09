package com.ceiba.controlador;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.ceiba.comando.ComandoContenedor;
import com.ceiba.testdatabuilder.ComandoContenedorTestBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ControladorHistorialAlmacenamientoTest {
	private static final String CODIGO = "TR0978";
	private static final String CODIGO2 = "PO0712";
	private static final String URLPOST = "/api/bodega/registrarHistorial";

	@Autowired
	private MockMvc mockMvc;	
	
	@Autowired
	private ObjectMapper mapper;
	
	@Test
	public void metodoCrearHistorialAlmacenamient() throws Exception{
		
		//Arrange
		ComandoContenedor contenedor = new ComandoContenedorTestBuilder().conCodigo(CODIGO).conMercancia("Peras").conPeso("20t").build();
		
		//Act //Assert
		mockMvc.perform(MockMvcRequestBuilders
				.post(URLPOST)
				.content(mapper.writeValueAsString(contenedor)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());	
	}
	
	@Test
	public void metodoListarHistorialAlmacenamiento() throws Exception {
		//Act //Assert
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/bodega/obtenerContenedores")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$[1].contenedor.codigo", is(CODIGO)));
	}
	

	@Test
	public void metodoSalidaContenedor() throws Exception{
		//Arrange
		ComandoContenedor comandoContenedor2 = new ComandoContenedorTestBuilder().conCodigo(CODIGO2).build();
		
		//Act //Assert
		mockMvc.perform(MockMvcRequestBuilders
				.post(URLPOST)
				.content(mapper.writeValueAsString(comandoContenedor2)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/bodega/salidaContenedor/" + CODIGO2)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	public void metodoConsultarHistorialAlmacenamiento() throws Exception {
		//Arrange
		ComandoContenedor comandoContenedor1 = new ComandoContenedorTestBuilder().build();
		
		//Act //Assert
		mockMvc.perform(MockMvcRequestBuilders
				.post(URLPOST)
				.content(mapper.writeValueAsString(comandoContenedor1)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/bodega/obtenerContenedor/" + comandoContenedor1.getCodigo())
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());
	}
	
	@Test
	public void validarManejadorDeErrorEnExcepcion() throws Exception{
		//Arrange
				ComandoContenedor contenedor = new ComandoContenedorTestBuilder().conCodigo(null).conMercancia("Peras").conPeso("20t").build();
				
				//Act //Assert
				mockMvc.perform(MockMvcRequestBuilders
						.post(URLPOST)
						.content(mapper.writeValueAsString(contenedor)).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());	
	}

	
	@Test
	public void validarManejadorDeErrorInternal() throws Exception{
				//Act //Assert				
				mockMvc.perform(MockMvcRequestBuilders
						.put("/api/bodega/salidaContenedor/" + null)
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isInternalServerError());
	}

}
