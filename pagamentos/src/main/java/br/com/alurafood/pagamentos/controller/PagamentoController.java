package br.com.alurafood.pagamentos.controller;

import br.com.alurafood.pagamentos.dto.PagamentoDto;
import br.com.alurafood.pagamentos.service.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {
    private final PagamentoService pagamentoService;
    private final RabbitTemplate rabbitTemplate;

    @GetMapping
    public Page<PagamentoDto> listar (@PageableDefault Pageable paginacao){
        return pagamentoService.obterTodos(paginacao);
    }
    @GetMapping("{id}")
    public ResponseEntity<PagamentoDto> obterPorId(@PathVariable @NotNull Long id){
        PagamentoDto dto = pagamentoService.obterPorId(id);
        return ResponseEntity.ok(dto);
    }
    @PostMapping
    public ResponseEntity<PagamentoDto> criar(@RequestBody @Valid PagamentoDto dto, UriComponentsBuilder uriBuilder){
        PagamentoDto pagamento = pagamentoService.criarPagamento(dto);
        URI endereco = uriBuilder.path("/pagamentos/{id}").buildAndExpand(pagamento.getId()).toUri();
        
        rabbitTemplate.convertAndSend("pagamentos.ex", "",pagamento);
        return ResponseEntity.created(endereco).body(pagamento);
    }
    @PutMapping("{id}")
    public ResponseEntity<PagamentoDto> atualizar(@RequestBody @Valid PagamentoDto dto, @PathVariable @NotNull Long id){
        PagamentoDto atualizado = pagamentoService.atualizarPagamento(dto,id);
        return ResponseEntity.ok(atualizado);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletar (@PathVariable @NotNull Long id){
        pagamentoService.deletarPagamento(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(
            name = "atualizaPedido",
            fallbackMethod = "pagamentoAutorizadoComIntegracaoPendente"
    )
    public void confirmarPagamento(@PathVariable @NotNull Long id){
        pagamentoService.confirmarPagamento(id);
    }

    public void pagamentoAutorizadoComIntegracaoPendente(
            Long id,
            Throwable e
    ){
        pagamentoService.alteraStatus(id);
    }
}
