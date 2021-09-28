package accenturebank.com.accentureBank.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import accenturebank.com.accentureBank.entities.Agencia;
import accenturebank.com.accentureBank.entities.Cliente;
import accenturebank.com.accentureBank.entities.ContaCorrente;
import accenturebank.com.accentureBank.entities.Extrato;
import accenturebank.com.accentureBank.exceptions.AgenciaNotFoundException;
import accenturebank.com.accentureBank.exceptions.ContaCorrenteNotFoundException;
import accenturebank.com.accentureBank.model.ContaCorrenteModel;
import accenturebank.com.accentureBank.repositories.AgenciaRepository;
import accenturebank.com.accentureBank.repositories.ContaCorrenteRepository;
import accenturebank.com.accentureBank.repositories.ExtratoRepository;

@Service
public class ContaCorrenteService {
	@Autowired
	ContaCorrenteRepository contaCorrenteRepository;
	@Autowired
	ClienteService clienteService;
	@Autowired
	AgenciaService agenciaService;
	@Autowired
	AgenciaRepository agenciaRepository;
	@Autowired
	ExtratoRepository extratoRepository;
	@Autowired
	ExtratoService extratoContaCorrenteService;

	public List<ContaCorrente> getAllContasCorrentes() {
		List<ContaCorrente> contasCorrentes = new ArrayList<ContaCorrente>();
		contaCorrenteRepository.findAll().forEach(contaCorrente -> contasCorrentes.add(contaCorrente));
		return contasCorrentes;
	}

	public ContaCorrente getIdContaCorrente(Long id) throws ContaCorrenteNotFoundException {

		// VALIDANDO SE A CONTA EXISTE
		Optional<ContaCorrente> contaCorrenteReturn = contaCorrenteRepository.findById(id);
		if (contaCorrenteReturn.isEmpty()) {
			throw new ContaCorrenteNotFoundException("Conta Corrente não encontrada");
		}
		return contaCorrenteReturn.get();
	}

	public double getSaldoByIdCliente(long id) throws ContaCorrenteNotFoundException {

		// BUSCAR SALDO PELO O ID DO CLIENTE
		ContaCorrente getSaldoByIdCliente = getAllContasCorrentes().stream()
				.filter(conta -> conta.getCliente().getId() == id).findFirst().get();

		double saldo = getSaldoByIdCliente.getContaCorrenteSaldo();

		return saldo;
	}

	public String Saque(Long id, double valorSaque) throws ContaCorrenteNotFoundException {

		// VALIDANDO SE A CONTA EXISTE
		validate(id);

		// PEGAR O SALDO DA CONTA E CALCULAR O SAQUE
		double contaCorrenteSaldo = contaCorrenteRepository.findById(id).get().getContaCorrenteSaldo();
		double resultadoSaque = contaCorrenteSaldo - valorSaque;

		if (contaCorrenteSaldo >= valorSaque) {
			operacaoContaCorrente(id, resultadoSaque, valorSaque, "Saque");
			return "Saque efetuado";
		} else {
			return "Saldo insuficiente";
		}

	}

	public String Depositar(Long id, double valorDeposito) throws ContaCorrenteNotFoundException {

		// VALIDANDO SE A CONTA EXISTE
		validate(id);
		// PEGAR O SALDO DA CONTA E CALCULAR O DEPOSITO
		double contaCorrenteSaldo = contaCorrenteRepository.findById(id).get().getContaCorrenteSaldo();
		double resultadoDeposito = contaCorrenteSaldo - valorDeposito;

		if (valorDeposito > 0) {
			
			//DEPOSITO NA CONTA
			operacaoContaCorrente(id, resultadoDeposito, valorDeposito, "Depósito");
			return "Deposito efetuado";
		} else {
			return "Valor invalido para deposito";
		}

	}

	public ContaCorrente saveOrUpdate(ContaCorrenteModel contaCorrenteModel) throws AgenciaNotFoundException {
		Cliente clienteRetorno = clienteService.getClienteById(contaCorrenteModel.getIdCliente().getId());
		Agencia agenciaRetorno = agenciaService.getAgenciaById(contaCorrenteModel.getIdAgencia().getId());

		Cliente cliente = new Cliente(contaCorrenteModel.getIdCliente().getId(), null, null, null);
		Agencia agencia = new Agencia(contaCorrenteModel.getIdAgencia().getId(), null, null, null);

		ContaCorrente contaCorrente = new ContaCorrente(null, agencia, gerarNumeroContaCorrente(), 0, cliente);
		ContaCorrente contaCorrenteRetorno = contaCorrenteRepository.save(contaCorrente);

		contaCorrenteRetorno.setAgencia(agenciaRetorno);
		contaCorrenteRetorno.setCliente(clienteRetorno);

		return contaCorrenteRetorno;

	}

	public void operacaoContaCorrente(long id, double resultadoOperacao, double valorOperacao, String operacao) {
		Long contaCorrenteId = contaCorrenteRepository.getById(id).getId();
		Agencia agenciaContaCorrente = contaCorrenteRepository.getById(id).getAgencia();
		String numeroContaCorrente = contaCorrenteRepository.getById(id).getContaCorrenteNumero();
		Cliente clienteContaCorrente = contaCorrenteRepository.getById(id).getCliente();

		ContaCorrente contaCorrente = new ContaCorrente(contaCorrenteId, agenciaContaCorrente, numeroContaCorrente,
				resultadoOperacao, clienteContaCorrente);

		contaCorrenteRepository.save(contaCorrente);

		LocalDateTime data = LocalDateTime.now();
		Extrato extratoContaCorrente = new Extrato(null, data, null, contaCorrente);
		extratoRepository.save(extratoContaCorrente);
	}

	public ContaCorrente getContaCorrenteByCliente(Cliente cliente) {
		return contaCorrenteRepository.findByCliente(cliente);
	}

	private String gerarNumeroContaCorrente() {
		Integer size = this.getAllContasCorrentes().size();
		int numero = size + 1;
		String numeroContaCorrente = Integer.toString(numero);
		return numeroContaCorrente;
	}
	
	public void validate(Long id) {
		Optional<ContaCorrente> contaCorrenteReturn = contaCorrenteRepository.findById(id);
		if (contaCorrenteReturn.isEmpty()) {
			throw new ContaCorrenteNotFoundException("Conta Corrente não encontrada");
	}
}}
