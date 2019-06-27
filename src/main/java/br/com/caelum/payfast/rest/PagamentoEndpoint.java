package br.com.caelum.payfast.rest;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.caelum.payfast.modelo.Pagamento;
import br.com.caelum.payfast.modelo.Transacao;

@RestController
@RequestMapping("/v1/pagamentos")
public class PagamentoEndpoint {

	private static Map<Integer, Pagamento> REPO = new HashMap<>();
	private static Integer idPagamento = 1;

	public PagamentoEndpoint() {
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

	@PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> criarPagamento(@RequestBody Transacao transacao)
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
		return ResponseEntity.created(new URI("/v1/pagamento/" + pagamento.getId()))
				.body(pagamento);
	}

	private Integer nextId() {
		return idPagamento++;
	}

	@PutMapping(value="/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> confirmarPagamento(@PathVariable("id") Integer pagamentoId) throws URISyntaxException {
		
		Pagamento pagamento = REPO.get(pagamentoId);
		ResponseEntity<?> response = null;
		
		if ( pagamento == null ) {
			response = ResponseEntity.notFound().build();
		} else {
			pagamento.comStatusConfirmado();
			response = ResponseEntity.ok().body(pagamento);
		}
		
		return response;
	}

	@GetMapping(value="/{id}", produces={ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<Pagamento> buscaPagamento(@PathVariable("id") Integer id) {
		return new ResponseEntity<Pagamento>(REPO.get(id),HttpStatus.OK);
	}

}
