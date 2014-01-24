package br.com.caelum.payfast.rest;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.caelum.payfast.modelo.Pagamento;
import br.com.caelum.payfast.modelo.Transacao;

@Path("/v1/pagamento")
public class PagamentoResource {

	private static Map<Integer, Pagamento> REPO = new HashMap<>();
	private static Integer idPagamento = 1;

	public PagamentoResource() {
		if (REPO.isEmpty()) {
			criarPagamantoDefault();
		}
	}

	private void criarPagamantoDefault() {
		Pagamento pagamento = new Pagamento();
		pagamento.setId(nextId());
		pagamento.setValor(BigDecimal.TEN);
		pagamento.comStatusCriado();
		REPO.put(pagamento.getId(), pagamento);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response criarPagamento(Transacao transacao)
			throws URISyntaxException {

		if (REPO.size() > 1000) {
			REPO.clear();
			idPagamento = 0;
			criarPagamantoDefault();
		}
		
		Pagamento pagamento = new Pagamento();
		pagamento.setId(nextId());
		pagamento.setValor(transacao.getValor());
		pagamento.comStatusCriado();

		REPO.put(pagamento.getId(), pagamento);

		System.out.println("PAGAMENTO CRIADO " + pagamento);
		return Response.created(new URI("/v1/pagamento/" + pagamento.getId()))
				.entity(pagamento).type(MediaType.APPLICATION_JSON).build();
	}

	private Integer nextId() {
		return idPagamento++;
	}

	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response confirmarPagamento(@PathParam("id") Integer pagamentoId) throws URISyntaxException {
		
		Pagamento pagamento = REPO.get(pagamentoId);
		Response response = null;
		
		if ( pagamento == null ) {

			response = Response.status(Status.NOT_FOUND).build();
		
		} else {
			pagamento.comStatusConfirmado();
		
			response = Response.ok(new URI("/v1/pagamento/" + pagamento.getId()))
					.entity(pagamento).type(MediaType.APPLICATION_JSON).build();
		
			System.out.println("Pagamento confirmado: " + pagamento);

		}
		
		return response;
	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Pagamento buscaPagamento(@PathParam("id") Integer id) {
		return REPO.get(id);
	}

}
