package br.com.alurafood.pagamentos.service;

import br.com.alurafood.pagamentos.dto.PagamentoDto;
import br.com.alurafood.pagamentos.http.PedidoClient;
import br.com.alurafood.pagamentos.model.Pagamento;
import br.com.alurafood.pagamentos.model.Status;
import br.com.alurafood.pagamentos.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PagamentoService {
    private final PagamentoRepository pagamentoRepository;
    private final  ModelMapper modelMapper;
    private final PedidoClient pedido;

    public Page<PagamentoDto> obterTodos(Pageable paginacao){
        return pagamentoRepository
                .findAll(paginacao)
                .map(p -> modelMapper.map(p,PagamentoDto.class));
    }

    public PagamentoDto obterPorId(Long id){
        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        return modelMapper.map(pagamento,PagamentoDto.class);
    }

    public PagamentoDto criarPagamento(PagamentoDto dto){
        Pagamento pagamento = modelMapper.map(dto,Pagamento.class);
        pagamento.setStatus(Status.CRIADO);
        pagamentoRepository.save(pagamento);
        return modelMapper.map(pagamento,PagamentoDto.class);
    }
    public PagamentoDto atualizarPagamento(PagamentoDto dto, Long id){

        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());

        pagamento.setValor(dto.getValor());
        pagamento.setNome(dto.getNome());
        pagamento.setNumero(dto.getNumero());
        pagamento.setExpiracao(dto.getExpiracao());
        pagamento.setCodigo(dto.getCodigo());
        pagamento.setFormaDePagamento(dto.getFormaDePagamento());
        pagamento.setPedidoId(dto.getPedidoId());

        pagamentoRepository.save(pagamento);

        return modelMapper.map(pagamento, PagamentoDto.class);
    }
    public void deletarPagamento(Long id){
        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        pagamentoRepository.delete(pagamento);
    }

    public void confirmarPagamento(Long id){
        Optional<Pagamento> pagamento = pagamentoRepository.findById(id);

        if(pagamento.isEmpty()){
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO);
        pagamentoRepository.save(pagamento.get());

        pedido.atualizaPagamento(pagamento.get().getPedidoId());
    }

    public void alteraStatus(Long id) {
        Optional<Pagamento> pagamento = pagamentoRepository.findById(id);

        if (pagamento.isEmpty()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);

        pagamentoRepository.save(pagamento.get());
    }
}
